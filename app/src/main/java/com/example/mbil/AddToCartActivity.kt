package com.example.mbil

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class AddToCartActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var cartAdapter: CartAdapter
    private lateinit var totalTextView: TextView
    private lateinit var checkoutButton: Button
    private var cartItems = mutableListOf<CartItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_to_cart)

        initUI()
        setupRecyclerView()
        getIntentData()

        checkoutButton.setOnClickListener {
            if (cartItems.isEmpty()) {
                Toast.makeText(this, "Your cart is empty!", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, PaymentActivity::class.java)
                intent.putParcelableArrayListExtra("CART_ITEMS", ArrayList(cartItems))
                startActivity(intent)
            }
        }

        updateTotal()
    }

    private fun initUI() {
        recyclerView = findViewById(R.id.recyclerViewCart)
        totalTextView = findViewById(R.id.textViewTotal)
        checkoutButton = findViewById(R.id.buttonCheckout)
    }

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(cartItems) { item ->
            removeItemFromCart(item)
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = cartAdapter
    }

    private fun getIntentData() {
        val itemName = intent.getStringExtra("ITEM_NAME")
        val itemPrice = intent.getStringExtra("ITEM_PRICE")

        Log.d(
            "AddToCartActivity",
            "Received item: $itemName, $itemPrice"
        )

        if (itemName != null && itemPrice != null) {
            addItemToCart(itemName, itemPrice)
        }
    }

    private fun addItemToCart(name: String, price: String) {
        // Provide a default value (null) for the imageUrl if not available
        val newItem = CartItem(name, price, 1, null) // Default imageUrl = null
        cartItems.add(newItem)
        cartAdapter.notifyDataSetChanged()
        updateTotal()
    }

    private fun removeItemFromCart(item: CartItem) {
        cartItems.remove(item)
        cartAdapter.notifyDataSetChanged()
        updateTotal()
    }

    private fun updateTotal() {
        val total = cartItems.sumOf { item ->
            val price = item.price.replace("Rp", "").replace(",", "").toDoubleOrNull() ?: 0.0
            price * item.quantity
        }
        totalTextView.text = "Total: Rp %.2f".format(total)
    }
}
