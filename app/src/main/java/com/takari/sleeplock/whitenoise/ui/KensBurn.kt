package com.takari.sleeplock.whitenoise.ui

import android.util.Log
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.takari.sleeplock.R




/**
 * TODO figure out how to dynamically detect the x and y boarder for an image so your
 * animation doesn't go out of bounds.
 */
@Preview(showBackground = true)
@Composable
fun KensBurnImage(imageID: Int = R.drawable.oceanshore) {
    val duration = 25000

    var animated by remember { mutableStateOf(false) }
    val startLocation = Offset(250F, 0F)
    val endLocation = Offset(-250F, 0F)

    val scale: Float by animateFloatAsState(
        targetValue = if (animated) 1f else .75f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = duration,
                easing = LinearOutSlowInEasing
            ), repeatMode = RepeatMode.Reverse
        )
    )

    LaunchedEffect(Unit) { animated = true }

    val panningOffset by animateOffsetAsState(
        targetValue = if (animated) endLocation else startLocation,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = duration,
                easing = LinearOutSlowInEasing
            ), repeatMode = RepeatMode.Reverse
        )
    )

    Image(
        painter = painterResource(id = imageID),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .offset(panningOffset.x.dp, panningOffset.y.dp)
            .wrapContentSize(unbounded = true, align = Alignment.Center)
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
            )
    )
}

/**
 * States:
 *  Zooming
 *  Translation Start
 *  Translation End
 */
@Composable
fun AsyncKensBurnImage(imageID: Int = R.drawable.oceanshore, imageModifier: Modifier) {
    val duration = 30000
    var animated by remember { mutableStateOf(false) }
    val startLocation = Offset(250F, 0F)
    val endLocation = Offset(-250F, 0F)

    val scale: Float by animateFloatAsState(
        targetValue = if (animated) 1.5f else 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = duration,
                easing = LinearOutSlowInEasing
            ), repeatMode = RepeatMode.Reverse
        )
    )

    LaunchedEffect(Unit) { animated = true }

    val panningOffset by animateOffsetAsState(
        targetValue = if (animated) endLocation else startLocation,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = duration,
                easing = LinearOutSlowInEasing
            ), repeatMode = RepeatMode.Reverse
        )
    )

    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageID)
            .diskCachePolicy(CachePolicy.ENABLED)
            .crossfade(true)
            .build(),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = imageModifier
            .graphicsLayer(
                translationX = panningOffset.x,
                translationY = panningOffset.y,
                scaleX = scale,
                scaleY = scale
            )
    )
}


@Preview(showBackground = true)
@Composable
fun KensBurnImageAlternate(imageID: Int = R.drawable.oceanshore) {
    val duration = 20000

    var animated by remember { mutableStateOf(false) }
    var panningStartX by remember { mutableStateOf(0f) }
    var panningEndX by remember { mutableStateOf(250f) }

    val startLocation = Offset(0F, 0F)
    val endLocation = Offset(250F, 0F)

    val scale: Float by animateFloatAsState(
        targetValue = if (animated) 1f else .75f,
        animationSpec = tween(durationMillis = duration, easing = LinearOutSlowInEasing),
        finishedListener = { Log.d("my-tag", "scale finished") }

    )

    LaunchedEffect(Unit) { animated = true }

    val panningOffset by animateOffsetAsState(
        targetValue = if (animated) endLocation else startLocation,
        animationSpec = tween(durationMillis = duration, easing = LinearOutSlowInEasing),
        finishedListener = {
            Log.d("my-tag", "panning finished")
            panningStartX = panningEndX.apply { panningEndX = panningStartX } // swap values
            animated = !animated
        }
    )

    Image(
        painter = painterResource(id = R.drawable.oceanshore),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .offset(panningOffset.x.dp, panningOffset.y.dp)
            .wrapContentSize(unbounded = true, align = Alignment.Center)
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
            )
    )
}
