package com.skul.yuriy.composeplayground.feature.animatedBorderRect.border

import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.os.Build
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun FireShaderDraftRectShadowBox(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 24.dp,
    bandWidth: Dp = 36.dp,
    contourWidth: Dp = 220.dp,
    contourHeight: Dp = 120.dp,
    bandScale: Float = 1f,
    smokeScale: Float = 1f,
    intensity: Float = 1f
) {
    var isPressed by remember { mutableStateOf(false) }
    val animatedPressBand by animateFloatAsState(
        targetValue = if (isPressed) 2.05f else 1f,
        animationSpec = tween(durationMillis = 220),
        label = ""
    )
    val animatedPressIntensity by animateFloatAsState(
        targetValue = if (isPressed) 3.2f else 1f,
        animationSpec = tween(durationMillis = 220),
        label = ""
    )
    val animatedPressSmoke by animateFloatAsState(
        targetValue = if (isPressed) 2.35f else 1f,
        animationSpec = tween(durationMillis = 220),
        label = ""
    )

    val interactiveBand = (bandWidth * bandScale * animatedPressBand).coerceIn(8.dp, 86.dp)
    val interactiveCorner = cornerRadius.coerceIn(8.dp, 56.dp)
    val interactiveIntensity = (intensity * animatedPressIntensity).coerceIn(0.2f, 4.5f)
    val interactiveSmoke = (smokeScale * animatedPressSmoke).coerceIn(0.3f, 4f)

    val transition = rememberInfiniteTransition(label = "")
    val time by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 60_000, easing = LinearEasing)
        ),
        label = ""
    )

    Box(
        modifier = modifier
            .pointerInput(Unit) {
                awaitEachGesture {
                    awaitFirstDown(requireUnconsumed = false)
                    isPressed = true
                    waitForUpOrCancellation()
                    isPressed = false
                }
            }
            .fireRectHaloShaderDraft(
                time = time,
                bandWidth = interactiveBand,
                cornerRadius = interactiveCorner,
                contourWidth = contourWidth,
                contourHeight = contourHeight,
                smokeScale = interactiveSmoke,
                intensity = interactiveIntensity
            )
    )
}

fun Modifier.fireRectHaloShaderDraft(
    time: Float,
    bandWidth: Dp,
    cornerRadius: Dp,
    contourWidth: Dp,
    contourHeight: Dp,
    smokeScale: Float,
    intensity: Float = 1f
): Modifier = composed {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return@composed this

    val density = LocalDensity.current
    val bandPx = with(density) { bandWidth.toPx() }
    val cornerPx = with(density) { cornerRadius.toPx() }
    val contourWidthPx = with(density) { contourWidth.toPx() }
    val contourHeightPx = with(density) { contourHeight.toPx() }

    var widthPx by remember { mutableIntStateOf(0) }
    var heightPx by remember { mutableIntStateOf(0) }

    val runtimeShader = remember { RuntimeShader(FireRectHaloAglsl) }
    val effect = remember(
        time,
        widthPx,
        heightPx,
        bandPx,
        cornerPx,
        contourWidthPx,
        contourHeightPx,
        smokeScale,
        intensity
    ) {
        if (widthPx <= 0 || heightPx <= 0) return@remember null
        runtimeShader.setFloatUniform("uResolution", widthPx.toFloat(), heightPx.toFloat())
        runtimeShader.setFloatUniform("uTime", time)
        runtimeShader.setFloatUniform("uBandPx", bandPx)
        runtimeShader.setFloatUniform("uCornerPx", cornerPx)
        runtimeShader.setFloatUniform("uContourSize", contourWidthPx, contourHeightPx)
        runtimeShader.setFloatUniform("uSmokeScale", smokeScale)
        runtimeShader.setFloatUniform("uIntensity", intensity)
        RenderEffect
            .createRuntimeShaderEffect(runtimeShader, "src")
            .asComposeRenderEffect()
    }

    this
        .onSizeChanged {
            widthPx = it.width
            heightPx = it.height
        }
        .then(
            if (widthPx > 0 && heightPx > 0) {
                Modifier.graphicsLayer {
                    compositingStrategy = CompositingStrategy.Offscreen
                    effect?.let { renderEffect = it }
                }
            } else {
                Modifier
            }
        )
        // Draw source inside the same graphics layer.
        .drawBehind {
            drawRect(color = androidx.compose.ui.graphics.Color.White)
        }
}

