package com.example.mbil

data class Item(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0, // Gunakan Double untuk harga
    val imageUrl: String = "",
    val rating: Float = 0f // Tambahkan jika diperlukan untuk rating
)
