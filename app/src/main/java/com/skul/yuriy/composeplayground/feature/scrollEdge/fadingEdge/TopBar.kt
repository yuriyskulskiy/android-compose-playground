package com.skul.yuriy.composeplayground.feature.scrollEdge.fadingEdge

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.skul.yuriy.composeplayground.R



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FadingEdgesTopBar(
    onNavigateUp: () -> Unit,  // Pass navigation action to the TopBar
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            navigationIconContentColor = Color.White,
            titleContentColor = Color.White,
            containerColor = Color.Transparent
        ),
        navigationIcon = {
            IconButton(onClick = { onNavigateUp() }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Go Back"
                )
            }
        },
        title = {
            Text(
                text = stringResource(R.string.scrolling_fading_edges)
            )
        },
    )
}