package com.skul.yuriy.composeplayground.feature.gooey.blurConcept

import android.os.Build
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.skul.yuriy.composeplayground.feature.gooey.blurConcept.util.BlurCircularButton
import com.skul.yuriy.composeplayground.feature.gooey.blurConcept.util.MetaballBoxWithShaders
import com.skul.yuriy.composeplayground.feature.gooey.blurConcept.util.ShaderType

@Composable
fun ExampleRuntimeRenderEffectColorFix(
    modifier: Modifier = Modifier
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // API 33
        var isExpanded by remember { mutableStateOf(false) }
        var selectedShader by remember { mutableStateOf(ShaderType.Simple) } // State for selected shader type

        val offsetDistance = (80).dp
        val buttonOffset by animateDpAsState(
            targetValue = if (isExpanded) offsetDistance else 0.dp,
            animationSpec = tween(durationMillis = 3000)
        )

        val color = Color.Red

        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Top
        ) {
            // Shader Selection Radio Group
            Text(
                text = "AGSL Shader Type",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 0.dp, top = 8.dp, start = 8.dp)
            )
            ShaderTypeRadioGroup(
                selectedShader = selectedShader,
                onShaderSelected = { selectedShader = it }
            )

            // Metaball Box with selected shader
            MetaballBoxWithShaders(
                shaderType = selectedShader,
                color = color
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp)
                ) {
                    BlurCircularButton(
                        Modifier.offset { IntOffset(x = -buttonOffset.roundToPx(), y = 0) },
                        onClick = { isExpanded = !isExpanded },
                        color = color
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Done,
                            contentDescription = null,
                            tint = Color.White,
                        )
                    }
                    BlurCircularButton(
                        Modifier.offset { IntOffset(x = +buttonOffset.roundToPx(), y = 0) },
                        onClick = { isExpanded = !isExpanded },
                        color = color
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Call,
                            contentDescription = null,
                            tint = Color.White,
                        )
                    }
                }
            }
        }
    } else {
        Box(
            modifier = modifier
                .height(100.dp)
                .padding(horizontal = 24.dp)
                .border(2.dp, Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Simple Runtime shader is missing. This feature requires Android 13 (API 33) or higher.",
                color = Color.Red
            )
        }
    }
}

@Composable
private fun ShaderTypeRadioGroup(
    selectedShader: ShaderType,
    onShaderSelected: (ShaderType) -> Unit
) {
    val shaderOptions = listOf(
        ShaderType.Simple to "Simple",
        ShaderType.FixColor to "Fix Color",
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        shaderOptions.forEach { (shaderType, name) ->
            ShaderRadioButton(
                shaderType = shaderType,
                name = name,
                isSelected = shaderType == selectedShader,
                onShaderSelected = onShaderSelected
            )
        }
    }
}

@Composable
fun ShaderRadioButton(
    shaderType: ShaderType,
    name: String,
    isSelected: Boolean,
    onShaderSelected: (ShaderType) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable { onShaderSelected(shaderType) }
            .padding(horizontal = 0.dp)
    ) {
        RadioButton(
            selected = isSelected,
            onClick = { onShaderSelected(shaderType) }
        )
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}

