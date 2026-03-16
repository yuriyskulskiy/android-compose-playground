package com.skul.yuriy.composeplayground.feature.overflowText.investigate.implementation

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.skul.yuriy.composeplayground.R

@Composable
internal fun FlowText_try2(
    modifier: Modifier = Modifier,
) {
    val text = stringResource(R.string.very_long_mock_text_paragraphs)
    val density = LocalDensity.current
    var topOffset by remember { mutableStateOf(FlowTextDefaults.FloatingBoxTopOffset) }
    var endOffset by remember { mutableStateOf(FlowTextDefaults.FloatingBoxEndOffset) }
    val config =
        FlowTextDefaults.floatingBoxConfig(
            topOffset = topOffset,
            endOffset = endOffset,
        )

    FlowText(
        text = "$text\n\n$text",
        config = config,
        modifier = modifier.fillMaxWidth(),
        style = MaterialTheme.typography.bodyMedium,
    ) {
        Image(
            painter = painterResource(R.drawable.ic_user_placeholder),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier =
                Modifier
                    .fillMaxSize()
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline,
                    )
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            topOffset = (topOffset + with(density) { dragAmount.y.toDp() }).coerceAtLeast(0.dp)
                            endOffset = (endOffset - with(density) { dragAmount.x.toDp() }).coerceAtLeast(0.dp)
                        }
                    }
                    .padding(8.dp),
        )
    }
}
