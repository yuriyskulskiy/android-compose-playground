package com.skul.yuriy.composeplayground.feature.sensorRotation.screen

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.skul.yuriy.composeplayground.feature.sensorRotation.sensor.AccelerometerRotationAngleSource
import com.skul.yuriy.composeplayground.feature.sensorRotation.sensor.OrientationEventRotationAngleSource
import com.skul.yuriy.composeplayground.feature.sensorRotation.sensor.RotationAngleSourceType
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SensorRotationScreen(
    onNavUp: () -> Unit
) {
    val tiltAngle = rememberRotationAngle()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp)
                .border(width = 1.dp, color = Color.Red)
        ) {
            CornerDebugCanvas(
                modifier = Modifier.fillMaxSize(),
                inset = 16.dp,
                rotationDegrees = tiltAngle
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        rotationZ = tiltAngle
                        transformOrigin = TransformOrigin.Center
                    }
            ) {
                Canvas(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val centerY = size.height / 2f
                    drawLine(
                        color = Color.Black,
                        start = Offset(x = 0f, y = centerY),
                        end = Offset(x = size.width, y = centerY),
                        strokeWidth = 2.dp.toPx()
                    )
                }

                Text(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset(y = (-16).dp),
                    text = "Tilt: ${"%.1f".format(tiltAngle)}°",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }

            // dont delete - прост опока он не нужен

//            SensorRotationTopAppBar(
//                modifier = Modifier
//                    .align(Alignment.TopCenter)
//                    .padding(top = rememberStatusBarHeight()),
//                title = stringResource(R.string.sensor_rotation_demo),
//                onNavUp = onNavUp
//            )
//
//            Text(
//                modifier = Modifier.align(Alignment.Center),
//                text = stringResource(R.string.sensor_rotation_demo_placeholder),
//                color = Color.Black
//            )
        }
    }
}

@Composable
private fun CornerDebugCanvas(
    modifier: Modifier = Modifier,
    inset: Dp,
    rotationDegrees: Float
) {
    Canvas(modifier = modifier) {
        val insetPx = inset.toPx()
        val markerRadius = 3.dp.toPx()
        val anchorMarkerRadius = 3.dp.toPx()
        val insetMarkerRadius = 5.dp.toPx()

        val topLeft = Offset(0f, 0f)
        val topRight = Offset(size.width, 0f)
        val bottomRight = Offset(size.width, size.height)
        val bottomLeft = Offset(0f, size.height)

        val insetTopLeft = Offset(insetPx, insetPx)
        val insetTopRight = Offset(size.width - insetPx, insetPx)
        val insetBottomRight = Offset(size.width - insetPx, size.height - insetPx)
        val insetBottomLeft = Offset(insetPx, size.height - insetPx)
        val topPair = resolveTopPair(
            anchorA = insetTopLeft,
            anchorB = insetTopRight,
            anchorC = insetBottomRight,
            anchorD = insetBottomLeft,
            rotationDegrees = rotationDegrees
        )

        drawCircle(color = Color.Blue, radius = markerRadius, center = topLeft)
        drawCircle(color = Color.Blue, radius = markerRadius, center = topRight)
        drawCircle(color = Color.Blue, radius = markerRadius, center = bottomRight)
        drawCircle(color = Color.Blue, radius = markerRadius, center = bottomLeft)

        drawCircle(color = Color.Green, radius = insetMarkerRadius, center = topPair.a1)
        drawCircle(color = Color.Green, radius = insetMarkerRadius, center = topPair.b1)
        drawCircle(color = Color.Green, radius = insetMarkerRadius, center = insetBottomRight)
        drawCircle(color = Color.Green, radius = insetMarkerRadius, center = insetBottomLeft)

        drawCircle(color = Color.Black, radius = anchorMarkerRadius, center = insetTopLeft)
        drawCircle(color = Color.Black, radius = anchorMarkerRadius, center = insetTopRight)
        drawCircle(color = Color.Black, radius = anchorMarkerRadius, center = insetBottomRight)
        drawCircle(color = Color.Black, radius = anchorMarkerRadius, center = insetBottomLeft)
    }
}

