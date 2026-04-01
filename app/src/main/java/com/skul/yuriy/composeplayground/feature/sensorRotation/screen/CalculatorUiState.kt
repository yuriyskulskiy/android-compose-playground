package com.skul.yuriy.composeplayground.feature.sensorRotation.screen

import com.skul.yuriy.composeplayground.feature.sensorRotation.shape.AspectSlidingShapesCalculator
import com.skul.yuriy.composeplayground.feature.sensorRotation.shape.FittedMorphingRectShapeCalculator
import com.skul.yuriy.composeplayground.feature.sensorRotation.shape.IRotationShapeCalculator
import com.skul.yuriy.composeplayground.feature.sensorRotation.shape.MorphingRectShapeCalculator
import com.skul.yuriy.composeplayground.feature.sensorRotation.shape.TwoPhaseSlidingShapeCalculator

internal enum class CalculatorUiState(
    val label: String
) {
    TwoPhaseSlide(label = "2 phase slide") {
        override fun createCalculator(): IRotationShapeCalculator = TwoPhaseSlidingShapeCalculator()
    },
    AspectSlide(label = "aspect slide") {
        override fun createCalculator(): IRotationShapeCalculator = AspectSlidingShapesCalculator()
    },
    MorphingRect(label = "morph rect") {
        override fun createCalculator(): IRotationShapeCalculator = MorphingRectShapeCalculator()
    },
    FittedMorphingRect(label = "fit morph rect") {
        override fun createCalculator(): IRotationShapeCalculator = FittedMorphingRectShapeCalculator()
    };

    abstract fun createCalculator(): IRotationShapeCalculator

    fun next(): CalculatorUiState = entries[(ordinal + 1) % entries.size]
}
