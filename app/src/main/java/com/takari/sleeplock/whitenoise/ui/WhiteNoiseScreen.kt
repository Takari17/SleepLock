package com.takari.sleeplock.whitenoise.ui

import SleepLockTimeSelectionDialog
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.takari.sleeplock.R
import com.takari.sleeplock.logD
import com.takari.sleeplock.whitenoise.WhiteNoiseViewModel
import com.takari.sleeplock.whitenoise.data.WhiteNoise
import com.takari.sleeplock.whitenoise.service.WhiteNoiseService
import kotlinx.coroutines.launch


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WhiteNoiseScreen(viewModel: WhiteNoiseViewModel = viewModel()) {
    val whiteNoiseUiState by viewModel.uiState.collectAsState()

    logD(whiteNoiseUiState.toString())

    Box(modifier = Modifier.fillMaxSize()) {
        val state = rememberLazyListState()
        val coroutineScope = rememberCoroutineScope()

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            state = state,
            flingBehavior = rememberSnapFlingBehavior(lazyListState = state),
            userScrollEnabled = !whiteNoiseUiState.mediaServiceIsRunning
        ) {
            items(viewModel.getWhiteNoiseOptions()) { item: WhiteNoise ->
                val imageModifier = Modifier
                    .fillParentMaxSize()
                    .clickable {
                        viewModel.onWhiteNoiseItemClick(
                            clickedWhiteNoise = item,
                            serviceIsRunning = WhiteNoiseService.isRunning(),
                            timerIsRunning = WhiteNoiseService.timerIsRunning(),
                        )
                    }

                WhiteNoiseItem(whiteNoise = item, imageModifier = imageModifier)
            }
        }

        coroutineScope.launch {
            val index =
                viewModel.getWhiteNoiseOptions().indexOf(whiteNoiseUiState.clickedWhiteNoise)
            logD("Index: $index")
            state.animateScrollToItem(index)

        }

        FadingText(
            modifier = Modifier.padding(start = 8.dp, top = 24.dp),
            text = "Pick a Sound!",
            fadingCondition = whiteNoiseUiState.mediaServiceIsRunning,
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
        )

        val imageId = if (whiteNoiseUiState.isTimerRunning) {
            R.drawable.transparant_pause_icon
        } else {
            R.drawable.transparant_play_icon
        }

        Image(
            painter = painterResource(id = imageId),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.Center)
                .size(100.dp)
        )

        FadingButton(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 64.dp),
            fadingCondition = whiteNoiseUiState.mediaServiceIsRunning,
            onClick = { viewModel.destroyService() },
        )

        FadingText(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 64.dp),
            text = whiteNoiseUiState.elapseTime,
            color = Color.White,
            fontSize = 64.sp,
            fadingCondition = !whiteNoiseUiState.mediaServiceIsRunning,
        )
    }

    SelectTimeDialog(
        showTimePicker = whiteNoiseUiState.showTimePickerDialog,
        onCancel = { viewModel.closeDialog() },
        onTimeSelected = { millis: Long -> viewModel.onUserSelectedTimeFromDialog(millis) }
    )
}

@Composable
fun WhiteNoiseItem(whiteNoise: WhiteNoise, imageModifier: Modifier) {
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
fun SelectTimeDialog(
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


@Preview
@Composable
fun FadingButton(
    modifier: Modifier = Modifier,
    fadingCondition: Boolean = true,
    onClick: () -> Unit = {}
) {

    val visibility: Float by animateFloatAsState(
        targetValue = if (fadingCondition) 1f else 0f,
        animationSpec = tween(durationMillis = 1000),
    )

    Button(
        onClick = { onClick() },
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        border = BorderStroke(1.dp, Color.White),
        modifier = modifier
            .width(150.dp)
            .graphicsLayer(alpha = visibility),
    ) {
        Text(text = "Reset", color = Color.White)
    }
}

@Composable
fun FadingText(
    modifier: Modifier = Modifier,
    text: String = "This is a sentence.",
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontWeight: FontWeight = FontWeight.Normal,
    fadingCondition: Boolean = true,
) {

    val visibility: Float by animateFloatAsState(
        targetValue = if (fadingCondition) 0f else 1f,
        animationSpec = tween(durationMillis = 1000),
    )

    Text(
        text = text,
        color = color,
        fontSize = fontSize,
        fontWeight = fontWeight,
        maxLines = 1,
        modifier = modifier.graphicsLayer(alpha = visibility)
    )
}