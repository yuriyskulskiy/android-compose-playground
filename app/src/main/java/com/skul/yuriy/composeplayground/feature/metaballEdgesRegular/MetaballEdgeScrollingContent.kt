package com.skul.yuriy.composeplayground.feature.metaballEdgesRegular

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollFactory
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.skul.yuriy.composeplayground.R
import com.skul.yuriy.composeplayground.feature.customBlur.util.alphaThreshold40PercentEffect

@OptIn(ExperimentalFoundationApi::class)
@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun MetaballEdgeScrollingContent(
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()

    val verticalParam = 56.dp
    val text = stringResource(R.string.very_long_mock_text).trimIndent()
    val textModifier = Modifier.padding(24.dp)
//    val textStyle = MaterialTheme.typography.titleLarge
    val textStyle = MaterialTheme.typography.displayMedium
    val textColor = Color.Black
    val textWeight = FontWeight.Normal
    val blurRadius = 6.dp

    CompositionLocalProvider(LocalOverscrollFactory provides null) {
        MetaballScrollText(
            modifier = modifier,
            blurRadius = blurRadius,
            scrollState = scrollState,
            verticalParam = verticalParam,
            text = text,
            textModifier = textModifier,
            textStyle = textStyle,
            textColor = textColor,
            textWeight = textWeight,
        )
    }
}

@Composable
private fun MetaballScrollText(
    modifier: Modifier,
    scrollState: ScrollState,
    verticalParam: Dp,
    text: String,
    textModifier: Modifier,
    textStyle: androidx.compose.ui.text.TextStyle,
    textColor: Color,
    blurRadius: Dp,
    textWeight: FontWeight,
) {
    Box(
        modifier.fillMaxSize()
    ) {

        // real scrolling content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
        ) {
            Text(
                style = textStyle,
                color = textColor,
                text = text,
                modifier = textModifier,
                fontWeight = textWeight
            )
        }

        //workaround with overlay: top blur text area and bottom blur text area
        BoxWithConstraints(
            modifier = Modifier
                .graphicsLayer {
                    this.renderEffect = alphaThreshold40PercentEffect
                }
                .fillMaxSize()
                .align(Alignment.TopStart)
                .zIndex(1f)
        ) {
            val density = LocalDensity.current
            val textHeighPx = with(density) { verticalParam.toPx() }
            val parentHeightPx = with(density) { maxHeight.toPx() }

           //top gradient edge
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopStart)
                    .height(verticalParam)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.4f),
                                Color.Black.copy(alpha = 0.0f)
                            )
                        )
                    )
            )
            //bottom gradient edge
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)
                    .height(verticalParam)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.0f),
                                Color.Black.copy(alpha = 0.4f),
                            )
                        )
                    )
            )

            //top blurred text
            BlurredTextOverlay(
                blurRadius = blurRadius,
                text = text,
                modifier = Modifier
                    .fillMaxWidth()
//                    .border(width = 1.dp, color = Color.Red)
                    .gradientTopButtonEdges(
                        topFadeHeight = verticalParam,
                        bottomFadeHeight = 0.dp,
                    ),
                textModifier = textModifier,
                style = textStyle,
                color = textColor,
                fontWeight = textWeight,
                blurHeight = verticalParam,
                topOffset = 0f,
                scrollOffsetPx = scrollState.value.toFloat(),
            )

            //bottom blurred text
            BlurredTextOverlay(
                blurRadius = blurRadius,
                text = text,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)
                    .gradientTopButtonEdges(
                        topFadeHeight = 0.dp,
                        bottomFadeHeight = verticalParam
                    ),
                textModifier = textModifier,
                style = textStyle,
                color = textColor,
                fontWeight = textWeight,
                blurHeight = verticalParam,
                topOffset = parentHeightPx - textHeighPx,
                scrollOffsetPx = scrollState.value.toFloat(),
            )
        }
    }
}
