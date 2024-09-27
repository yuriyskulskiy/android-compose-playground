package com.skul.yuriy.composeplayground.feature.parallax

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.math.max

// another way to track item height and offset
//.onGloballyPositioned { layoutCoordinates ->
//    // Capture the height of the item
//    itemHeight = layoutCoordinates.size.height.toFloat()
//    itemOffsetY = layoutCoordinates.positionInParent().y
//    },


@Composable
fun ParallaxListItem(
    listState: LazyListState,
    index: Int,
    modifier: Modifier = Modifier,
    viewportHeight: Float
) {


    val progress by remember(listState, viewportHeight) {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val visibleItemInfo = layoutInfo.visibleItemsInfo.firstOrNull { it.index == index }
            val itemInfo = visibleItemInfo?.let {
                val offsetY = it.offset - layoutInfo.viewportStartOffset
                val height = it.size
                offsetY to height
            } ?: (0 to 0)
            val (offsetY, height) = itemInfo
            computeProgress(offsetY = offsetY, height = height, viewportHeight = viewportHeight)
        }
    }

    ListItem(
        modifier = modifier,
        progress = progress,
        index = index
    )
}


@Composable
fun ListItem(
    modifier: Modifier = Modifier,
    index: Int,
    progress: Float
) {
    // Weights for spacers to create the parallax effect
    val topWeight = max(0.01f, 1f - progress)
    val bottomWeight = max(0.01f, progress)

    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (index == 4) {
                Color.LightGray
            } else {
                Color.White
            }
        ),
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(4 / 3f),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.weight(topWeight))
            Text(
                text = "Parallax text...",
                style = MaterialTheme.typography.headlineLarge
            )
            Spacer(modifier = Modifier.weight(bottomWeight))
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
fun computeProgress(offsetY: Int, height: Int, viewportHeight: Float): Float {
    return if (height > 0 && viewportHeight > 0) {
        val fullScrollPath = viewportHeight + height
        val itemTop = offsetY + height
        // Normalized progress calculation from 0 (item bottom at viewport bottom) to 1 (item top at viewport top)
        val visiblePortionTop = (fullScrollPath - itemTop) / fullScrollPath
        (1f - visiblePortionTop).coerceIn(0f, 1f)
    } else 0f
}