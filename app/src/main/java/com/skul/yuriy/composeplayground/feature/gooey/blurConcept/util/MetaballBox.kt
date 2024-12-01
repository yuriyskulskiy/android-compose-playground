package com.skul.yuriy.composeplayground.feature.gooey.blurConcept.util

import android.graphics.ColorMatrixColorFilter
import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import com.skul.yuriy.composeplayground.feature.gooey.blurConcept.util.shader.ShaderSourceProperColoring
import com.skul.yuriy.composeplayground.feature.gooey.blurConcept.util.shader.ShaderSourceSimple
import com.skul.yuriy.composeplayground.feature.gooey.blurConcept.util.shader.ShaderSource_outlined_color_test_1
import com.skul.yuriy.composeplayground.feature.gooey.blurConcept.util.shader.ShaderSource_outlined_marker_color
import com.skul.yuriy.composeplayground.feature.gooey.blurConcept.util.shader.ShaderSource_outlined_marker_color_and_marker_brightness
import com.skul.yuriy.composeplayground.feature.gooey.blurConcept.util.shader.ShaderSource_outlined_simple


@Composable
fun RuntimeShaderMetaballBox(
    modifier: Modifier = Modifier,
    transparencyLimit: Float = 0.5f,
    color: Color = Color.Black,
    content: @Composable BoxScope.() -> Unit,
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        //api 13+
        val metaBallShader = remember {
            RuntimeShader(ShaderSourceSimple)
        }

        Box(
            modifier = modifier
                .graphicsLayer {
                    metaBallShader.setFloatUniform("transparencyLimit", transparencyLimit)
                    metaBallShader.setFloatUniform("rgbColor", color.red, color.green, color.blue)
                    renderEffect = RenderEffect
                        .createRuntimeShaderEffect(metaBallShader, "composable")
                        .asComposeRenderEffect()
                },
            content = content,
        )
    }
}

enum class ShaderType(val source: String) {
    Simple(ShaderSourceSimple),
    FixColor(ShaderSourceProperColoring),
}

/**
 * A composable function similar to `RuntimeShaderMetaballBox`, but with an additional parameter
 * to switch between different shader types using an enum.
 *
 * @param modifier The modifier to be applied to the `Box`. Defaults to an empty `Modifier`.
 * @param shaderType The type of shader to use, defined by the `ShaderType` enum. Defaults to `ShaderType.Simple`.
 * @param transparencyLimit A float value representing the transparency cutoff limit for the shader. Defaults to 0.5.
 * @param color The color to be passed to the shader as a uniform, represented as a `Color` object. Defaults to `Color.Black`.
 * @param content The composable content to be rendered inside the `Box`.
 */
@Composable
fun MetaballBoxWithShaders(
    modifier: Modifier = Modifier,
    shaderType: ShaderType = ShaderType.Simple, // Default to "Simple"
    transparencyLimit: Float = 0.5f,
    color: Color = Color.Black,
    content: @Composable BoxScope.() -> Unit,
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        // Remember the shader based on the selected shader type
        val metaBallShader = remember(shaderType) {
            RuntimeShader(shaderType.source)
        }

        Box(
            modifier = modifier
                .graphicsLayer {
                    clip = true
                    metaBallShader.setFloatUniform("transparencyLimit", transparencyLimit)
                    metaBallShader.setFloatUniform("rgbColor", color.red, color.green, color.blue)
                    renderEffect = RenderEffect
                        .createRuntimeShaderEffect(metaBallShader, "composable")
                        .asComposeRenderEffect()
                },
            content = content,
        )
    }
}

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun RenderEffectShaderMetaballBox(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    //api 12+
    val metaBallShader = remember {
        metaBallRenderEffect()
    }

    Box(
        modifier = modifier.graphicsLayer {
            renderEffect = metaBallShader
        },
        content = content,
    )
}

@RequiresApi(Build.VERSION_CODES.S)
fun metaBallRenderEffect(): androidx.compose.ui.graphics.RenderEffect =
    RenderEffect.createColorFilterEffect(
        ColorMatrixColorFilter(
            android.graphics.ColorMatrix(
                floatArrayOf(
                    1f, 0f, 0f, 0f, 0f,
                    0f, 1f, 0f, 0f, 0f,
                    0f, 0f, 1f, 0f, 0f,
                    0f, 0f, 0f, 39f, -5000f
                )
            )
        )
    ).asComposeRenderEffect()

@Composable
fun StandardColorMatrixMetaBox(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {

    val metaballColorMatrix = remember {
        alphaFilterColorMatrix()
    }

    Box(
        modifier = modifier
            .colorMatrix(metaballColorMatrix),
//            .colorMatrixWithContent(metaballColorMatrix)  same conception
        content = content,
    )
}

//wrapper for outline border AGSL render script
@Composable
fun OutlineShaderMetaballBox(
    modifier: Modifier = Modifier,
    cutoffMin: Float = 0.5f,
    borderAlphaThickness: Float = 0.05f,
    color: Color = Color.Black,
    markerColor: Color = Color.Black,
    content: @Composable BoxScope.() -> Unit,
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        //api 13+
        val metaBallShader = remember {
//            RuntimeShader(ShaderSource_outlined_color_test_1) //article chapter 1
//            RuntimeShader(ShaderSource_outlined_simple) // article chapter 2
//            RuntimeShader(ShaderSource_outlined_marker_color) //article chapter 3
            RuntimeShader(ShaderSource_outlined_marker_color_and_marker_brightness) //article chapter 4
        }

        Box(
            modifier = modifier
                .graphicsLayer {
                    clip = true
                    metaBallShader.setFloatUniform("cutoff_min", cutoffMin)
                    metaBallShader.setFloatUniform("border_thickness", borderAlphaThickness)
                    metaBallShader.setFloatUniform("rgbColor", color.red, color.green, color.blue)
                    metaBallShader
                        .setFloatUniform(
                            "rgbMarkerColor", markerColor.red, markerColor.green, markerColor.blue
                        )

                    renderEffect = RenderEffect
                        .createRuntimeShaderEffect(metaBallShader, "composable")
                        .asComposeRenderEffect()
                },
            content = content,
        )
    }
}




