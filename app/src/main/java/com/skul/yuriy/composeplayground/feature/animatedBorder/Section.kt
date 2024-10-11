package com.skul.yuriy.composeplayground.feature.animatedBorder

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@Composable
fun LabeledSectionWrapper(
    modifier: Modifier = Modifier,
    isBorderEnabled: Boolean,
    text: String,
    shadowBox: @Composable (Modifier) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Pass the modifier with conditional border based on isBorderEnabled
        shadowBox(
            modifier.then(
                if (isBorderEnabled) {
                    Modifier.border(BorderStroke(1.dp, Color.White))
                } else {
                    Modifier
                }
            )
        )
        Spacer(modifier = Modifier.size(24.dp))
        Text(text = text, color = Color.White)
    }
}