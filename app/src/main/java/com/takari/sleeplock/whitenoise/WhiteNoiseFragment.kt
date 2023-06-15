package com.takari.sleeplock.whitenoise

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.takari.sleeplock.R
import com.takari.sleeplock.whitenoise.data.WhiteNoise
import com.takari.sleeplock.whitenoise.data.WhiteNoiseOptions



class WhiteNoiseFragment : Fragment() {
    companion object {
        const val TAG = "White Noise"
    }

    private lateinit var viewModel: WhiteNoiseViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                // TODO get from the viewmodel
                val whiteNoises = WhiteNoiseOptions.get
                WhiteNoiseScreen(whiteNoises)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[WhiteNoiseViewModel::class.java]
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun WhiteNoiseScreen(whiteNoises: List<WhiteNoise>) {
        Box(modifier = Modifier.fillMaxSize()) {
            val state = rememberLazyListState()

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                state = state,
                flingBehavior = rememberSnapFlingBehavior(lazyListState = state),
            ) {
                items(whiteNoises) { item: WhiteNoise ->
                    val imageModifier = Modifier
                        .fillParentMaxSize()
                        .clickable { }

                    WhiteNoiseView(whiteNoise = item, imageModifier = imageModifier)
                }
            }

            Text(
                text = "Pick a Sound!",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp, top = 8.dp)
            )

            Image(
                painter = painterResource(id = R.drawable.transparant_play_icon),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(100.dp)
            )
        }
    }

    @Composable
    fun WhiteNoiseView(whiteNoise: WhiteNoise, imageModifier: Modifier) {
        Box {
            AsyncKensBurnImage(
                imageID = whiteNoise.image(),
                imageModifier = imageModifier
            )

            Column(
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier
                    .fillMaxHeight()
                    .width(375.dp)
                    .padding(bottom = 150.dp, start = 16.dp)
            ) {
                Text(
                    text = whiteNoise.name(),
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                )

                Text(
                    text = whiteNoise.description(),
                    color = Color.White,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun Preview() {
        val whiteNoises = WhiteNoiseOptions.get
        WhiteNoiseScreen(whiteNoises)
    }
}