package com.skul.yuriy.composeplayground.feature.parallax

import androidx.lifecycle.ViewModel
import com.skul.yuriy.composeplayground.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ParallaxVewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(mockData)
    val uiState: StateFlow<List<ListItemUi>> = _uiState.asStateFlow()
}


