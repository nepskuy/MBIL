package com.example.mbil

data class ItemCart(
    val id: String,            // ID is a String
    val name: String,
    val price: Double,         // Price is a Double
    val imageUrl: String,
    var quantity: Int          // Quantity is an Int
)
