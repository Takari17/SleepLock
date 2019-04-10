package com.example.sleeplock

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Glide.init
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import kotlinx.android.synthetic.main.recycler_view_layout.view.*

class MyAdapter(private val image: List<Int>, private val text: List<String>) :
    RecyclerView.Adapter<MyAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyAdapter.MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_layout, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int = image.size

    override fun onBindViewHolder(holder: MyAdapter.MyViewHolder, position: Int) {

        Glide.with(holder.itemView.context)
            .asBitmap()
            .load(image[position])
            .into(holder.image)

        holder.text.text = text[position]

    }


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image = itemView.item_image
        val text = itemView.item_text

        init {
            itemView.setOnClickListener {
                itemIndex.onNext(adapterPosition)
            }
        }
    }
}