package com.skul.yuriy.composeplayground.util.renderEffect

import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.RenderEffect as ComposeRenderEffect
import androidx.compose.ui.graphics.asComposeRenderEffect

/**
 * Creates an AGSL alpha-threshold effect for threshold in [0f..1f].
 * Returns `null` when threshold is 0f (identity/no-op).
 */
@Composable
fun rememberAlphaThresholdAgslEffect(threshold01: Float): ComposeRenderEffect? {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return null

    val threshold = threshold01.coerceIn(0f, 1f)
    if (threshold <= 0f) return null

    val shader = remember(threshold) { RuntimeShader(alphaThresholdAglslSource(threshold)) }
    return remember(shader) {
        RenderEffect.createRuntimeShaderEffect(shader, "src").asComposeRenderEffect()
    }
}

// Same threshold behavior as the main shader, written with explicit if/else for readability.
private fun alphaThresholdAglslSource_simple(threshold: Float): String = """
uniform shader src;

half4 main(float2 p) {
    half4 c = src.eval(p);
    if (c.a >= $threshold) {
        return half4(c.rgb, 1.0);
    }
    return half4(c.rgb, 0.0);
}
"""

// Current AGSL implementation used by rememberAlphaThresholdAgslEffect.
private fun alphaThresholdAglslSource(threshold: Float): String = """
uniform shader src;

half4 main(float2 p) {
    half4 c = src.eval(p);
    float m = step($threshold, c.a);
    return half4(c.rgb * m, m);
}
"""
