package com.example.mbil

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

class PaymentActivity : AppCompatActivity() {

    private lateinit var totalAmountTextView: TextView
    private lateinit var completePaymentButton: Button

    private var cartItems = mutableListOf<CartItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        // Menghubungkan elemen UI dengan ID
        totalAmountTextView = findViewById(R.id.textViewTotalAmount)
        completePaymentButton = findViewById(R.id.buttonCompletePayment)

        // Mendapatkan data cartItems dari Intent
        cartItems = intent.getParcelableArrayListExtra("CART_ITEMS") ?: mutableListOf()

        // Debugging: Check cartItems and their prices
        Log.d("PaymentActivity", "Received Cart Items: $cartItems")

        // Update total amount
        updateTotalAmount()

        // Handle tombol "Complete Payment" untuk menyelesaikan pembayaran
        completePaymentButton.setOnClickListener {
            // Proses pembayaran bisa dilakukan di sini
            // Misalnya, arahkan ke halaman konfirmasi pembayaran atau tampilkan pesan sukses
            // Contoh:
            // Toast.makeText(this, "Payment completed!", Toast.LENGTH_SHORT).show()
        }
    }

    // Fungsi untuk menghitung dan menampilkan total harga
    private fun updateTotalAmount() {
        val totalAmount = cartItems.sumOf { item ->
            val price = item.price.replace("Rp.", "").replace(",", "").toDoubleOrNull() ?: 0.0

            Log.d("PaymentActivity", "Item Price: ${item.price}, Parsed Price: $price, Quantity: ${item.quantity}")
            price * item.quantity
        }


        Log.d("PaymentActivity", "Total Amount (before formatting): $totalAmount")

        // Format total amount as "Rp. 15.000" (Indonesian currency format)
        totalAmountTextView.text = formatPrice(totalAmount)
    }

    private fun formatPrice(price: Double): String {
        // Format harga dengan pemisah ribuan dan tanpa angka desimal
        val numberFormat: NumberFormat = DecimalFormat("#,###")
        return "Total Amount: Rp. ${numberFormat.format(price)}"
    }

}
