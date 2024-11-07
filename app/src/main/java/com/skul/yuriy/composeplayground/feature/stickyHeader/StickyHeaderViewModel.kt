package com.skul.yuriy.composeplayground.feature.stickyHeader

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class StickyHeaderViewModel @Inject constructor() : ViewModel() {


    //todo make another data ui tate
    private val _uiState = MutableStateFlow(UiStateStickyScreen.Data(generateSections()))
    val uiState: StateFlow<UiStateStickyScreen.Data> = _uiState.asStateFlow()

    fun expandSection(toggledId: String) {
        _uiState.update {currentState->
            currentState.copy(
                data = currentState.data.map { section ->
                    if (section.id == toggledId) {
                        section.copy(isExpanded = !section.isExpanded)
                    } else {
                        section
                    }
                }
            )
        }
    }
}