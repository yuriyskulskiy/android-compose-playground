package com.skul.yuriy.composeplayground.feature.animatedRectButton

import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp

@Composable
fun GlowingText(
    text: String,
    textColor: Color,
    textSizeSp: Int,
    isPressed: Boolean,
    shadowOffset: Pair<Dp, Dp>,
    blurRadius: Dp
) {
    if (!isPressed) {
        Text(
            text = text,
            modifier = Modifier
                .offset(
                    x = shadowOffset.first,
                    y = shadowOffset.second
                )
                .blur(blurRadius),
            color = textColor.copy(alpha = 0.8f),
            fontSize = textSizeSp.sp
        )
    }

    Text(
        text = text,
        color = textColor,
        fontSize = textSizeSp.sp
    )
}
