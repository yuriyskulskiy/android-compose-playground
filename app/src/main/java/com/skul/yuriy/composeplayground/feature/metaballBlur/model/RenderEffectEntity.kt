package com.skul.yuriy.composeplayground.feature.metaballBlur.model

import android.os.Build
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.S)
data class RenderEffectEntity(
    val id: Int,
    val renderEffect: androidx.compose.ui.graphics.RenderEffect,
    val displayName: String
)