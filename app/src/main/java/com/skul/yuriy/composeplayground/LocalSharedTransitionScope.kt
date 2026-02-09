package com.skul.yuriy.composeplayground

import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.staticCompositionLocalOf

val LocalSharedTransitionScope = staticCompositionLocalOf<SharedTransitionScope?> { null }
