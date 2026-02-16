package com.skul.yuriy.composeplayground.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Screens : NavKey {

    @Serializable
    data object Starter : Screens

    @Serializable
    data object Parallax : Screens

    @Serializable
    data object StickyHeaderStateTracker : Screens

    @Serializable
    data object MetaballScreen : Screens

    @Serializable
    data object AnimatedElevationEdge : Screens

    @Serializable
    data object FadingEdgesScreen : Screens

    @Serializable
    data object VectorDrawableShadow : Screens

    @Serializable
    data object CircularHaloShadow : Screens

    @Serializable
    data object AnimatedArk : Screens

    @Serializable
    data object TransparentShadowBox : Screens

    @Serializable
    data object BottomEdgeShadow : Screens

    @Serializable
    data object AnimateCircularButton : Screens

    @Serializable
    data object GooeyEffect : Screens

    @Serializable
    data object MetaballClassicMath : Screens

    @Serializable
   data object MetaballTextEdges : Screens

    @Serializable
    data object CustomBlur : Screens

    @Serializable
    data object MetaballEdgeAdvanced : Screens

    @Serializable
    data object MetaballBasicTextAndEdge : Screens

    @Serializable
    data object TextMetaballConcept : Screens
}
