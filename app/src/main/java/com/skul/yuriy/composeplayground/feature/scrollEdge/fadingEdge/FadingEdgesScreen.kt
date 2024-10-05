@file:OptIn(ExperimentalMaterial3Api::class)

package com.skul.yuriy.composeplayground.feature.scrollEdge.fadingEdge

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.skul.yuriy.composeplayground.LocalNavController
import com.skul.yuriy.composeplayground.R


@Composable
fun FadingEdgesScreen(
) {

    val navController = LocalNavController.current
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Black,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    navigationIconContentColor = Color.White,
                    titleContentColor = Color.White,
                    containerColor = Color.Transparent
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
                        text = stringResource(R.string.scrolling_fading_edges)
                    )
                },
            )
        },
        bottomBar = {
            RegularBottomBar()
        }
    ) { paddingValues ->
        ScreenContent(Modifier.padding(paddingValues))
    }
}

@Composable
fun ScreenContent(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()) // Enable scrolling
    ) {
        // Long Lorem Ipsum text
        Text(
            color = Color.White,
            text = stringResource(R.string.very_long_mock_text).trimIndent(),
            modifier = Modifier.padding(16.dp),
            fontWeight = FontWeight.Normal
        )
    }
}