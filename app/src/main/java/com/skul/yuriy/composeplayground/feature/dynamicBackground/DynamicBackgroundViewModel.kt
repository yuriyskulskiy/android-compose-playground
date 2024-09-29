package com.skul.yuriy.composeplayground.feature.dynamicBackground

import androidx.lifecycle.ViewModel
import com.skul.yuriy.composeplayground.feature.parallax.ListItemUi
import com.skul.yuriy.composeplayground.feature.parallax.mockData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject


@HiltViewModel
class DynamicBackgroundViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(mockData)
    val uiState: StateFlow<List<ListItemUi>> = _uiState.asStateFlow()
}