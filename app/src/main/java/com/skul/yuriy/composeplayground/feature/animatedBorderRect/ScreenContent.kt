package com.skul.yuriy.composeplayground.feature.animatedBorderRect

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.skul.yuriy.composeplayground.R
import com.skul.yuriy.composeplayground.feature.animatedBorderRect.border.agsladvanced.FireShaderDraftRectShadowBox
import com.skul.yuriy.composeplayground.feature.animatedBorderRect.border.agslsimple.SimpleAgslBorderRectShadowBox
import com.skul.yuriy.composeplayground.feature.animatedBorderRect.border.agslsimple.SimpleAgslRenderMode
import com.skul.yuriy.composeplayground.feature.animatedBorderRect.border.blurmask.BlurredRectShadowBox
import com.skul.yuriy.composeplayground.feature.animatedBorderRect.border.gradient.GradientRectShadowBox
import com.skul.yuriy.composeplayground.feature.animatedBorderRect.border.multilayer.MultiLayerRectShadowBox
import com.skul.yuriy.composeplayground.feature.animatedBorderRect.border.shadowlayer.ShadowLayerRectShadowBox

@Composable
fun ScreenContent(
    modifier: Modifier = Modifier
) {
    val cornerRadius = 24.dp
    val shadowBoxWidth = 220.dp
    val sectionModifier = Modifier.fillMaxWidth()
    val shadowBoxModifier = Modifier
        .height(120.dp)
        .width(shadowBoxWidth)
    var multiLayerCount by remember { mutableIntStateOf(30) }
    var shadowLayerPasses by remember { mutableIntStateOf(2) }
    var blurMaskBlurRadiusDp by remember { mutableIntStateOf(16) }
    var showAdvancedAgsl by remember { mutableStateOf(false) }
    var simpleAgslRenderMode by remember { mutableStateOf(SimpleAgslRenderMode.RenderEffect) }
    var showBorderParts by remember { mutableStateOf(true) }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        RectLabeledSectionWrapper(
            modifier = sectionModifier,
            text = stringResource(R.string.multi_layer_shadow),
            aboveTitleContent = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedLayerStepButton(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Decrease layers",
                        enabled = multiLayerCount > 0,
                        onClick = {
                            multiLayerCount = prevLayerCount(multiLayerCount)
                        }
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Layers $multiLayerCount",
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    OutlinedLayerStepButton(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Increase layers",
                        enabled = multiLayerCount < 120,
                        onClick = {
                            multiLayerCount = nextLayerCount(
                                current = multiLayerCount,
                                max = 120
                            )
                        }
                    )
                }
            }
        ) {
            MultiLayerRectShadowBox(
                modifier = shadowBoxModifier,
                color = Color.Green,
                cornerRadius = cornerRadius,
                initialHaloBorderWidth = 4.dp,
                pressedHaloBorderWidth = 36.dp,
                layersCount = multiLayerCount
            )
        }
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp,
            color = Color.White.copy(alpha = 0.18f)
        )

        RectLabeledSectionWrapper(
            modifier = sectionModifier,
            text = stringResource(R.string.paint_with_blurmaskfilter),
            aboveTitleContent = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedLayerStepButton(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Decrease blur radius",
                            enabled = blurMaskBlurRadiusDp > 2,
                            onClick = { blurMaskBlurRadiusDp = (blurMaskBlurRadiusDp - 2).coerceAtLeast(2) }
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Blur ${blurMaskBlurRadiusDp}dp",
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        OutlinedLayerStepButton(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Increase blur radius",
                            enabled = blurMaskBlurRadiusDp < 48,
                            onClick = { blurMaskBlurRadiusDp = (blurMaskBlurRadiusDp + 2).coerceAtMost(48) }
                        )
                    }
                }
            }
        ) {
            BlurredRectShadowBox(
                modifier = shadowBoxModifier,
                color = Color.Green,
                cornerRadius = cornerRadius,
                initialBlurRadius = 4.dp,
                pressedBlurRadius = blurMaskBlurRadiusDp.dp,
                initialHaloShadowWidth = 4.dp,
                pressedHaloShadowWidth = 32.dp
            )
        }
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp,
            color = Color.White.copy(alpha = 0.18f)
        )

        RectLabeledSectionWrapper(
            modifier = sectionModifier,
            text = stringResource(R.string.outlined_shadow_layer),
            aboveTitleContent = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedLayerStepButton(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Decrease shadow passes",
                        enabled = shadowLayerPasses > 0,
                        onClick = { shadowLayerPasses-- }
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Shadow Passes $shadowLayerPasses",
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    OutlinedLayerStepButton(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Increase shadow passes",
                        enabled = shadowLayerPasses < 20,
                        onClick = { shadowLayerPasses++ }
                    )
                }
            }
        ) {
            ShadowLayerRectShadowBox(
                modifier = shadowBoxModifier,
                color = Color.Red,
                cornerRadius = cornerRadius,
                initialHaloBorderWidth = 4.dp,
                pressedHaloBorderWidth = 28.dp,
                passesCount = shadowLayerPasses
            )
        }
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp,
            color = Color.White.copy(alpha = 0.18f)
        )

        RectLabeledSectionWrapper(
            modifier = sectionModifier,
            text = stringResource(R.string.radial_linear_gradient_border),
            aboveTitleContent = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(R.string.show_border_parts),
                        color = Color.White.copy(alpha = if (showBorderParts) 1f else 0.55f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Switch(
                        checked = showBorderParts,
                        onCheckedChange = { showBorderParts = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color.White.copy(alpha = 0.45f),
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = Color.White.copy(alpha = 0.2f)
                        )
                    )
                }
            }
        ) {
            GradientRectShadowBox(
                modifier = shadowBoxModifier,
                color = Color.Yellow,
                cornerRadius = cornerRadius,
                initialHaloBorderWidth = 4.dp,
                pressedHaloBorderWidth = 36.dp,
                split = showBorderParts
            )
        }
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp,
            color = Color.White.copy(alpha = 0.18f)
        )

        RectLabeledSectionWrapper(
            modifier = sectionModifier
                .graphicsLayer { clip = true },
            text = stringResource(R.string.simple_agsl_border),
            aboveTitleContent = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RenderModeRadioOption(
                        label = "RenderEffect",
                        selected = simpleAgslRenderMode == SimpleAgslRenderMode.RenderEffect,
                        onClick = { simpleAgslRenderMode = SimpleAgslRenderMode.RenderEffect }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    RenderModeRadioOption(
                        label = "Canvas Paint",
                        selected = simpleAgslRenderMode == SimpleAgslRenderMode.CanvasPaint,
                        onClick = { simpleAgslRenderMode = SimpleAgslRenderMode.CanvasPaint }
                    )
                }
            }
        ) {
            SimpleAgslBorderRectShadowBox(
                modifier = shadowBoxModifier,
                color = Color(red = 0.10f, green = 0.30f, blue = 1.00f, alpha = 1f),
                cornerRadius = cornerRadius,
                maxHaloBorderWidth = 32.dp,
                renderMode = simpleAgslRenderMode
            )
        }
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp,
            color = Color.White.copy(alpha = 0.18f)
        )

        RectLabeledSectionWrapper(
            modifier = sectionModifier,
            text = stringResource(R.string.fire_shader_draft),
            topSpacerHeight = 8.dp
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(268.dp)
            ) {
                OutlinedButton(
                    onClick = { showAdvancedAgsl = !showAdvancedAgsl },
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(start = 12.dp, top = 8.dp),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.7f)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.White
                    )
                ) {
                    Text(text = if (showAdvancedAgsl) "Stop" else "Start")
                }

                if (showAdvancedAgsl) {
                    FireShaderDraftRectShadowBox(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 48.dp)
                            .size(width = 340.dp, height = 220.dp),
                        cornerRadius = 24.dp,
                        bandWidth = 14.dp,
                        contourWidth = 220.dp,
                        contourHeight = 120.dp
                    )
                }
            } 
        }
    }
}

