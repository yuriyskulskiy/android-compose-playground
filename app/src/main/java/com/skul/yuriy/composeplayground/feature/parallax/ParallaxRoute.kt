package com.skul.yuriy.composeplayground.feature.parallax

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ParallaxRoute() {
    ParallaxScreen()
}

@Composable
fun ParallaxScreen(
    viewModel: ParallaxVewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ParallaxLazyColumn(uiState)
}

@Composable
fun ParallaxLazyColumn(uiState: List<ListItemUi>) {
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
        this.itemsIndexed(items = uiState,
            key = { index, itemUi -> itemUi.id }) { index, itemUi: ListItemUi ->
            ParallaxListItem(
                itemUi = itemUi,
                listState = listState,
                index = index,
                viewportHeight = viewportHeight
            )
        }
    }
}

