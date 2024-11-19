package com.skul.yuriy.composeplayground.util.regularComponents

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopAppBar(
    title: String,
    onNavUp: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = Color.Black,
    navigationIconColor: Color = Color.White,
    titleColor: Color = Color.White,
    enableHorizontalDivider: Boolean = true,
    dividerColor: Color = Color.Gray
) {
    Column(modifier = modifier) {
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = containerColor,
                navigationIconContentColor = navigationIconColor
            ),
            navigationIcon = {
                IconButton(
                    onClick = onNavUp,
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = navigationIconColor
                    )
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Go Back"
                    )
                }
            },
            title = {
                Text(
                    text = title,
                    color = titleColor
                )
            }
        )
        if (enableHorizontalDivider) {
            HorizontalDivider(
                thickness = 1.dp,
                color = dividerColor
            )
        }
    }
}