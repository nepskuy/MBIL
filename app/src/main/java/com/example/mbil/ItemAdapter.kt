package com.example.mbil

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.text.NumberFormat
import java.util.Locale

class ItemAdapter(private val itemList: List<Item>) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.textViewName)
        val descriptionTextView: TextView = itemView.findViewById(R.id.textViewDescription)
        val priceTextView: TextView = itemView.findViewById(R.id.textViewPrice)
        val imageView: ImageView = itemView.findViewById(R.id.imageViewItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = itemList[position]
        holder.nameTextView.text = item.name
        holder.descriptionTextView.text = item.description

        // Format price as currency (Rp)
        val formattedPrice = formatCurrency(item.price)
        holder.priceTextView.text = formattedPrice

        // Load image using Glide
        Glide.with(holder.itemView.context)
            .load(item.imageUrl)
            .into(holder.imageView)

        // Set OnClickListener for the item
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra("ITEM_NAME", item.name)
            intent.putExtra("ITEM_DESCRIPTION", item.description)
            intent.putExtra("ITEM_PRICE", item.price.toString()) // Convert Double to String for intent
            intent.putExtra("ITEM_IMAGE_URL", item.imageUrl)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    // Helper function to format price as currency
    private fun formatCurrency(price: Double): String {
        // Format as currency with thousands separator, without decimals
        val formattedPrice = NumberFormat.getNumberInstance(Locale("id", "ID")).format(price)

        // Add "Rp." manually in front of the formatted price
        return "Rp. $formattedPrice"
    }
}
