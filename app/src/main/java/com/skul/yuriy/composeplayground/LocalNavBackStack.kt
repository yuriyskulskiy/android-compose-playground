package com.skul.yuriy.composeplayground

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey

val LocalNavBackStack = staticCompositionLocalOf<NavBackStack<NavKey>> {
    error("LocalBackStack is not provided")
}