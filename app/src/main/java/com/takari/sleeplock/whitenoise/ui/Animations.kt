package com.takari.sleeplock.whitenoise.ui

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.takari.sleeplock.whitenoise.data.allWhiteNoises
import java.lang.Math.abs


@Preview
@Composable
fun ZoomingAnimationTest() {
    val listState = rememberLazyListState()

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val halfRowWidth = constraints.maxWidth / 2

        LazyRow(state = listState) {
            itemsIndexed(allWhiteNoises) { i, item ->
                val opacity by remember {
                    derivedStateOf {
                        val currentItemInfo = listState.layoutInfo.visibleItemsInfo
                            .firstOrNull { it.index == i }
                            ?: return@derivedStateOf 0.5f

                        val itemHalfSize = currentItemInfo.size / 2
                        val distanceFromCenter =
                            abs(currentItemInfo.offset + itemHalfSize - halfRowWidth).toFloat() / halfRowWidth
                        val scrollDistance = (1f - minOf(1f, distanceFromCenter * 0.1f))

                        scrollDistance
                    }
                }

                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(item.image())
                        .diskCachePolicy(CachePolicy.ENABLED)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .scale(opacity)
                        .alpha(opacity)
                        .fillParentMaxSize()
                )
            }
        }
    }
}


/**
 * The Ken Burns effect is a type of panning and zooming effect used in film and video production
 * from still imagery. This is only intended to be used on image related composables.
 *
 * [Wiki](https://en.wikipedia.org/wiki/Ken_Burns_effect)
 */
fun Modifier.addKensBurnEffect(): Modifier = composed {
    var animated by remember { mutableStateOf(false) }
    val startLocation = Offset(250F, 0F)
    val endLocation = Offset(-250F, 0F)

    val scale: Float by animateFloatAsState(
        targetValue = if (animated) 1.5f else 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 30000,
                easing = LinearOutSlowInEasing
            ), repeatMode = RepeatMode.Reverse
        )
    )

    LaunchedEffect(Unit) { animated = true }

    val panningOffset by animateOffsetAsState(
        targetValue = if (animated) endLocation else startLocation,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 20000,
                easing = LinearOutSlowInEasing
            ), repeatMode = RepeatMode.Reverse
        )
    )

    graphicsLayer(
        translationX = panningOffset.x,
        translationY = panningOffset.y,
        scaleX = scale,
        scaleY = scale
    )
}