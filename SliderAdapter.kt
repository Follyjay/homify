package com.example.homify

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

// Adapter for displaying a list of images in a RecyclerView
class SliderAdapter(private val context: Context, private val imageUrls: List<String>) :
    RecyclerView.Adapter<SliderAdapter.ImageViewHolder>() {

    // ViewHolder class to hold the views for each item in the RecyclerView
    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val contentImages: ImageView = itemView.findViewById(R.id.imgContentImages)
    }

    // Inflate the item layout and create the ViewHolder instance
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.content_images, parent, false)
        return ImageViewHolder(view)
    }

    // Bind data to the view items
    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageUrl = imageUrls[position]
        Glide.with(context).load(imageUrl).into(holder.contentImages)
    }

    // Returns the number of items
    override fun getItemCount(): Int {
        return imageUrls.size
    }
}
