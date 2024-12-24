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
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT,
                $COLUMN_PRICE REAL, -- Menggunakan REAL untuk tipe harga
                $COLUMN_QUANTITY INTEGER, -- Menggunakan INTEGER untuk quantity
                $COLUMN_IMAGE_URL TEXT
            )
        """
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun addItemToCart(name: String, price: Double, quantity: Int, imageUrl: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, name)
            put(COLUMN_PRICE, price) // Harga sebagai Double
            put(COLUMN_QUANTITY, quantity)
            put(COLUMN_IMAGE_URL, imageUrl)
        }
        return db.insert(TABLE_NAME, null, values)
    }


    fun getCartItems(): List<ItemCart> {
        val db = readableDatabase
        val cursor = db.query(TABLE_NAME, null, null, null, null, null, null)
        val items = mutableListOf<ItemCart>()

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
                val price = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRICE)) // Menggunakan Double untuk harga
                val imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_URL))
                val quantity = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_QUANTITY)) // Menggunakan Int untuk quantity

                items.add(ItemCart(id, name, price, imageUrl, quantity))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return items
    }

    fun deleteItemFromCart(itemId: String) {
        val db = writableDatabase
        db.delete(TABLE_NAME, "$COLUMN_ID = ?", arrayOf(itemId))
    }

    fun clearCart() {
        val db = writableDatabase
        db.delete(TABLE_NAME, null, null)
    }
}

data class ItemCart(
    val id: String,
    val name: String,
    val price: Double, // Gunakan Double untuk harga
    val imageUrl: String,
    var quantity: Int // Gunakan Int untuk quantity
)
