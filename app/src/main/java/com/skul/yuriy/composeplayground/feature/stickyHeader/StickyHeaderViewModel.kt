package com.skul.yuriy.composeplayground.feature.stickyHeader

import androidx.lifecycle.ViewModel
import com.skul.yuriy.composeplayground.feature.parallax.ListItemUi
import com.skul.yuriy.composeplayground.feature.parallax.mockData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class StickyHeaderViewModel @Inject constructor() : ViewModel() {

    //todo make another data ui tate
    private val _uiState = MutableStateFlow(mockData)
    val uiState: StateFlow<List<ListItemUi>> = _uiState.asStateFlow()
}