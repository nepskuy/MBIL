package com.example.mbil

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import java.text.NumberFormat
import java.util.*

class DetailActivity : AppCompatActivity() {

    private lateinit var itemNameTextView: TextView
    private lateinit var itemPriceTextView: TextView
    private lateinit var itemDescriptionTextView: TextView
    private lateinit var itemImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        // Inisialisasi elemen UI
        itemNameTextView = findViewById(R.id.textViewItemName)
        itemPriceTextView = findViewById(R.id.textViewItemPrice)
        itemDescriptionTextView = findViewById(R.id.textViewDetailDescription)
        itemImageView = findViewById(R.id.imageViewItem)

        // Ambil data item dari intent
        val itemName = intent.getStringExtra("ITEM_NAME") ?: "Item Name"
        val itemPrice = intent.getStringExtra("ITEM_PRICE") ?: "Rp.0.00"
        val itemDescription = intent.getStringExtra("ITEM_DESCRIPTION") ?: "No description available"
        val itemImageUrl = intent.getStringExtra("ITEM_IMAGE_URL") // URL gambar item

        // Format price as "Rp."
        val formattedPrice = formatPrice(itemPrice)

        // Set data ke elemen UI
        itemNameTextView.text = itemName
        itemPriceTextView.text = formattedPrice
        itemDescriptionTextView.text = itemDescription

        // Menampilkan gambar menggunakan Glide
        Glide.with(this).load(itemImageUrl).into(itemImageView)
    }

    // Helper function to format the price as "Rp."
    private fun formatPrice(price: String): String {
        val priceValue = price.replace("Rp.", "").replace(",", "").toDoubleOrNull() ?: 0.0
        val numberFormat = NumberFormat.getNumberInstance(Locale("id", "ID"))
        return "Rp. ${numberFormat.format(priceValue)}"
    }
}