private fun resolveTopPair(
    anchorA: Offset,
    anchorB: Offset,
    anchorC: Offset,
    anchorD: Offset,
    rotationDegrees: Float
): TopPair {
    val radians = Math.toRadians(rotationDegrees.toDouble())
    val gravityDown = Offset(
        x = (-sin(radians)).toFloat(),
        y = cos(radians).toFloat()
    )
    val horizontalRight = Offset(
        x = cos(radians).toFloat(),
        y = sin(radians).toFloat()
    )

    val bLowerThanA = dot(anchorB, gravityDown) > dot(anchorA, gravityDown)
    return if (bLowerThanA) {
        val a1 = intersectRayWithSegment(
            rayOrigin = anchorB,
            rayDirection = horizontalRight * -1f,
            segmentStart = anchorA,
            segmentEnd = anchorD
        )
        Log.d(
            "SensorRotationTopPair",
            "B lower by gravity: fixed=B1, moved=A1"
        )
        TopPair(
            a1 = a1,
            b1 = anchorB
        )
    } else {
        val b1 = intersectRayWithSegment(
            rayOrigin = anchorA,
            rayDirection = horizontalRight,
            segmentStart = anchorB,
            segmentEnd = anchorC
        )
        Log.d(
            "SensorRotationTopPair",
            "A lower by gravity: fixed=A1, moved=B1"
        )
        TopPair(
            a1 = anchorA,
            b1 = b1
        )
    }
}

private fun intersectRayWithSegment(
    rayOrigin: Offset,
    rayDirection: Offset,
    segmentStart: Offset,
    segmentEnd: Offset
): Offset {
    val segmentDirection = segmentEnd - segmentStart
    val denominator = cross(rayDirection, segmentDirection)
    if (denominator == 0f) return segmentStart

    val diff = segmentStart - rayOrigin
    val rayT = cross(diff, segmentDirection) / denominator
    val segmentT = cross(diff, rayDirection) / denominator

    if (rayT < 0f) return segmentStart

    val clampedSegmentT = segmentT.coerceIn(0f, 1f)
    return segmentStart + segmentDirection * clampedSegmentT
}

private fun cross(a: Offset, b: Offset): Float = a.x * b.y - a.y * b.x

private fun dot(a: Offset, b: Offset): Float = a.x * b.x + a.y * b.y

private data class TopPair(
    val a1: Offset,
    val b1: Offset
)

@Composable
private fun rememberRotationAngle(
    sourceType: RotationAngleSourceType = DEFAULT_ROTATION_ANGLE_SOURCE
//    sourceType: RotationAngleSourceType = ALTERNATIVE_ROTATION_ANGLE_SOURCE
): Float {
    val context = LocalContext.current
    val angleSource = remember(context, sourceType) {
        when (sourceType) {
            RotationAngleSourceType.Accelerometer -> AccelerometerRotationAngleSource(context)
            RotationAngleSourceType.OrientationEventListener -> {
                OrientationEventRotationAngleSource(context)
            }
        }
    }
    var tiltAngle by remember { mutableFloatStateOf(0f) }
    val currentOnAngleChanged by rememberUpdatedState<(Float) -> Unit> { tiltAngle = it }

    DisposableEffect(angleSource) {
        angleSource.start { angle ->
            currentOnAngleChanged(angle)
        }
        onDispose(angleSource::stop)
    }

    return tiltAngle
}

private val DEFAULT_ROTATION_ANGLE_SOURCE = RotationAngleSourceType.Accelerometer
private val ALTERNATIVE_ROTATION_ANGLE_SOURCE = RotationAngleSourceType.OrientationEventListener
