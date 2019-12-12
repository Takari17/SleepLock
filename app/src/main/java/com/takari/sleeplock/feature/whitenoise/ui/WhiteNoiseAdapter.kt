package com.takari.sleeplock.feature.whitenoise.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.takari.sleeplock.R
import com.takari.sleeplock.feature.whitenoise.data.sounds.WhiteNoise
import kotlinx.android.synthetic.main.recycler_view_item_layout.view.*


class WhiteNoiseAdapter(
    private val whiteNoiseList: List<WhiteNoise>,
    private val clickedItemData: (WhiteNoise) -> Unit
) :
    RecyclerView.Adapter<WhiteNoiseAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_view_item_layout, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int = whiteNoiseList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.apply {
            image.load(whiteNoiseList[position].image())
            text.text = whiteNoiseList[position].name()
        }
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.itemPic
        val text: TextView = itemView.itemText

        init {
            itemView.setOnClickListener { clickedItemData(whiteNoiseList[adapterPosition]) }
        }
    }
}
