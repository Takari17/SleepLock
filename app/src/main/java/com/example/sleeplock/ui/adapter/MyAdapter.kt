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
import com.jakewharton.rxrelay2.PublishRelay
import kotlinx.android.synthetic.main.recycler_view_layout.view.*

/*
 * Displays the sound options available to the user.
 */
class MyAdapter(
    private val imageList: List<Int>,
    private val textList: List<String>
) :
    RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    //Emits the index of the item clicked
    val itemOnClickListener = BehaviorRelay.create<Int>()

    /*
    Tells ListFragment.kt to show a toast on recycler view click, have to use a publish relay for this
    else a toast will randomly appear onStart, even when the user hasn't done anything since behavior relays
    hold the last value emitted..
     */
    val showClickedToast = PublishRelay.create<Boolean>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder =
        LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_layout, parent, false).let { view ->
            MyViewHolder(view)
        }


    override fun getItemCount(): Int = imageList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        Glide.with(holder.itemView.context)
            .asBitmap()
            .load(imageList[position])
            .into(holder.image)

        holder.text.text = textList[position]
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.itemPic
        val text: TextView = itemView.itemText

        init {
            itemView.setOnClickListener {
                itemOnClickListener.accept(adapterPosition)
                showClickedToast.accept(true)
            }
        }
    }
}