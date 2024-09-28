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

val mockData: List<ListItemUi> = listOf(
    ListItemUi(
        textRes = R.string.toyota_supra_the_fourth_generation_a80,
        drawableRes = R.drawable.img_10
    ),
    ListItemUi(
        textRes = R.string.nissan_silvia_s15,
        drawableRes = R.drawable.img_4
    ),
    ListItemUi(
        textRes = R.string.nissan_200sx_s13_5,
        drawableRes = R.drawable.img_s13_to_s15_conversion
    ),
    ListItemUi(
        textRes = R.string.nissan_gtr_r34,
        drawableRes = R.drawable.gtr_34_1
    ),
    ListItemUi(
        textRes = R.string.nissan_350_z,
        drawableRes = R.drawable.img_1
    ),
    ListItemUi(
        textRes = R.string.nissan_silvia_s14_5,
        drawableRes = R.drawable.img_8
    ),
    ListItemUi(
        textRes = R.string.nissan_silvia_s14,
        drawableRes = R.drawable.img_7
    ),
    ListItemUi(
        textRes = R.string.toyota_new_supra,
        drawableRes = R.drawable.img_9
    ),
    ListItemUi(
        textRes = R.string.nissan_350_z,
        drawableRes = R.drawable.img_5
    ),
    ListItemUi(
        textRes = R.string.nissan_silvia_200sx,
        drawableRes = R.drawable.img_6_nissan_silvia_and_240sx
    ),


    ListItemUi(
        textRes = R.string.nissan_gtr_r35,
        drawableRes = R.drawable.gtr_1
    ),


    )