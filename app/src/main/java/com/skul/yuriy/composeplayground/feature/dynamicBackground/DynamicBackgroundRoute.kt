package com.skul.yuriy.composeplayground.feature.dynamicBackground

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun DynamicBackGroundRoue(
    viewModel: DynamicBackgroundViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    DynamicBackGroundScreen(
//        uiState
    )
}

@Composable
fun DynamicBackGroundScreen(
//    uiState: List<ListItemUi>
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Text(text = "hello dynamickBackground")
    }
}