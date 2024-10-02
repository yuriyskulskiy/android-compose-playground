package com.skul.yuriy.composeplayground.feature.scrollEdge.animatedElevation

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject


@HiltViewModel
class AnimatedElevationEdgeViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(Pair(columnTabDataMock(), lazyColumnTabDataMock()))
    val uiState: StateFlow<Pair<String, List<String>>> = _uiState.asStateFlow()

}