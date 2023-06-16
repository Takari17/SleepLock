package com.takari.sleeplock.whitenoise

import SleepLockTimeSelectionDialog
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.takari.sleeplock.R
import com.takari.sleeplock.whitenoise.data.WhiteNoise
import com.takari.sleeplock.whitenoise.service.WhiteNoiseService
import com.takari.sleeplock.whitenoise.ui.WhiteNoiseViewModel


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WhiteNoiseScreen(viewModel: WhiteNoiseViewModel) {
    val whiteNoiseUiState by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        val state = rememberLazyListState()

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            state = state,
            flingBehavior = rememberSnapFlingBehavior(lazyListState = state),
        ) {
            items(viewModel.getWhiteNoiseOptions()) { item: WhiteNoise ->
                val imageModifier = Modifier
                    .fillParentMaxSize()
                    .clickable {
                        viewModel.onAdapterClick(
                            item,
                            WhiteNoiseService.isRunning(),
                            false
                        )
                    }

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

    OpenTimeOptionsDialog(
        showTimePicker = whiteNoiseUiState.showTimePicker,
        onCancel = { viewModel.closeDialog() },
        onTimeSelected = { viewModel.closeDialog() }
    )
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

@Composable
fun OpenTimeOptionsDialog(
    showTimePicker: Boolean,
    onCancel: () -> Unit = {},
    onTimeSelected: (Long) -> Unit = { }
) {
    SleepLockTimeSelectionDialog(
        showTimePicker,
        onCancel = { onCancel() },
        onTimeSelected = { onTimeSelected(it) }
    )
}