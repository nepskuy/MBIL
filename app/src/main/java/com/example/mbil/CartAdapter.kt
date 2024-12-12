package com.example.mbil

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class CartAdapter(
    private val cartItemList: MutableList<CartItem>,
    private val onRemoveClick: (CartItem) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    // ViewHolder class to hold the item views for each cart item
    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemImageView: ImageView = itemView.findViewById(R.id.imageViewCartItem)
        val nameTextView: TextView = itemView.findViewById(R.id.textViewCartItemName)
        val priceTextView: TextView = itemView.findViewById(R.id.textViewCartItemPrice)
        val quantityTextView: TextView = itemView.findViewById(R.id.textViewCartItemQuantity)
        val removeButton: Button = itemView.findViewById(R.id.buttonRemoveItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        // Inflate the cart item layout for each view holder
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cart_item_layout, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        // Get the cart item from the list at the specified position
        val cartItem = cartItemList[position]

        // Set item name and price
        holder.nameTextView.text = cartItem.name
        holder.priceTextView.text = cartItem.price
        holder.quantityTextView.text = "Qty: ${cartItem.quantity}"

        // If imageUrl is provided, load the image using Glide
        if (!cartItem.imageUrl.isNullOrEmpty()) {
            Glide.with(holder.itemView.context)
                .load(cartItem.imageUrl)  // Load image from the provided URL
                .placeholder(R.drawable.ic_launcher_background) // Set a placeholder image while loading
                .into(holder.itemImageView) // Set the loaded image into ImageView
        } else {
            // If imageUrl is null or empty, set a default image
            holder.itemImageView.setImageResource(R.drawable.ic_launcher_background)
        }

        // Handle the remove button click to remove item from the cart
        holder.removeButton.setOnClickListener {
            onRemoveClick(cartItem)
        }
    }

    override fun getItemCount(): Int {
        // Return the total number of items in the cart
        return cartItemList.size
    }

    // Method to update the list of cart items
    fun updateCartItems(newItems: List<CartItem>) {
        cartItemList.clear() // Clear existing items in the cart
        cartItemList.addAll(newItems) // Add the new items
        notifyDataSetChanged() // Notify the adapter that the data has changed
    }
}