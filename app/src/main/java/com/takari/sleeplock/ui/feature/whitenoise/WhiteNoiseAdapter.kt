package com.takari.sleeplock.ui.feature.whitenoise

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.takari.sleeplock.R
import com.takari.sleeplock.ui.feature.SharedViewModel
import com.takari.sleeplock.ui.feature.WhiteNoiseData
import kotlinx.android.synthetic.main.recycler_view_item_layout.view.*


/**
 * Displays 12 selectable white sound options to the user.
 */
class WhiteNoiseAdapter(
    private val sharedViewModel: SharedViewModel,
    private val context: Context
) :
    RecyclerView.Adapter<WhiteNoiseAdapter.MyViewHolder>() {

    private val whiteNoiseList = sharedViewModel.getWhiteNoiseList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_view_item_layout, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int = whiteNoiseList.getAllImages().size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.apply {
            image.load(whiteNoiseList.getAllImages()[position])

            text.text = whiteNoiseList.getAllNames(context)[position]
        }
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.itemPic
        val text: TextView = itemView.itemText

        init {
            itemView.setOnClickListener {

                val data = WhiteNoiseData(
                    getItemImage(adapterPosition),
                    getItemName(adapterPosition),
                    getItemWhiteNoise(adapterPosition)
                )

                sharedViewModel.apply {
                    setWhiteNoiseDataIfTimerNotStarted(data)
                    setToastData()
                }
            }
        }

        private fun getItemImage(position: Int) = whiteNoiseList.getAllImages()[position]

        private fun getItemName(position: Int) = whiteNoiseList.getAllNames(context)[position]

        private fun getItemWhiteNoise(position: Int) = whiteNoiseList.getAllNoises()[position]
    }
}