private const val FireRectHaloAglsl = """
uniform shader src;
uniform float2 uResolution;
uniform float uTime;
uniform float uBandPx;
uniform float uCornerPx;
uniform float2 uContourSize;
uniform float uSmokeScale;
uniform float uIntensity;

const float PI = 3.14159265358979323846;
const float TWO_PI = 6.28318530717958647692;

float rand(float2 n) {
    return fract(sin(dot(n, float2(12.9898, 12.1414))) * 83758.5453);
}

float noise(float2 n) {
    const float2 d = float2(0.0, 1.0);
    float2 b = floor(n);
    float2 f = mix(float2(0.0), float2(1.0), fract(n));
    return mix(
        mix(rand(b), rand(b + d.yx), f.x),
        mix(rand(b + d.xy), rand(b + d.yy), f.x),
        f.y
    );
}

float fire(float2 n) {
    return noise(n) + noise(n * 2.1) * 0.6 + noise(n * 5.4) * 0.42;
}

float3 ramp(float t) {
    t = max(t, 0.001);
    return t <= 0.5
        ? float3(1.0 - t * 1.4, 0.2, 1.05) / t
        : float3(0.3 * (1.0 - t) * 2.0, 0.2, 1.05) / t;
}

float shade(float2 uv, float t) {
    uv.x += uv.y < 0.5 ? (23.0 + t * 0.035) : (-11.0 + t * 0.03);
    uv.y = abs(uv.y - 0.5);
    uv.x *= 35.0;

    float q = fire(uv - t * 0.013) / 2.0;
    float2 rv = float2(
        fire(uv + q / 2.0 + t - uv.x - uv.y),
        fire(uv + q - t)
    );
    return pow((rv.y + rv.y) * max(0.0, uv.y) + 0.1, 4.0);
}

float3 colorFromGrad(float grad) {
    float g = sqrt(max(grad, 0.0));
    float3 c = ramp(g);
    c /= (1.15 + max(float3(0.0), c));
    return c;
}

float sdRoundBox(float2 p, float2 b, float r) {
    float2 q = abs(p) - b + float2(r);
    return length(max(q, float2(0.0))) + min(max(q.x, q.y), 0.0) - r;
}

float perimeterS(float2 p, float2 halfSize, float r) {
    float2 inner = max(halfSize - float2(r), float2(0.0001));
    float Lh = 2.0 * inner.x;
    float Lv = 2.0 * inner.y;
    float Lc = 0.5 * PI * r;

    float2 a = abs(p);
    bool corner = (a.x > inner.x) && (a.y > inner.y);

    if (!corner) {
        if (a.x > inner.x) {
            if (p.x >= 0.0) {
                return clamp(p.y + inner.y, 0.0, Lv);
            } else {
                float base = Lv + Lc + Lh + Lc;
                return base + clamp(inner.y - p.y, 0.0, Lv);
            }
        }

        if (a.y > inner.y) {
            if (p.y >= 0.0) {
                float base = Lv + Lc;
                return base + clamp(inner.x - p.x, 0.0, Lh);
            } else {
                float base = Lv + Lc + Lh + Lc + Lv + Lc;
                return base + clamp(p.x + inner.x, 0.0, Lh);
            }
        }

        return clamp(p.y + inner.y, 0.0, Lv);
    }

    if (p.x >= 0.0 && p.y >= 0.0) {
        float2 c = float2(inner.x, inner.y);
        float2 v = (p - c) / r;
        float ang = clamp(atan(v.y, v.x), 0.0, 0.5 * PI);
        return Lv + ang * r;
    }

    if (p.x <= 0.0 && p.y >= 0.0) {
        float2 c = float2(-inner.x, inner.y);
        float2 v = (p - c) / r;
        float ang = clamp(atan(v.y, v.x), 0.5 * PI, PI);
        float local = ang - 0.5 * PI;
        return Lv + Lc + Lh + local * r;
    }

    if (p.x <= 0.0 && p.y <= 0.0) {
        float2 c = float2(-inner.x, -inner.y);
        float2 v = (p - c) / r;
        float ang = atan(v.y, v.x);
        float ang2 = ang < 0.0 ? ang + TWO_PI : ang;
        ang2 = clamp(ang2, PI, PI + 0.5 * PI);
        float local = ang2 - PI;
        return Lv + Lc + Lh + Lc + Lv + local * r;
    }

    float2 c = float2(inner.x, -inner.y);
    float2 v = (p - c) / r;
    float ang = clamp(atan(v.y, v.x), -0.5 * PI, 0.0);
    float local = ang + 0.5 * PI;
    float base = Lv + Lc + Lh + Lc + Lv + Lc + Lh;
    return base + local * r;
}

half4 main(float2 fragCoord) {
    float2 res = max(uResolution, float2(1.0));
    float t = uTime * 60.0;
    float2 p = fragCoord - 0.5 * res;
    float2 pNorm = p / max(res.y, 1.0);

    float thickness = max(1.5, uBandPx * 0.30);
    float smokeW = max(thickness * 4.8, uBandPx * 2.2) * max(uSmokeScale, 0.3);

    float2 baseHalfSize = uContourSize * 0.5;
    float2 halfSize = baseHalfSize;
    float radius = min(uCornerPx, min(halfSize.x, halfSize.y) - 0.1);
    radius = max(radius, 1.0);

    float d = sdRoundBox(p, halfSize, radius);

    float2 inner = max(halfSize - float2(radius), float2(0.0001));
    float Lh = 2.0 * inner.x;
    float Lv = 2.0 * inner.y;
    float P = 2.0 * (Lh + Lv) + TWO_PI * radius;

    float s = perimeterS(p, halfSize, radius);
    float u = fract(s / P);
    float v = 0.5 + d / (thickness * 2.0);

    float coreMask = 1.0 - smoothstep(thickness, thickness * 2.0, abs(d));
    float smokeMask = 1.0 - smoothstep(smokeW, smokeW * 3.2, abs(d));

    float ff = smoothstep(-0.15, 0.25, -pNorm.y);

    // Seam fix: 10px overlap crossfade between (u) and (u + 1).
    float overlapPx = 10.0;
    float seamWidth = clamp(overlapPx / max(P, 1.0), 0.001, 0.20);
    float seamBlend = smoothstep(0.0, seamWidth, u) * smoothstep(0.0, seamWidth, 1.0 - u);

    float2 uvA = float2(u + 1.30, v);
    float2 uvA2 = float2(u + 1.90, 1.0 - v);
    float3 a1 = colorFromGrad(shade(uvA, t)) * ff;
    float3 a2 = colorFromGrad(shade(uvA2, t)) * (1.0 - ff);
    float3 flameA = a1 + a2;

    float uB = u + 1.0;
    float2 uvB = float2(uB + 1.30, v);
    float2 uvB2 = float2(uB + 1.90, 1.0 - v);
    float3 b1 = colorFromGrad(shade(uvB, t)) * ff;
    float3 b2 = colorFromGrad(shade(uvB2, t)) * (1.0 - ff);
    float3 flameB = b1 + b2;

    float3 flame = mix(flameB, flameA, seamBlend) * uIntensity;
    float3 col = flame * coreMask;
    col += flame * 0.55 * smokeMask;
    col *= smokeMask;

    float a = clamp(max(max(col.r, col.g), col.b), 0.0, 1.0);
    a = max(a * 0.95, coreMask * 0.35);
    return half4(col, a);
}
"""
