package com.takari.sleeplock.sleeptimer.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.takari.sleeplock.R
import com.takari.sleeplock.sleeptimer.service.SleepTimerService
import com.takari.sleeplock.shared.theme.DarkBackground
import com.takari.sleeplock.shared.theme.DeepBlue


@Preview
@Composable
fun SleepTimerScreen(viewModel: SleepTimerViewModel = viewModel()) {
    val sleepTimerUiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {

        Box {

            Image(
                painter = painterResource(id = R.drawable.ic_wave),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .fillMaxWidth()
                    .size(200.dp)
            )

            Image(
                painter = painterResource(id = R.drawable.good_night),
                contentDescription = null,
                modifier = Modifier
                    .padding(top = 50.dp, start = 50.dp)
                    .height(175.dp)
                    .width(150.dp)
            )
        }

        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                text = sleepTimerUiState.elapseTime,
                color = Color.White,
                fontSize = 72.sp,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 128.dp)
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier.fillMaxSize()
            ) {

                sleepTimerUiState.timerServiceIsRunning
                sleepTimerUiState.isTimerRunning

                VerticalSlidingButton(
                    modifier = Modifier
                        .padding(bottom = 36.dp)
                        .align(Alignment.CenterHorizontally)
                        .width(200.dp),
                    slidingCondition = sleepTimerUiState.timerServiceIsRunning,
                    startAxis = 1f,
                    endAxis = -150f,
                    colors = ButtonDefaults.buttonColors(containerColor = DeepBlue),
                    onClick = {
                        viewModel.onStartButtonClick(
                            serviceIsRunning = SleepTimerService.isRunning(),
                            timerIsRunning = SleepTimerService.timerIsRunning()
                        )
                    },
                    content = {
                        val buttonText = when {
                            sleepTimerUiState.timerServiceIsRunning and sleepTimerUiState.isTimerRunning -> "Pause"
                            sleepTimerUiState.timerServiceIsRunning and !sleepTimerUiState.isTimerRunning -> "Resume"
                            else -> "Start"
                        }

                        Text(text = buttonText, color = Color.White)
                    }
                )

                VerticalSlidingButton(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .width(200.dp),
                    slidingCondition = sleepTimerUiState.timerServiceIsRunning,
                    startAxis = 250f,
                    endAxis = -225f,
                    colors = ButtonDefaults.buttonColors(containerColor = DeepBlue),
                    onClick = { viewModel.resetState() },
                    content = { Text(text = "Reset", color = Color.White) }
                )
            }
        }
    }
}


@Preview
@Composable
fun VerticalSlidingButton(
    modifier: Modifier = Modifier,
    slidingCondition: Boolean = true,
    startAxis: Float = 1f,
    endAxis: Float = 250f,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    content: @Composable () -> Unit = {},
    onClick: () -> Unit = {}
) {

    val yAxis: Float by animateFloatAsState(
        targetValue = if (slidingCondition) endAxis else startAxis,
        animationSpec = tween(durationMillis = 500),
    )

    Button(
        onClick = { onClick() },
        colors = colors,
        modifier = modifier.graphicsLayer(translationY = yAxis),
    ) {
        content()
    }
}
