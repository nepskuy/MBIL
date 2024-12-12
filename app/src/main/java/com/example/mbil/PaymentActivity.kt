package com.example.mbil

import com.example.mbil.CartItem
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

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
            val price = item.price.replace("Rp.", "").toDoubleOrNull() ?: 0.0
            price * item.quantity
        }

        // Update total amount with "Rp." prefix
        totalAmountTextView.text = "Rp. %.2f".format(totalAmount)
    }
}
