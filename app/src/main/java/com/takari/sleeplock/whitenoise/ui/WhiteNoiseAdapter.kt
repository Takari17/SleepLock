package com.takari.sleeplock.whitenoise.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.takari.sleeplock.R
import com.takari.sleeplock.whitenoise.data.WhiteNoise
import kotlinx.android.synthetic.main.recycler_view_item_layout.view.*


/**
 * Displays a list of WhiteNoise's
 */
class WhiteNoiseAdapter(
    private val whiteNoiseList: List<WhiteNoise>,
    private val context: Context,
    private val onItemClick: (WhiteNoise) -> Unit
) : RecyclerView.Adapter<WhiteNoiseAdapter.WhiteNoiseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WhiteNoiseViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.recycler_view_item_layout, parent, false)

        return WhiteNoiseViewHolder(view)
    }

    override fun getItemCount(): Int = whiteNoiseList.size

    override fun onBindViewHolder(holder: WhiteNoiseViewHolder, position: Int) {
        holder.apply {

            Glide.with(context)
                .load(whiteNoiseList[position].image())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transition(DrawableTransitionOptions.withCrossFade(1000))
                .into(image)

            title.text = whiteNoiseList[position].name()
            description.text = whiteNoiseList[position].description()
        }
    }

    inner class WhiteNoiseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.itemPic
        val title: TextView = itemView.title
        val description: TextView = itemView.description

        init {
            itemView.setOnClickListener { onItemClick(whiteNoiseList[adapterPosition]) }
        }
    }
}
