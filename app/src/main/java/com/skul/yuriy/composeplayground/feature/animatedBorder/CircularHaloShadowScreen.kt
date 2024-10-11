package com.skul.yuriy.composeplayground.feature.animatedBorder

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.skul.yuriy.composeplayground.R
import com.skul.yuriy.composeplayground.util.cornerRedLinearGradient


@Composable
fun CircularHaloShadowScreen() {
    var isBorderEnabled by remember { mutableStateOf(false) }
    Box(
        Modifier
            .fillMaxSize()
            .background(brush = cornerRedLinearGradient(), alpha = 1f)
            .drawBehind {
                drawRect(
                    color = Black.copy(alpha = 0.6f),
                )
            },

        contentAlignment = Alignment.Center
    ) {

        OutlinedButton(
            onClick = {
                isBorderEnabled = !isBorderEnabled // Toggle the border visibility
            },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(32.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = White
            ),
            border = BorderStroke(2.dp, White)
        ) {
            Text(
                text = if (isBorderEnabled) stringResource(R.string.composable_border_on) else stringResource(
                    R.string.composable_border_off
                )
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ScreenContent(
                isBorderEnabled = isBorderEnabled,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}