@Composable
private fun RenderModeRadioOption(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = Color.White,
                unselectedColor = Color.White.copy(alpha = 0.55f)
            )
        )
        Text(
            text = label,
            color = Color.White
        )
    }
}

@Composable
private fun OutlinedLayerStepButton(
    imageVector: ImageVector,
    contentDescription: String,
    enabled: Boolean,
    onClick: () -> Unit,
    size: Dp = 36.dp
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val borderColor = if (isPressed) Color.White else Color.White.copy(alpha = 0.7f)

    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        interactionSource = interactionSource,
        contentPadding = PaddingValues(0.dp),
        modifier = Modifier.size(size),
        border = BorderStroke(1.dp, borderColor),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (isPressed) Color.White.copy(alpha = 0.16f) else Color.Transparent,
            contentColor = if (enabled) Color.White else Color.White.copy(alpha = 0.45f),
            disabledContainerColor = Color.Transparent,
            disabledContentColor = Color.White.copy(alpha = 0.45f)
        )
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription
        )
    }
}

private fun nextLayerCount(current: Int, max: Int): Int {
    if (current < 0) return 0
    if (current >= max) return max

    return when {
        current < 5 -> (current + 1).coerceAtMost(max)
        current < 10 -> minOf(current + 2, 10, max)
        else -> {
            val next = if (current % 5 == 0) current + 5 else ((current / 5) + 1) * 5
            next.coerceAtMost(max)
        }
    }
}

private fun prevLayerCount(current: Int): Int {
    if (current <= 0) return 0

    return when {
        current <= 5 -> current - 1
        current <= 10 -> maxOf(current - 2, 5)
        else -> {
            val prev = if (current % 5 == 0) current - 5 else (current / 5) * 5
            prev.coerceAtLeast(10)
        }
    }
}
