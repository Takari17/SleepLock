package com.takari.sleeplock.whitenoise

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider


class WhiteNoiseFragment : Fragment() {

    companion object {
        const val TAG = "White Noise"
    }

    private lateinit var viewModel: WhiteNoiseViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("mytag", "White Noise Fragment Created")
        return ComposeView(requireContext()).apply {
            setContent {
                WhiteNoiseScreen()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[WhiteNoiseViewModel::class.java]
    }

    @Composable
    fun WhiteNoiseScreen() {
        Text(
            text = "This is the WhiteNoiseFragment :D",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 5.dp, bottom = 10.dp)
        )
    }

    @Preview(showBackground = true)
    @Composable
    fun Preview() {
        WhiteNoiseScreen()
    }
}