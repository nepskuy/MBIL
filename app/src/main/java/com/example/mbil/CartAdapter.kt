package com.example.mbil

import ItemCart
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.text.NumberFormat
import java.util.*

class CartAdapter(
    private val cartItems: MutableList<ItemCart>,
    private val onRemoveClick: (ItemCart) -> Unit,
    private val onQuantityChanged: (ItemCart) -> Unit // Callback untuk pembaruan quantity
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemImageView: ImageView = itemView.findViewById(R.id.imageViewCartItem)
        val itemNameTextView: TextView = itemView.findViewById(R.id.textViewCartItemName)
        val itemPriceTextView: TextView = itemView.findViewById(R.id.textViewCartItemPrice)
        val itemQuantityTextView: TextView = itemView.findViewById(R.id.textViewCartItemQuantity)
        val increaseQuantityButton: Button = itemView.findViewById(R.id.buttonIncreaseQuantity)
        val decreaseQuantityButton: Button = itemView.findViewById(R.id.buttonDecreaseQuantity)
        val removeButton: Button = itemView.findViewById(R.id.buttonRemoveItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartItem = cartItems[position]

        holder.itemNameTextView.text = cartItem.name
        holder.itemPriceTextView.text = formatPrice(cartItem.price * cartItem.quantity)
        holder.itemQuantityTextView.text = cartItem.quantity.toString()

        Glide.with(holder.itemView.context).load(cartItem.imageUrl).into(holder.itemImageView)

        holder.increaseQuantityButton.setOnClickListener {
            cartItem.quantity++
            notifyItemChanged(position)
            onQuantityChanged(cartItem) // Panggil callback untuk memperbarui total harga
        }

        holder.decreaseQuantityButton.setOnClickListener {
            if (cartItem.quantity > 1) {
                cartItem.quantity--
                notifyItemChanged(position)
                onQuantityChanged(cartItem) // Panggil callback untuk memperbarui total harga
            }
        }

        holder.removeButton.setOnClickListener {
            onRemoveClick(cartItem) // Panggil callback untuk menghapus item
        }
    }

    override fun getItemCount(): Int = cartItems.size

    // Tambahkan fungsi removeItem
    fun removeItem(cartItem: ItemCart) {
        val position = cartItems.indexOf(cartItem)
        if (position != -1) {
            cartItems.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    private fun formatPrice(price: Double): String {
        val localeID = Locale("id", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
        return numberFormat.format(price).replace("Rp", "Rp.").replace(",00", "")
    }
}
