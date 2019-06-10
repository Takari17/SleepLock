package com.takari.sleeplock.ui.feature.whitenoise


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.takari.sleeplock.Application.Companion.applicationComponent
import com.takari.sleeplock.R
import com.takari.sleeplock.utils.ItemData
import com.takari.sleeplock.utils.activityViewModelFactory
import com.takari.sleeplock.utils.showSoundSelectedToast
import com.takari.sleeplock.utils.showWarningToast
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.fragment_list.*

class WhiteNoiseFragment : Fragment() {

    private val viewModel by activityViewModelFactory { applicationComponent.timerViewModel }
    private val compositeDisposable = CompositeDisposable()
    private val myAdapter by lazy {
        WhiteNoiseAdapter(
            ItemData.getAllImageReferences(),
            ItemData.getAllText(context!!)
        )
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

        viewModel.subscribeToItemIndex(myAdapter.itemOnClickListener)
    }

    private fun showItemClickedToast(context: Context) =
        if (viewModel.getDidTimerStart()) showWarningToast(context)
        else showSoundSelectedToast(context)


    override fun onStart() {
        super.onStart()
        compositeDisposable += myAdapter.showClickedToast
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