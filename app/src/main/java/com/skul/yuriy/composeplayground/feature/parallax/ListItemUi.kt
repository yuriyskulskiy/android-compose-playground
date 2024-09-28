package com.skul.yuriy.composeplayground.feature.parallax

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import java.util.UUID

data class ListItemUi(
    val id: String = UUID.randomUUID().toString(),
    @DrawableRes val drawableRes: Int,
    @StringRes val textRes: Int
)