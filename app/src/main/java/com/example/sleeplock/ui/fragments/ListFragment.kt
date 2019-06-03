package com.example.sleeplock.ui.fragments


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.sleeplock.Application.Companion.applicationComponent
import com.example.sleeplock.R
import com.example.sleeplock.ui.adapter.MyAdapter
import com.example.sleeplock.utils.ItemData
import com.example.sleeplock.utils.activityViewModelFactory
import com.example.sleeplock.utils.showSoundSelectedToast
import com.example.sleeplock.utils.showWarningToast
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.fragment_list.*

class ListFragment : Fragment() {

    private val viewModel by activityViewModelFactory { applicationComponent.mainViewModel }
    private val compositeDisposable = CompositeDisposable()
    private val myAdapter by lazy {
        MyAdapter(ItemData.getAllImageReferences(), ItemData.getAllText(context!!))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_list, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(context, 2)
            adapter = myAdapter
        }

        viewModel.subscribeToItemIndex(myAdapter.itemIndex)
    }

    private fun showItemClickedToast(context: Context) =
        if (viewModel.getDidTimerStart()) showWarningToast(context)
        else showSoundSelectedToast(context)


    override fun onStart() {
        super.onStart()
        compositeDisposable += myAdapter.itemOnClickListener
            .subscribeBy(
                onNext = { showItemClickedToast(context!!) },
                onError = { Log.d("zwi", "Error observing recycler view itemClickListener: $it") }
            )
    }

    override fun onStop() {
        super.onStop()
        compositeDisposable.clear()
    }
}