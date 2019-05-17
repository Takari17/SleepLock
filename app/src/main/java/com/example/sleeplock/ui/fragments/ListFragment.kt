package com.example.sleeplock.ui.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.sleeplock.R
import com.example.sleeplock.injection.Application.Companion.applicationComponent
import com.example.sleeplock.injection.activityViewModelFactory
import com.example.sleeplock.ui.adapter.MyAdapter
import com.example.sleeplock.utils.ITEM_PIC
import com.example.sleeplock.utils.ITEM_TEXT
import kotlinx.android.synthetic.main.fragment_list.*

class ListFragment : Fragment() {

    private lateinit var myAdapter: MyAdapter
    private val viewModel by activityViewModelFactory { applicationComponent.mainViewModel }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        myAdapter = MyAdapter(context!!, ITEM_PIC, ITEM_TEXT)

        recycler_view.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(context, 2)
            adapter = myAdapter
        }
        viewModel.subscribeToItemIndex(myAdapter.itemIndex)
    }
}