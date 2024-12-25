package com.example.mbil

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.AdapterView
import java.util.Locale

class PaymentActivity : AppCompatActivity() {

    private lateinit var cartDatabaseHelper: CartDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        // Initialize the database helper
        cartDatabaseHelper = CartDatabaseHelper(this)

        // Retrieve total price passed from CartActivity
        val totalPrice = intent.getDoubleExtra("TOTAL_PRICE", 0.0)  // Get the total price passed as an extra

        // Show the total price in a TextView
        val totalPriceTextView: TextView = findViewById(R.id.textViewTotalPrice)
        totalPriceTextView.text = "Total: ${formatPrice(totalPrice)}"  // Display the formatted total price

        // Spinner for selecting payment method
        val paymentMethods = arrayOf("Tunai", "Kartu Kredit/Debit", "Transfer Bank", "BCA OneKlik", "BRI Direct Debit")
        val bankOptions = arrayOf("Bank BCA", "SeaBank", "Bank Mandiri", "Bank BNI", "Bank BRI", "Bank Syariah Indonesia", "Bank Permata")

        val spinnerPaymentMethod: Spinner = findViewById(R.id.spinnerPaymentMethod)
        val spinnerBankOptions: Spinner = findViewById(R.id.spinnerBankOptions)

        // Set adapter for payment method spinner
        val paymentAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, paymentMethods)
        paymentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPaymentMethod.adapter = paymentAdapter

        // Set adapter for bank options spinner
        val bankAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, bankOptions)
        bankAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerBankOptions.adapter = bankAdapter

        // Handle payment method selection dynamically using OnItemSelectedListener
        spinnerPaymentMethod.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, selectedView: View?, position: Int, id: Long) {
                val selectedPaymentMethod = spinnerPaymentMethod.selectedItem.toString()

                // Hide all fields initially
                hideAllFields()

                // Show fields based on selected payment method
                when (selectedPaymentMethod) {
                    "Transfer Bank" -> {
                        findViewById<View>(R.id.textViewBankOptionsLabel).visibility = View.VISIBLE
                        findViewById<View>(R.id.spinnerBankOptions).visibility = View.VISIBLE
                        findViewById<View>(R.id.textViewVirtualAccount).visibility = View.VISIBLE
                    }
                    "Kartu Kredit/Debit" -> {
                        findViewById<View>(R.id.editTextCardNumber).visibility = View.VISIBLE
                        findViewById<View>(R.id.editTextExpiryDate).visibility = View.VISIBLE
                        findViewById<View>(R.id.editTextCVV).visibility = View.VISIBLE
                        findViewById<View>(R.id.editTextPhoneNumber).visibility = View.VISIBLE
                        findViewById<View>(R.id.buttonAddCard).visibility = View.VISIBLE
                    }
                    "BCA OneKlik", "BRI Direct Debit" -> {
                        findViewById<View>(R.id.editTextCardNumber).visibility = View.VISIBLE
                        findViewById<View>(R.id.editTextCardLimit).visibility = View.VISIBLE
                        findViewById<View>(R.id.editTextActivationMethod).visibility = View.VISIBLE
                    }
                    else -> {
                        // All other cases, ensure fields are hidden
                    }
                }
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {
                // Handle case where no item is selected (optional)
            }
        }

        // Confirm payment button logic
        val buttonConfirmPayment: Button = findViewById(R.id.buttonConfirmPayment)
        buttonConfirmPayment.setOnClickListener {
            val selectedPaymentMethod = spinnerPaymentMethod.selectedItem.toString()
            val selectedBank = spinnerBankOptions.selectedItem.toString()

            // Show Virtual Account format based on selected bank
            val virtualAccount = when (selectedBank) {
                "Bank BCA" -> "014 + [Nomor Pelanggan]"
                "Bank Mandiri" -> "008 + [Nomor Pelanggan]"
                "Bank BRI" -> "002 + [Nomor Pelanggan]"
                "Bank BNI" -> "009 + [Nomor Pelanggan]"
                "Bank CIMB Niaga" -> "022 + [Nomor Pelanggan]"
                "Bank Permata" -> "013 + [Nomor Pelanggan]"
                "Bank Danamon" -> "011 + [Nomor Pelanggan]"
                "Bank BTN" -> "200 + [Nomor Pelanggan]"
                "Bank Syariah Indonesia" -> "451 + [Nomor Pelanggan]"
                else -> ""
            }
            findViewById<TextView>(R.id.textViewVirtualAccount).text = "Virtual Account: $virtualAccount"

            if (selectedPaymentMethod == "Transfer Bank" && selectedBank.isEmpty()) {
                Toast.makeText(this, "Pilih bank untuk transfer!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Pembayaran dengan metode: $selectedPaymentMethod berhasil.", Toast.LENGTH_SHORT).show()

                // After payment is confirmed, clear the cart
                cartDatabaseHelper.clearCart()

                // Navigate back to CartActivity and refresh the cart view
                val intent = Intent(this, CartActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP  // Clear the current activity stack
                startActivity(intent)

                finish() // Close the PaymentActivity
            }
        }
    }

    // Helper function to hide all payment-related fields
    private fun hideAllFields() {
        // Hide fields for all payment methods
        findViewById<View>(R.id.textViewBankOptionsLabel).visibility = View.GONE
        findViewById<View>(R.id.spinnerBankOptions).visibility = View.GONE
        findViewById<View>(R.id.textViewVirtualAccount).visibility = View.GONE
        findViewById<View>(R.id.editTextCardNumber).visibility = View.GONE
        findViewById<View>(R.id.editTextExpiryDate).visibility = View.GONE
        findViewById<View>(R.id.editTextCVV).visibility = View.GONE
        findViewById<View>(R.id.editTextPhoneNumber).visibility = View.GONE
        findViewById<View>(R.id.buttonAddCard).visibility = View.GONE
        findViewById<View>(R.id.editTextCardLimit).visibility = View.GONE
        findViewById<View>(R.id.editTextActivationMethod).visibility = View.GONE
    }

    // Helper function to format price as "Rp."
    private fun formatPrice(price: Double): String {
        val numberFormat = java.text.NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        return numberFormat.format(price).replace("Rp", "Rp.").replace(",00", "")
    }
}
