package com.skul.yuriy.composeplayground.feature.parallax

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Scale
import coil.size.Size
import kotlin.math.max


@Composable
fun ScrollPositionListItem(
    itemUi: ListItemUi,
    listState: LazyListState,
    index: Int,
    modifier: Modifier = Modifier,
    viewportHeight: Float
) {


    val normalizedScrollProgress by remember(listState, viewportHeight) {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val visibleItemInfo = layoutInfo.visibleItemsInfo.firstOrNull { it.index == index }
            val itemInfo = visibleItemInfo?.let {
                val offsetY = it.offset - layoutInfo.viewportStartOffset
                val height = it.size
                offsetY to height
            } ?: (0 to 0)
            val (offsetY, height) = itemInfo
            computeNormalizedScrollProgress(offsetY = offsetY, height = height, viewportHeight = viewportHeight)
        }
    }

    ListItem(
        itemUi = itemUi,
        modifier = modifier,
        progress = normalizedScrollProgress,
        index = index
    )
}


@Composable
fun ListItem(
    modifier: Modifier = Modifier,
    index: Int,
    progress: Float,
    itemUi: ListItemUi
) {

    // Calculate spacer weights based on scroll progress, ensuring they are always positive
    val topWeight = max(0.01f, 1f - progress)
    val bottomWeight = max(0.01f, progress)

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(4 / 3f),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {

        var maxOffset by remember { mutableStateOf(1f) }
        var boxHeight by remember { mutableStateOf(1) }

        Box(modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned { coordinates ->
                val newHeight = coordinates.size.height
                val newWidth = coordinates.size.width
                boxHeight = newHeight
                maxOffset = (newWidth - newHeight)
                    .coerceAtLeast(0)
                    .toFloat()
            }
        ) {

            val currentVerticalOffset = maxOffset * (0.5f - progress)

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(itemUi.drawableRes)
                    .size(Size(boxHeight, boxHeight))
                    .scale(Scale.FILL)
                    .crossfade(true)
                    .build(),
                contentDescription = "Downscaled WebP Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .offset {
                        IntOffset(0, currentVerticalOffset.toInt())
                    },
            )

            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Spacer(modifier = Modifier.weight(topWeight))
                Text(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(color = Color.Black.copy(alpha = 0.4f))
                        .padding(4.dp),
                    color = Color.White,
                    text = stringResource(id = itemUi.textRes),
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.weight(bottomWeight))
            }
        }
    }
}


/**
 * Computes the progress of an item as it moves within the viewport.
 *
 * @param offsetY The vertical offset of the item from the top of the viewport.
 * @param height The height of the item.
 * @param viewportHeight The height of the viewport.
 * @return A normalized progress value from 0 to 1.
 */
fun computeNormalizedScrollProgress(offsetY: Int, height: Int, viewportHeight: Float): Float {
    return if (height > 0 && viewportHeight > 0) {
        val fullScrollPath = viewportHeight + height
        val itemTop = offsetY + height
        // Normalized progress calculation from 0 (item bottom at viewport bottom) to 1 (item top at viewport top)
        val visiblePortionTop = (fullScrollPath - itemTop) / fullScrollPath
        (1f - visiblePortionTop).coerceIn(0f, 1f)
    } else 0f
}