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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.skul.yuriy.composeplayground.R

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun MetaballEdgeScrollingContent(modifier: Modifier = Modifier) {
    val scrollState = rememberScrollState()
    val renderEffect = rememberTextMetaballRenderEffect()

    Box(modifier
        .fillMaxSize()
        .graphicsLayer {
//            this.renderEffect = renderEffect
            this.renderEffect = singleEffect
        }) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.24f),
                            Color.Black.copy(alpha = 0f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)

        ) {
            TextWithTopBlur(
//            Text(
                style = MaterialTheme.typography.headlineLarge,
                color = Color.Black,
                text = stringResource(R.string.very_long_mock_text).trimIndent(),
                modifier = Modifier.padding(16.dp),
                fontWeight = FontWeight.Normal
            )
        }


    }
}