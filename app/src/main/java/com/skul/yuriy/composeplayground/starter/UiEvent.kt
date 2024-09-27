package com.skul.yuriy.composeplayground.starter

sealed class UiEvent {
    data object OnStickyHeaderSelected : UiEvent()
    data object OnParallaxScrollSelected : UiEvent()
}

