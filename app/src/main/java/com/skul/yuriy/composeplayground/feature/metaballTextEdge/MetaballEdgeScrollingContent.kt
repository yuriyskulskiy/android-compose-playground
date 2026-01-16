package com.skul.yuriy.composeplayground.feature.metaballTextEdge

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.skul.yuriy.composeplayground.R
import com.skul.yuriy.composeplayground.util.fadingTopBottomEdgesDp

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun MetaballEdgeScrollingContent(
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()

    val verticalParam = 56.dp

    Box(
        modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)

        ) {
            Text(
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black,
                text = stringResource(R.string.very_long_mock_text).trimIndent(),
                modifier = Modifier
                    .padding(24.dp),
                fontWeight = FontWeight.Normal
            )
        }
//////////////////////////////////////////////////////////////////////

        var parentHeightPx by remember { mutableStateOf(0) }
        Box(
            modifier = Modifier
                .graphicsLayer {
                    this.renderEffect = singleEffect
                }
                .fillMaxSize()
                .align(Alignment.TopStart)
                .zIndex(1f)
                .onGloballyPositioned { coords ->
                    parentHeightPx = coords.size.height
                }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopStart)
                    .height(verticalParam)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.245f),
                                Color.Black.copy(alpha = 0.0f)
                            )
                        )
                    )
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)
                    .height(verticalParam)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.0f),
                                Color.Black.copy(alpha = 0.245f),
                            )
                        )
                    )
            )

            TextWithTopBlur(
                text = stringResource(R.string.very_long_mock_text).trimIndent(),
                modifier = Modifier
                    .fillMaxWidth()
                    .gradientTopButtonEdges(
                        topFadeHeight = verticalParam,
                    ),

                textModifier = Modifier
                    .padding(24.dp),
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black,
                fontWeight = FontWeight.Normal,
                blurHeight = verticalParam,
                topOffset = 0f,
                scrollOffsetPx = scrollState.value.toFloat(),
            )


            val textHeighPx = with(LocalDensity.current) { verticalParam.toPx() }

            TextWithTopBlur(
                text = stringResource(R.string.very_long_mock_text).trimIndent(),
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)
                    .gradientTopButtonEdges(
                        bottomFadeHeight = verticalParam
                    ),
                textModifier = Modifier
                    .padding(24.dp),
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black,
                fontWeight = FontWeight.Normal,
                blurHeight = verticalParam,

                topOffset = parentHeightPx - textHeighPx,
                scrollOffsetPx = scrollState.value.toFloat(),
            )
        }
    }
}
