data class ItemCart(
    val id: String,          // ID produk
    val name: String,        // Nama produk
    val price: Double,       // Harga dalam Double
    val imageUrl: String,    // URL gambar produk
    var quantity: Int = 1    // Kuantitas produk
)
