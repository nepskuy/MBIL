package com.example.mbil

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import java.text.NumberFormat
import java.util.*

class DetailActivity : AppCompatActivity() {

    private lateinit var itemNameTextView: TextView
    private lateinit var itemPriceTextView: TextView
    private lateinit var itemDescriptionTextView: TextView
    private lateinit var itemImageView: ImageView
    private lateinit var cartDatabaseHelper: CartDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        cartDatabaseHelper = CartDatabaseHelper(this)

        // Tombol Add to Cart
        val addToCartButton: Button = findViewById(R.id.buttonAddToCart)
        addToCartButton.setOnClickListener {
            val itemName = intent.getStringExtra("ITEM_NAME") ?: "Item Name"
            val itemPriceText = intent.getStringExtra("ITEM_PRICE") ?: "0.0"
            val itemImageUrl = intent.getStringExtra("ITEM_IMAGE_URL") ?: ""

            // Konversi harga dari String ke Double dengan validasi null
            val itemPrice = itemPriceText.toDoubleOrNull() ?: run {
                Toast.makeText(this, "Invalid price format", Toast.LENGTH_SHORT).show()
                return@setOnClickListener // Keluar dari listener jika harga tidak valid
            }

            // Simpan ke database
            val result = cartDatabaseHelper.addItemToCart(itemName, itemPrice, 1, itemImageUrl)
            if (result != -1L) {
                Toast.makeText(this, "Item added to cart", Toast.LENGTH_SHORT).show()

                // Arahkan ke CartActivity
                val intent = Intent(this, CartActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Failed to add item", Toast.LENGTH_SHORT).show()
            }
        }

        // Inisialisasi elemen UI
        itemNameTextView = findViewById(R.id.textViewItemName)
        itemPriceTextView = findViewById(R.id.textViewItemPrice)
        itemDescriptionTextView = findViewById(R.id.textViewDetailDescription)
        itemImageView = findViewById(R.id.imageViewItem)

        // Ambil data item dari intent
        val itemName = intent.getStringExtra("ITEM_NAME") ?: "Item Name"
        val itemPriceText = intent.getStringExtra("ITEM_PRICE") ?: "0.0"
        val itemDescription = intent.getStringExtra("ITEM_DESCRIPTION") ?: "No description available"
        val itemImageUrl = intent.getStringExtra("ITEM_IMAGE_URL") // URL gambar item

        // Konversi harga untuk tampilan
        val itemPrice = itemPriceText.toDoubleOrNull() ?: 0.0
        val formattedPrice = formatPrice(itemPrice)

        // Set data ke elemen UI
        itemNameTextView.text = itemName
        itemPriceTextView.text = formattedPrice
        itemDescriptionTextView.text = itemDescription

        // Menampilkan gambar menggunakan Glide
        Glide.with(this).load(itemImageUrl).into(itemImageView)
    }

    // Helper function to format the price as "Rp."
    private fun formatPrice(price: Double): String {
        val numberFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        return numberFormat.format(price).replace("Rp", "Rp.").replace(",00", "")
    }
}
