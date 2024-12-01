package com.skul.yuriy.composeplayground.feature.metaballBlur

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.skul.yuriy.composeplayground.feature.metaballBlur.model.RenderEffectEntity

@Composable
fun RadioButtonWithLabel(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        RadioButton(
            selected = isSelected,
            onClick = null
        )
        Text(text = label, modifier = Modifier.padding(start = 4.dp))
    }
}

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun RenderEffectSelector(
    selectedRenderEffect: androidx.compose.ui.graphics.RenderEffect?,
    renderEffectMap: Map<Int, RenderEffectEntity>,
    onRenderEffectSelectedById: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .background(Color.White.copy(alpha = 0.5f))
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        renderEffectMap.values.forEach { entity ->
            RadioButtonWithLabel(
                label = entity.displayName,
                isSelected = entity.renderEffect == selectedRenderEffect,
                onClick = {
                    onRenderEffectSelectedById(entity.id)
                }
            )

            Spacer(modifier = Modifier.width(16.dp))
        }
    }
}