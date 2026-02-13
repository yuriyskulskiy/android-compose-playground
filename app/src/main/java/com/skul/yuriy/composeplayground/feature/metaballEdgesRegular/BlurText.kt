package com.skul.yuriy.composeplayground.feature.metaballEdgesRegular

import android.graphics.RenderEffect
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun BlurredTextOverlay(
    text: String,
    modifier: Modifier = Modifier,
    textModifier: Modifier = Modifier,
    style: TextStyle = TextStyle.Default,
    color: Color = Color.Unspecified,
    fontWeight: FontWeight? = null,
    blurRadius: Dp = 4.dp,
    topOffset: Float = 0f,
    scrollOffsetPx: Float = 0f,
    blurHeight: Dp,
) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
        Text(
            text = "Not supported for SDK lev < ${Build.VERSION_CODES.S}",
            modifier = modifier.then(textModifier),
//            style = MaterialTheme.typography.titleMedium,
            style = MaterialTheme.typography.displayMedium,
            color = Color.Red,
            fontWeight = fontWeight
        )
        return
    }

    TextWithTopBlurApi31(
        text = text,
        modifier = modifier,
        textModifier = textModifier,
        style = style,
        color = color,
        fontWeight = fontWeight,
        blurHeight = blurHeight,
        blurRadius = blurRadius,
        topOffset = topOffset,
        scrollOffsetPx = scrollOffsetPx,
    )
}

@RequiresApi(Build.VERSION_CODES.S)
@Composable
private fun TextWithTopBlurApi31(
    text: String,
    modifier: Modifier,
    textModifier: Modifier,
    style: TextStyle,
    color: Color,
    fontWeight: FontWeight?,
    blurHeight: Dp,
    blurRadius: Dp,
    topOffset: Float,
    scrollOffsetPx: Float,
) {
    val blurPx = with(LocalDensity.current) { blurRadius.toPx() }
    val blurEffect = remember(blurPx) {
        RenderEffect.createBlurEffect(blurPx, blurPx, android.graphics.Shader.TileMode.CLAMP)
    }

    Box(
        modifier = modifier
            .offset(y = 0.dp)
            .fillMaxWidth()
            .background(Color.Transparent)
            .graphicsLayer {
                renderEffect = blurEffect.asComposeRenderEffect()
            }
            .height(blurHeight)
            .clipToBounds()
    ) {
        Text(
            text = text,
            modifier = textModifier
                .graphicsLayer { translationY = -topOffset - scrollOffsetPx }
                .wrapContentHeight(unbounded = true, align = Alignment.Top),
            style = style,
            color = color,
            fontWeight = fontWeight
        )
    }

}
