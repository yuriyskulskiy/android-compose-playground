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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.skul.yuriy.composeplayground.R

enum class BottomNavItem {
    Home,
    ShoppingCart,
    Search,
    Settings
}

@Composable
fun BottomBar() {

    var selectedItem by remember { mutableStateOf(BottomNavItem.Home) } // Track the selected item using enum

    BottomAppBar(
        containerColor = Color.Transparent,
        contentPadding = PaddingValues(0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            IconButton(
                onClick = { selectedItem = BottomNavItem.Home },
            ) {
                Icon(
                    Icons.Filled.Home,
                    contentDescription = stringResource(R.string.home),
                    tint = setColor(selectedItem == BottomNavItem.Home)
                )
            }

            IconButton(
                onClick = { selectedItem = BottomNavItem.ShoppingCart },
            ) {
                Icon(
                    Icons.Filled.ShoppingCart,
                    contentDescription = stringResource(R.string.shopping_cart),
                    tint = setColor(selectedItem == BottomNavItem.ShoppingCart),
                )
            }

            IconButton(
                onClick = { selectedItem = BottomNavItem.Search },
            ) {
                Icon(
                    Icons.Filled.Search,
                    contentDescription = stringResource(R.string.search),
                    tint = setColor(selectedItem == BottomNavItem.Search),
                )
            }

            IconButton(
                modifier = Modifier,
                onClick = { selectedItem = BottomNavItem.Settings },
            ) {
                Icon(
                    Icons.Filled.Settings,
                    contentDescription = stringResource(R.string.settings),
                    tint = setColor(selectedItem == BottomNavItem.Settings),
                )
            }
        }

    }
}

fun setColor(isSelected: Boolean): Color {
    return if (isSelected) {
        Color.Red
    } else {
        Color.White
    }

}