package com.skul.yuriy.composeplayground.feature.scrollEdge.animatedElevation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

sealed class Tab(val value: Int) {
    data object TabColumn : Tab(0)
    data object TabLazColumn : Tab(1)
}


@Composable
fun AnimatedElevationRoute(viewmodel: AnimatedElevationEdgeViewModel = hiltViewModel()) {
    val uiState by viewmodel.uiState.collectAsStateWithLifecycle()
    AnimatedElevationScreen(
        uiState = uiState,
    )
}


@Composable
fun AnimatedElevationScreen(
    uiState: Pair<String, List<String>>,
) {

    // `selectedTab` keeps track of the currently selected tab (either `TabColumn` or `TabLazyColumn`).
    // It uses `mutableStateOf` to remember its state across recompositions.
    var selectedTab by remember { mutableStateOf<Tab>(Tab.TabColumn) }

    // `columnTabScrollState` is a ScrollState that tracks the vertical scroll position
    // of the `Column` in the `TabColumn`. It is used to determine whether the content is scrolled.
    val columnTabScrollState = rememberScrollState()

    // `lazyColumnTabScrollState` is a LazyListState that tracks the scroll position
    // of the `LazyColumn` in the `TabLazyColumn`. It is used to track if the content is scrolled.
    val lazyColumnTabScrollState = rememberLazyListState()

    // `columnTabElevation` is a derived state that determines whether to show elevation (shadow)
    // based on whether the `Column` or `LazyColumn` tab is scrolled.
    // It returns `0.dp` if the tab is scrolled to the top, and `8.dp` if it's not.
    val columnTabElevation by remember {
        derivedStateOf {
            val isElevationShown = when (selectedTab) {
                Tab.TabColumn -> columnTabScrollState.isColumnAtTop()
                Tab.TabLazColumn -> lazyColumnTabScrollState.isLazyListAtTop()
            }
            if (isElevationShown) {
                0.dp
            } else {
                8.dp
            }
        }
    }

    // `animatedElevation` animates the change of elevation with a duration of 600ms.
    // This provides a smooth transition when the elevation changes based on scroll state.
    val animatedElevation by columnTabElevation.animateElevation()


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Surface(
                Modifier.fillMaxWidth(),
                shadowElevation = animatedElevation  //<-- Animated elevation applied here
            ) {
                // Custom TopBar with Tabs to switch between Column and LazyColumn
                TopBarWithTabs(
                    modifier = Modifier
                        .fillMaxWidth(),
                    onTabSelected = { selectedTab = it },
                    selectedTab = selectedTab
                )
            }
        }
    ) { paddingValues ->
        // Conditionally show the content based on the selected tab.
        when (selectedTab) {
            Tab.TabColumn -> {
                RegularColumnTab(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(columnTabScrollState) //<-- Regular Column scroll state attached here
                        .padding(paddingValues),
                    columnTabUiData = uiState.first,
                )
            }

            Tab.TabLazColumn -> LazyColumnTab(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                lazyColumnTabUiData = uiState.second,
                lazyListState = lazyColumnTabScrollState   //<-- LazyColumn scroll state attached here
            )
        }
    }
}
