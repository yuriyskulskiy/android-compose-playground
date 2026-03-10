package com.skul.yuriy.composeplayground.feature.customAlphaBlurRadial

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
internal fun BoxScope.CornerBar(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    cornerRadius: Dp,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .align(Alignment.BottomEnd)
            .size(cornerRadius * 2)
            .offset(x = cornerRadius, y = cornerRadius)
            .clip(CircleShape)
            .background(Color.Black)
    ) {
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedTrackColor = Color.Red
            ),
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(6.dp)
        )
    }
}
