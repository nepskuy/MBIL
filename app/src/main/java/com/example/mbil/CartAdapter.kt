package com.example.mbil

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
    private val cartItems: MutableList<ItemCart>,   // The cartItems list of ItemCart objects
    private val onRemoveClick: (ItemCart) -> Unit,   // Callback for item removal
    private val onQuantityChanged: (ItemCart) -> Unit // Callback for quantity change
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    // ViewHolder for binding data to each item in the RecyclerView
    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemImageView: ImageView = itemView.findViewById(R.id.imageViewCartItem)
        val itemNameTextView: TextView = itemView.findViewById(R.id.textViewCartItemName)
        val itemPriceTextView: TextView = itemView.findViewById(R.id.textViewCartItemPrice)
        val itemQuantityTextView: TextView = itemView.findViewById(R.id.textViewCartItemQuantity)
        val increaseQuantityButton: Button = itemView.findViewById(R.id.buttonIncreaseQuantity)
        val decreaseQuantityButton: Button = itemView.findViewById(R.id.buttonDecreaseQuantity)
        val removeButton: Button = itemView.findViewById(R.id.buttonRemoveItem)
    }

    // Creates a new ViewHolder and inflates the item layout for each cart item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    // Binds data from cartItems to each view in the ViewHolder
    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        if (position !in cartItems.indices) return // Ensure the position is valid

        val cartItem = cartItems[position]

        // Set the name, formatted price, and quantity of the item
        holder.itemNameTextView.text = cartItem.name
        holder.itemPriceTextView.text = formatPrice(cartItem.price * cartItem.quantity) // Format price to Rupiah
        holder.itemQuantityTextView.text = cartItem.quantity.toString() // Display the quantity as a string

        // Use Glide to load the item image into the ImageView
        Glide.with(holder.itemView.context)
            .load(cartItem.imageUrl)
            .placeholder(android.R.drawable.ic_menu_gallery) // Placeholder image if not found
            .error(android.R.drawable.ic_menu_report_image) // Error image if the image URL is incorrect
            .into(holder.itemImageView)

        // Increase quantity button click listener
        holder.increaseQuantityButton.setOnClickListener {
            cartItem.quantity++
            notifyItemChanged(position)
            onQuantityChanged(cartItem) // Callback to update quantity in the database
        }

        // Decrease quantity button click listener
        holder.decreaseQuantityButton.setOnClickListener {
            if (cartItem.quantity > 1) {
                cartItem.quantity--
                notifyItemChanged(position)
                onQuantityChanged(cartItem) // Callback to update quantity in the database
            }
        }

        // Remove item button click listener
        holder.removeButton.setOnClickListener {
            onRemoveClick(cartItem) // Callback to remove the item from the cart and database
        }
    }

    // Returns the total number of items in the cart
    override fun getItemCount(): Int = cartItems.size

    // Function to remove an item from the cart and update the RecyclerView
    fun removeItem(cartItem: ItemCart) {
        val position = cartItems.indexOf(cartItem)
        if (position != -1) {
            cartItems.removeAt(position)
            notifyItemRemoved(position) // Notify the adapter that an item has been removed
        }
    }

    // Helper function to format the price in Rupiah
    private fun formatPrice(price: Double): String {
        val localeID = Locale("id", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
        return numberFormat.format(price).replace("Rp", "Rp.").replace(",00", "")
    }
}
