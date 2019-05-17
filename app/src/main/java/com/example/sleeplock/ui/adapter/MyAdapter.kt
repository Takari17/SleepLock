package com.example.sleeplock.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sleeplock.R
import com.jakewharton.rxrelay2.BehaviorRelay
import kotlinx.android.synthetic.main.recycler_view_layout.view.*

class MyAdapter(private val image: List<Int>, private val text: List<String>) :
    RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    val itemIndex = BehaviorRelay.create<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_layout, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int = image.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        Glide.with(holder.itemView.context)
            .asBitmap()
            .load(image[position])
            .into(holder.image)

        holder.text.text = text[position]
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.item_image
        val text: TextView = itemView.item_text

        init {//Passes the index of the item clicked
            itemView.setOnClickListener { itemIndex.accept(adapterPosition) }
        }
    }
}