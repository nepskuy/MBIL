package com.example.mbil

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.text.NumberFormat
import java.util.Locale

class PaymentActivity : AppCompatActivity() {

    private val bankVaPrefixes = mapOf(
        "Bank BCA" to "014",
        "Bank Mandiri" to "008",
        "Bank BRI" to "002",
        "Bank BNI" to "009",
        "Bank CIMB Niaga" to "022",
        "Bank Permata" to "013"
    )

    private lateinit var cartDatabaseHelper: CartDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        cartDatabaseHelper = CartDatabaseHelper(this)

        val totalPrice = intent.getDoubleExtra("TOTAL_PRICE", 0.0)
        val textViewTotalPrice: TextView = findViewById(R.id.textViewTotalPrice)
        textViewTotalPrice.text = formatPrice(totalPrice)

        val paymentMethods = arrayOf("Tunai", "Kartu Kredit/Debit", "Transfer Bank", "BCA OneKlik", "BRI Direct Debit")
        val bankOptions = bankVaPrefixes.keys.toTypedArray()

        val spinnerPaymentMethod: Spinner = findViewById(R.id.spinnerPaymentMethod)
        val spinnerBankOptions: Spinner = findViewById(R.id.spinnerBankOptions)
        val textViewVirtualAccount: TextView = findViewById(R.id.textViewVirtualAccount)
        val editTextCardNumber: EditText = findViewById(R.id.editTextCardNumber)
        val editTextExpiryDate: EditText = findViewById(R.id.editTextExpiryDate)
        val editTextPhoneNumber: EditText = findViewById(R.id.editTextPhoneNumber)
        val buttonAddCard: Button = findViewById(R.id.buttonAddCard)
        val buttonConfirmPayment: Button = findViewById(R.id.buttonConfirmPayment)

        val paymentAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, paymentMethods)
        paymentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPaymentMethod.adapter = paymentAdapter

        val bankAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, bankOptions)
        bankAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerBankOptions.adapter = bankAdapter

        spinnerBankOptions.visibility = View.GONE
        textViewVirtualAccount.visibility = View.GONE
        editTextCardNumber.visibility = View.GONE
        editTextExpiryDate.visibility = View.GONE
        editTextPhoneNumber.visibility = View.GONE
        buttonAddCard.visibility = View.GONE

        spinnerPaymentMethod.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedPaymentMethod = paymentMethods[position]

                when (selectedPaymentMethod) {
                    "Transfer Bank" -> {
                        spinnerBankOptions.visibility = View.VISIBLE
                        editTextCardNumber.visibility = View.GONE
                        editTextExpiryDate.visibility = View.GONE
                        editTextPhoneNumber.visibility = View.GONE
                        buttonAddCard.visibility = View.GONE
                    }
                    "Kartu Kredit/Debit" -> {
                        editTextCardNumber.visibility = View.VISIBLE
                        editTextExpiryDate.visibility = View.VISIBLE
                        editTextPhoneNumber.visibility = View.VISIBLE
                        buttonAddCard.visibility = View.GONE
                        spinnerBankOptions.visibility = View.GONE
                        textViewVirtualAccount.visibility = View.GONE
                    }
                    "BCA OneKlik", "BRI Direct Debit" -> {
                        editTextCardNumber.visibility = View.VISIBLE
                        editTextExpiryDate.visibility = View.VISIBLE
                        editTextPhoneNumber.visibility = View.VISIBLE
                        buttonAddCard.visibility = View.VISIBLE
                        spinnerBankOptions.visibility = View.GONE
                        textViewVirtualAccount.visibility = View.GONE
                    }
                    else -> {
                        spinnerBankOptions.visibility = View.GONE
                        textViewVirtualAccount.visibility = View.GONE
                        editTextCardNumber.visibility = View.GONE
                        editTextExpiryDate.visibility = View.GONE
                        editTextPhoneNumber.visibility = View.GONE
                        buttonAddCard.visibility = View.GONE
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                spinnerBankOptions.visibility = View.GONE
                textViewVirtualAccount.visibility = View.GONE
                editTextCardNumber.visibility = View.GONE
                editTextExpiryDate.visibility = View.GONE
                editTextPhoneNumber.visibility = View.GONE
                buttonAddCard.visibility = View.GONE
            }
        }

        spinnerBankOptions.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedBank = bankOptions[position]
                val customerId = "12345678"
                val virtualAccount = "${bankVaPrefixes[selectedBank]}$customerId"
                textViewVirtualAccount.text = "Virtual Account: $virtualAccount"
                textViewVirtualAccount.visibility = View.VISIBLE
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                textViewVirtualAccount.visibility = View.GONE
            }
        }

        buttonConfirmPayment.setOnClickListener {
            val selectedPaymentMethod = spinnerPaymentMethod.selectedItem.toString()

            if (selectedPaymentMethod == "Kartu Kredit/Debit" || selectedPaymentMethod == "BCA OneKlik" || selectedPaymentMethod == "BRI Direct Debit") {
                val cardNumber = editTextCardNumber.text.toString()
                if (cardNumber.isEmpty()) {
                    Toast.makeText(this, "Masukkan nomor kartu!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            val selectedBank = if (spinnerBankOptions.visibility == View.VISIBLE) {
                spinnerBankOptions.selectedItem.toString()
            } else {
                ""
            }

            if (selectedPaymentMethod == "Transfer Bank" && selectedBank.isEmpty()) {
                Toast.makeText(this, "Pilih bank untuk transfer!", Toast.LENGTH_SHORT).show()
            } else {
                cartDatabaseHelper.clearCart()

                Toast.makeText(this, "Pembayaran berhasil dengan metode: $selectedPaymentMethod.", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
        }
    }

    private fun formatPrice(price: Double): String {
        val localeID = Locale("id", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
        return numberFormat.format(price).replace("Rp", "Rp.").replace(",00", "")
    }
}
