package com.skul.yuriy.composeplayground.starter

import android.app.Activity
import androidx.annotation.StringRes
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey

sealed interface StarterDestination {
    @get:StringRes
    val titleRes: Int
}

data class StarterYearDivider(
    val label: String
) : StarterDestination {
    override val titleRes: Int
        get() = 0
}

data class ComposeStarterDestination(
    @StringRes override val titleRes: Int,
    val navigate: NavBackStack<NavKey>.() -> Unit
) : StarterDestination

data class ActivityStarterDestination(
    @StringRes override val titleRes: Int,
    val activityClass: Class<out Activity>
) : StarterDestination
