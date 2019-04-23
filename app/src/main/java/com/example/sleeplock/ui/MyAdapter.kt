package com.example.sleeplock.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sleeplock.R
import com.example.sleeplock.feature.isTimerRunning
import com.example.sleeplock.utils.warnOrSuccessToast
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.recycler_view_layout.view.*

val itemIndex = BehaviorSubject.create<Int>()

class MyAdapter(private val context: Context, private val image: List<Int>, private val text: List<String>) :
    RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_layout, parent, false)

        return MyViewHolder(context, view)
    }

    override fun getItemCount(): Int = image.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        Glide.with(holder.itemView.context)
            .asBitmap()
            .load(image[position])
            .into(holder.image)

        holder.text.text = text[position]
    }

    class MyViewHolder(context: Context, itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image = itemView.item_image
        val text = itemView.item_text

        init {
            itemView.setOnClickListener {
                itemIndex.onNext(adapterPosition)
                isTimerRunning.warnOrSuccessToast(context) // will show a warning toast if the timer is running
            }
        }
    }
}