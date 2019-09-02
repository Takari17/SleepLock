package com.takari.sleeplock.ui.feature.whitenoise


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.takari.sleeplock.App.Companion.applicationComponent
import com.takari.sleeplock.R
import com.takari.sleeplock.ui.feature.ToastTypes
import com.takari.sleeplock.utils.activityViewModelFactory
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.whitenoise_fragment.*

class WhiteNoiseFragment : Fragment() {

    private val sharedViewModel by activityViewModelFactory { applicationComponent.sharedViewModel }
    private val whiteNoiseAdapter by lazy { WhiteNoiseAdapter(sharedViewModel, context!!) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.whitenoise_fragment, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(context, 2)
            adapter = whiteNoiseAdapter
        }


        sharedViewModel.getToast().observe(viewLifecycleOwner, Observer { toast ->

            if (toast.type == ToastTypes.Success.name) showClickedToast(toast.stringID)
            else showWarningToast(toast.stringID)
        })
    }

    private fun showClickedToast(stringID: Int) =
        Toasty.success(context!!, stringID, Toasty.LENGTH_SHORT, true).show()

    private fun showWarningToast(stringID: Int) =
        Toasty.warning(context!!, stringID, Toasty.LENGTH_SHORT, true).show()

}