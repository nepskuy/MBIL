package com.example.mbil

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.Locale

class CartActivity : AppCompatActivity() {

    private lateinit var cartDatabaseHelper: CartDatabaseHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var cartAdapter: CartAdapter
    private lateinit var cartItems: MutableList<ItemCart>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        // Initialize database helper and RecyclerView
        cartDatabaseHelper = CartDatabaseHelper(this)
        recyclerView = findViewById(R.id.recyclerViewCart)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Get cart items from database
        cartItems = cartDatabaseHelper.getCartItems().toMutableList()

        // Set up the adapter with the cart items
        cartAdapter = CartAdapter(cartItems, { cartItem ->
            // onRemoveClick callback
            cartDatabaseHelper.deleteItemFromCart(cartItem.id)
            cartAdapter.removeItem(cartItem)  // Remove from the list in the adapter
            updateTotalPrice()  // Update total price after item removal
            Toast.makeText(this, "${cartItem.name} removed from cart", Toast.LENGTH_SHORT).show()
        }, { cartItem ->
            // onQuantityChanged callback
            cartDatabaseHelper.updateCartItem(cartItem)  // Update the cart item after quantity change
            updateTotalPrice()  // Update total price after quantity change
        })

        recyclerView.adapter = cartAdapter

        // Update the total price when the activity is created
        updateTotalPrice()

        // Handle Clear Cart button click
        findViewById<Button>(R.id.buttonClearCart).setOnClickListener {
            cartDatabaseHelper.clearCart()
            cartItems.clear()  // Clear the list of cart items
            updateTotalPrice()  // Update total price after clearing the cart
            cartAdapter.notifyDataSetChanged()  // Notify the adapter to refresh the list
            Toast.makeText(this, "Cart cleared", Toast.LENGTH_SHORT).show()
        }

        // Handle Checkout button click
        findViewById<Button>(R.id.buttonCheckout).setOnClickListener {
            if (cartItems.isNotEmpty()) {
                // Calculate the total price
                val totalPrice = cartItems.sumOf { it.price * it.quantity }

                // Proceed to PaymentActivity and pass the total price
                val intent = Intent(this, PaymentActivity::class.java)
                intent.putExtra("TOTAL_PRICE", totalPrice)  // Pass the total price as an extra
                startActivity(intent)  // Start PaymentActivity
            } else {
                // Show message if cart is empty
                Toast.makeText(this, "Cart is empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Function to update the total price of the cart
    private fun updateTotalPrice() {
        val totalPrice = cartItems.sumOf { it.price * it.quantity }
        findViewById<TextView>(R.id.textViewTotalPrice).text = formatPrice(totalPrice)
    }

    // Function to format the price in Rupiah format
    private fun formatPrice(price: Double): String {
        val localeID = Locale("id", "ID")
        val numberFormat = java.text.NumberFormat.getCurrencyInstance(localeID)
        return numberFormat.format(price).replace("Rp", "Rp.").replace(",00", "")
    }
}
