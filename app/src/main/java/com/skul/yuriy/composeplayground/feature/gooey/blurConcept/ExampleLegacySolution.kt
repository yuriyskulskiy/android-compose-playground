package com.skul.yuriy.composeplayground.feature.gooey.blurConcept

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.skul.yuriy.composeplayground.feature.gooey.blurConcept.util.BlurFilledTonalIconButton
import com.skul.yuriy.composeplayground.feature.gooey.blurConcept.util.StandardColorMatrixMetalBox



@Composable
fun ExampleLegacyContent(
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    val offsetDistance = (80).dp
    val buttonOffset by animateDpAsState(
        targetValue = if (isExpanded) offsetDistance else 0.dp,
        animationSpec = tween(durationMillis = 3000)
    )

    val color = Color.Black

    StandardColorMatrixMetalBox(
        modifier = modifier,
    ) {

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {

            BlurFilledTonalIconButton(
                icon = Icons.Filled.Done,
                containerColor = color,
                modifier = Modifier
                    .offset { IntOffset(x = -buttonOffset.roundToPx(), y = 0) },
                onClick = {
                    isExpanded = !isExpanded
                },
                contentDescription = null,
            )
            BlurFilledTonalIconButton(
                containerColor = color,
                modifier = Modifier
                    .offset { IntOffset(x = buttonOffset.roundToPx(), y = 0) },
                icon = Icons.Filled.Call,
                onClick = { isExpanded = !isExpanded },
                contentDescription = null
            )
        }
    }
}