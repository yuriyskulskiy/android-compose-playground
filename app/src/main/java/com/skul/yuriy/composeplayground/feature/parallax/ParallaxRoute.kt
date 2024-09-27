package com.skul.yuriy.composeplayground.feature.parallax

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ParallaxRoute() {
    ParallaxScreen()
}

@Composable
fun ParallaxScreen() {
    ParallaxLazyColumn()
}

@Composable
fun ParallaxLazyColumn() {
    // Create a LazyListState to track the scroll position
    val listState = rememberLazyListState()
    val viewportHeight by remember {
        derivedStateOf {
            listState.layoutInfo.viewportEndOffset.toFloat() - listState.layoutInfo.viewportStartOffset.toFloat()
        }
    }
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(16.dp),
        state = listState,
        modifier = Modifier.fillMaxSize()
    ) {
        items(30) { index ->
            ParallaxListItem(listState = listState, index = index, viewportHeight = viewportHeight)
        }
    }
}

