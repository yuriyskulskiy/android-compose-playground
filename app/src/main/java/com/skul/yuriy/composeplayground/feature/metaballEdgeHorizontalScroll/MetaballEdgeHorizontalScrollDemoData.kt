package com.skul.yuriy.composeplayground.feature.metaballEdgeHorizontalScroll

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector

internal data class CircularItemUiItem(
    val id: Int,
    val icon: ImageVector,
)

private val circularItemIcons = listOf(
    Icons.Default.Home,
    Icons.Default.Search,
    Icons.Default.Person,
    Icons.Default.Settings,
    Icons.Default.ShoppingCart,
    Icons.Default.Info,
    Icons.Default.Done,
    Icons.Default.Call,
    Icons.Default.Build,
    Icons.Default.Add,
    Icons.Default.Favorite,
)

internal val circularItemUiItems = List(16) { index ->
    CircularItemUiItem(
        id = index,
        icon = circularItemIcons[index % circularItemIcons.size],
    )
}
