package com.skul.yuriy.composeplayground.feature.scrollEdge.fadingEdge

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

enum class BottomNavItem {
    Home,
    ShoppingCart,
    Search,
    Settings
}

@Composable
fun RegularBottomBar() {

    var selectedItem by remember { mutableStateOf(BottomNavItem.Home) } // Track the selected item using enum

    BottomAppBar(
        containerColor = Color.Transparent,
        contentPadding = PaddingValues(0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        )
        {

            // Home Button
            IconButton(
                onClick = { selectedItem = BottomNavItem.Home },
            ) {
                Icon(
                    Icons.Filled.Home,
                    contentDescription = "Home",
                    tint = if (selectedItem == BottomNavItem.Home) Color.Red else Color.White,
                )
            }

            IconButton(
                onClick = { selectedItem = BottomNavItem.ShoppingCart },
            ) {
                Icon(
                    Icons.Filled.ShoppingCart,
                    contentDescription = "Shopping Cart",
                    tint = if (selectedItem == BottomNavItem.ShoppingCart) Color.Red else Color.White,
                )
            }

            IconButton(
                onClick = { selectedItem = BottomNavItem.Search },
            ) {
                Icon(
                    Icons.Filled.Search,
                    contentDescription = "Search",
                    tint = if (selectedItem == BottomNavItem.Search) Color.Red else Color.White,
                )
            }

            IconButton(
                onClick = { selectedItem = BottomNavItem.Settings },
            ) {
                Icon(
                    Icons.Filled.Settings,
                    contentDescription = "Settings",
                    tint = if (selectedItem == BottomNavItem.Settings) Color.Red else Color.White,
                )
            }
        }
    }
}