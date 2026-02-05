package com.skul.yuriy.composeplayground.feature.customBlur

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.skul.yuriy.composeplayground.LocalNavController
import com.skul.yuriy.composeplayground.R
import com.skul.yuriy.composeplayground.feature.scrollEdge.fadingEdge.BottomBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomBlurScreen(
    modifier: Modifier = Modifier
) {
    val navController = LocalNavController.current
    Scaffold(
        modifier = modifier,
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    navigationIconContentColor = Color.White,
                    titleContentColor = Color.White,
                    containerColor = Color.Black
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Go Back"
                        )
                    }
                },
                title = {
                    Text(
                        text = stringResource(R.string.custom_blur)
                    )
                },
            )

        },
        bottomBar = {
            BottomBar(
                containerColor = Color.Black
            )
        }
    ) { paddingValues ->
        CustomBlurScrollingContent(
            modifier = Modifier.padding(paddingValues),
        )
    }
}
