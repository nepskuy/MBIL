package com.example.mbil

data class Item(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val price: String = "",
    val imageUrl: String = "",
    val rating: Float = 0f // Menambahkan rating
)
