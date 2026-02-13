package com.skul.yuriy.composeplayground.feature.metaballEdgesAndText

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.skul.yuriy.composeplayground.feature.metaballEdgesAndText.tabs.edge.GooeyEdgeScreen
import com.skul.yuriy.composeplayground.feature.metaballEdgesAndText.tabs.text.TextMeltScreen
import com.skul.yuriy.composeplayground.feature.metaballEdgesAndText.tabs.text.TextMeltState

@RequiresApi(Build.VERSION_CODES.S)
@Composable
internal fun MetaballEdgeTextContent(
    selectedTab: MetaballEdgeTextTab,
    textMeltState: TextMeltState,
    textMeltScrollState: ScrollState,
    modifier: Modifier = Modifier,
) {
    val fullModifier = modifier.fillMaxSize()

    Crossfade(
        targetState = selectedTab,
        animationSpec = tween(durationMillis = 220),
        modifier = fullModifier,
        label = "metaballEdgeTextTabsTransition",
    ) { tab ->
        when (tab) {
            MetaballEdgeTextTab.GooeyEdge -> GooeyEdgeScreen(modifier = Modifier.fillMaxSize())
            else -> TextMeltScreen(
                state = textMeltState,
                scrollState = textMeltScrollState,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
