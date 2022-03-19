package com.example.mapsapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mapsapp.databinding.SingleItemBinding
import com.example.mapsapp.model.ImageItem

class MarkerViewAdapter(
    private val values: List<ImageItem>
) : RecyclerView.Adapter<MarkerViewAdapter.ViewHolder>() {

    inner class ViewHolder(binding: SingleItemBinding) : RecyclerView.ViewHolder(binding.root) {
        var imageView = binding.imageView
        val createdDateTextView = binding.textCreatedDate
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            SingleItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val imageItem = values[position]
        //holder.imageView.setImageBitmap(imageItem.image)
        holder.imageView.setImageURI(imageItem.imageUri)
        holder.createdDateTextView.text = imageItem.createDate.toString()
    }

    override fun getItemCount(): Int = values.size
}