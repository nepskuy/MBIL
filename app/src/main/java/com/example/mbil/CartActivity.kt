package com.example.mbil

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.NumberFormat
import java.util.*
import android.widget.Button
import android.widget.TextView

class CartActivity : AppCompatActivity() {

    private lateinit var cartDatabaseHelper: CartDatabaseHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var cartAdapter: CartAdapter
    private lateinit var cartItems: MutableList<ItemCart> // Menggunakan ItemCart sebagai data model

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        // Inisialisasi database dan RecyclerView
        cartDatabaseHelper = CartDatabaseHelper(this)
        recyclerView = findViewById(R.id.recyclerViewCart)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Mengambil data dari database
        cartItems = cartDatabaseHelper.getCartItems().toMutableList()

        cartAdapter = CartAdapter(cartItems) { cartItem ->
            cartDatabaseHelper.deleteItemFromCart(cartItem.id)
            cartAdapter.removeItem(cartItem)
            updateTotalPrice()
            Toast.makeText(this, "${cartItem.name} removed from cart", Toast.LENGTH_SHORT).show()
        }

        recyclerView.adapter = cartAdapter


        // Update total harga saat pertama kali
        updateTotalPrice()

        // Tombol Clear Cart
        findViewById<Button>(R.id.buttonClearCart).setOnClickListener {
            cartDatabaseHelper.clearCart()
            cartItems.clear()
            updateTotalPrice()
            cartAdapter.notifyDataSetChanged()
            Toast.makeText(this, "Cart cleared", Toast.LENGTH_SHORT).show()
        }

        // Tombol Checkout
        findViewById<Button>(R.id.buttonCheckout).setOnClickListener {
            if (cartItems.isNotEmpty()) {
                // Arahkan ke halaman pembayaran atau lakukan proses lainnya
                Toast.makeText(this, "Checkout successful", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Cart is empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Fungsi untuk menghitung total harga dan memperbarui UI
    private fun updateTotalPrice() {
        val totalPrice = cartItems.sumOf { it.price * it.quantity }
        findViewById<TextView>(R.id.textViewTotalPrice).text = formatPrice(totalPrice)
    }

    // Fungsi untuk format harga ke format Rupiah
    private fun formatPrice(price: Double): String {
        val localeID = Locale("id", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
        return numberFormat.format(price).replace("Rp", "Rp.").replace(",00", "")
    }
}
