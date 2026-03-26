package com.skul.yuriy.composeplayground.feature.animatedRectButton

enum class RectButtonShapeMode {
    CIRCLE,
    ROUNDED_RECTANGLE;

    fun toggle(): RectButtonShapeMode = when (this) {
        CIRCLE -> ROUNDED_RECTANGLE
        ROUNDED_RECTANGLE -> CIRCLE
    }
}
