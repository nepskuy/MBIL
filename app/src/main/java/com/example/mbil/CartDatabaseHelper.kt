package com.example.mbil

import ItemCart
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
                $COLUMN_NAME TEXT NOT NULL,
                $COLUMN_PRICE REAL NOT NULL,
                $COLUMN_QUANTITY INTEGER NOT NULL,
                $COLUMN_IMAGE_URL TEXT
            )
        """
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    // Fungsi untuk menambahkan item ke keranjang
    fun addItemToCart(name: String, price: Double, quantity: Int, imageUrl: String) {
        val db = writableDatabase
        val cursor = db.query(
            TABLE_NAME,
            null,
            "$COLUMN_NAME = ? AND $COLUMN_PRICE = ? AND $COLUMN_IMAGE_URL = ?",
            arrayOf(name, price.toString(), imageUrl),
            null,
            null,
            null
        )

        if (cursor.moveToFirst()) {
            // Jika item sudah ada, perbarui kuantitas
            val existingQuantity = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_QUANTITY))
            val newQuantity = existingQuantity + quantity

            val values = ContentValues().apply {
                put(COLUMN_QUANTITY, newQuantity)
            }

            db.update(
                TABLE_NAME,
                values,
                "$COLUMN_NAME = ? AND $COLUMN_PRICE = ? AND $COLUMN_IMAGE_URL = ?",
                arrayOf(name, price.toString(), imageUrl)
            )
        } else {
            // Jika item belum ada, tambahkan sebagai item baru
            val values = ContentValues().apply {
                put(COLUMN_NAME, name)
                put(COLUMN_PRICE, price)
                put(COLUMN_QUANTITY, quantity)
                put(COLUMN_IMAGE_URL, imageUrl)
            }
            db.insert(TABLE_NAME, null, values)
        }

        cursor.close()
        db.close() // Tutup database
    }

    // Fungsi untuk mengambil semua item dari keranjang
    fun getCartItems(): List<ItemCart> {
        val db = readableDatabase
        val cursor = db.query(TABLE_NAME, null, null, null, null, null, null)
        val items = mutableListOf<ItemCart>()

        cursor.use {
            while (it.moveToNext()) {
                val id = it.getString(it.getColumnIndexOrThrow(COLUMN_ID))
                val name = it.getString(it.getColumnIndexOrThrow(COLUMN_NAME))
                val price = it.getDouble(it.getColumnIndexOrThrow(COLUMN_PRICE))
                val quantity = it.getInt(it.getColumnIndexOrThrow(COLUMN_QUANTITY))
                val imageUrl = it.getString(it.getColumnIndexOrThrow(COLUMN_IMAGE_URL))

                items.add(ItemCart(id, name, price, imageUrl, quantity))
            }
        }
        db.close() // Tutup database
        return items
    }

    // Fungsi untuk memperbarui jumlah item dalam keranjang
    fun updateQuantity(itemId: String, newQuantity: Int): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_QUANTITY, newQuantity)
        }
        val rowsAffected = db.update(TABLE_NAME, values, "$COLUMN_ID = ?", arrayOf(itemId))
        db.close() // Tutup database
        return rowsAffected > 0
    }

    // Fungsi untuk menghapus item dari keranjang
    fun deleteItemFromCart(itemId: String) {
        val db = writableDatabase
        db.delete(TABLE_NAME, "$COLUMN_ID = ?", arrayOf(itemId))
        db.close() // Tutup database
    }

    // Fungsi untuk menghapus semua item dari keranjang
    fun clearCart() {
        val db = writableDatabase
        db.delete(TABLE_NAME, null, null)
        db.close() // Tutup database
    }
}
