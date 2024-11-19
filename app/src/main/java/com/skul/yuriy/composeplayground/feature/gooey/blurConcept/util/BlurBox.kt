package com.skul.yuriy.composeplayground.feature.gooey.blurConcept.util

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun BoxScope.BlurCircularButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    color: Color = Color.Black,
    blur: Dp = 28.dp,
    size: Dp = 100.dp,
    content: @Composable BoxScope.() -> Unit
) {
    BlurBox(
        modifier = modifier
            .clickable(
                interactionSource = remember {
                    MutableInteractionSource()
                },
                indication = null,
                onClick = onClick,
            )
            .align(Alignment.Center),
        blur = blur,
        metaContent = {
            Box(
                Modifier
                    .background(color, CircleShape)
                    .fillMaxSize()
            )
        }
    ) {
        Box(
            Modifier.size(size),
            content = content,
            contentAlignment = Alignment.Center,
        )
    }
}

@Composable
fun BlurBox(
    modifier: Modifier = Modifier,
    blur: Dp = 24.dp,
    metaContent: @Composable BoxScope.() -> Unit,
    content: @Composable BoxScope.() -> Unit = {},
) {

    Box(
        modifier
            .width(IntrinsicSize.Min)
            .height(IntrinsicSize.Min),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .blur(blur, BlurredEdgeTreatment.Unbounded),
            content = metaContent,
        )
        content()
    }
}

