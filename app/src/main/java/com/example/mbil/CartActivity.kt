package com.example.mbil

import ItemCart
import android.content.Intent
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

        loadCartItems()

        // Tombol Clear Cart
        findViewById<Button>(R.id.buttonClearCart).setOnClickListener {
            clearCart()
        }

        // Tombol Checkout
        findViewById<Button>(R.id.buttonCheckout).setOnClickListener {
            if (cartItems.isNotEmpty()) {
                // Arahkan ke halaman PaymentActivity
                val intent = Intent(this, PaymentActivity::class.java)

                // Kirim total harga ke PaymentActivity
                val totalPrice = cartItems.sumOf { it.price * it.quantity }
                intent.putExtra("TOTAL_PRICE", totalPrice)

                startActivity(intent)
            } else {
                Toast.makeText(this, "Cart is empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Memuat ulang item keranjang setelah kembali dari PaymentActivity
        loadCartItems()
    }

    private fun loadCartItems() {
        cartItems = cartDatabaseHelper.getCartItems().toMutableList()
        cartAdapter = CartAdapter(
            cartItems,
            onRemoveClick = { cartItem ->
                cartDatabaseHelper.deleteItemFromCart(cartItem.id)
                cartAdapter.removeItem(cartItem)
                updateTotalPrice()
                Toast.makeText(this, "${cartItem.name} removed from cart", Toast.LENGTH_SHORT).show()
            },
            onQuantityChanged = { cartItem ->
                cartDatabaseHelper.updateQuantity(cartItem.id, cartItem.quantity) // Memperbarui quantity di database
                updateTotalPrice() // Perbarui total harga
            }
        )
        recyclerView.adapter = cartAdapter
        updateTotalPrice()
    }

    private fun clearCart() {
        cartDatabaseHelper.clearCart()
        cartItems.clear()
        updateTotalPrice()
        cartAdapter.notifyDataSetChanged()
        Toast.makeText(this, "Cart cleared", Toast.LENGTH_SHORT).show()
    }

    // Fungsi untuk menghitung total harga dan memperbarui UI
    private fun updateTotalPrice() {
        val totalPrice = cartItems.sumOf { it.price * it.quantity }
        findViewById<TextView>(R.id.textViewTotalPrice).text = formatPrice(totalPrice)

        if (cartItems.isEmpty()) {
            Toast.makeText(this, "Your cart is empty!", Toast.LENGTH_SHORT).show()
        }
    }

    // Fungsi untuk format harga ke format Rupiah
    private fun formatPrice(price: Double): String {
        val localeID = Locale("id", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
        return numberFormat.format(price).replace("Rp", "Rp.").replace(",00", "")
    }
}
