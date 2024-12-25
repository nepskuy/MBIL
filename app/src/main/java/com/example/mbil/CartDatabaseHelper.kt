package com.example.mbil

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class CartDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "cart.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_NAME = "cart"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_PRICE = "price"
        const val COLUMN_QUANTITY = "quantity"
        const val COLUMN_IMAGE_URL = "image_url"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID TEXT PRIMARY KEY,  -- Changed to TEXT for String ID
                $COLUMN_NAME TEXT,
                $COLUMN_PRICE REAL, 
                $COLUMN_QUANTITY INTEGER, 
                $COLUMN_IMAGE_URL TEXT
            )
        """
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    // Function to add item to the cart or update quantity if item already exists
    fun addItemToCart(name: String, price: String, quantity: String, imageUrl: String): Long {
        val db = writableDatabase

        // Check if the item already exists in the cart
        val cursor = db.query(TABLE_NAME, null, "$COLUMN_NAME = ?", arrayOf(name), null, null, null)

        return if (cursor.moveToFirst()) {
            // If the item exists, update its quantity
            val existingId = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val existingQuantity = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_QUANTITY))

            val updatedQuantity = existingQuantity + quantity.toInt()

            val values = ContentValues().apply {
                put(COLUMN_QUANTITY, updatedQuantity)
            }

            db.update(TABLE_NAME, values, "$COLUMN_ID = ?", arrayOf(existingId))
            cursor.close()

            0 // No new row inserted
        } else {
            // If the item doesn't exist, insert it into the cart
            val values = ContentValues().apply {
                put(COLUMN_NAME, name)
                put(COLUMN_PRICE, price)
                put(COLUMN_QUANTITY, quantity)
                put(COLUMN_IMAGE_URL, imageUrl)
            }
            db.insert(TABLE_NAME, null, values)
        }
    }

    // Function to get all cart items
    fun getCartItems(): List<ItemCart> {
        val db = readableDatabase
        val cursor = db.query(TABLE_NAME, null, null, null, null, null, null)
        val items = mutableListOf<ItemCart>()

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID)) // ID as String
                val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
                val price = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRICE))
                val imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_URL))
                val quantity = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_QUANTITY))

                items.add(ItemCart(id, name, price, imageUrl, quantity))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return items
    }

    // Function to delete item from the cart
    fun deleteItemFromCart(itemId: String) {
        val db = writableDatabase
        db.delete(TABLE_NAME, "$COLUMN_ID = ?", arrayOf(itemId))
    }

    // Function to clear the entire cart
    fun clearCart() {
        val db = writableDatabase
        db.delete(TABLE_NAME, null, null) // Delete all rows in the cart table
    }

    // Function to update item quantity in the cart
    fun updateCartItem(cartItem: ItemCart) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_QUANTITY, cartItem.quantity)  // Update the quantity column
        }
        db.update(TABLE_NAME, values, "$COLUMN_ID = ?", arrayOf(cartItem.id)) // Use String ID
    }
}
