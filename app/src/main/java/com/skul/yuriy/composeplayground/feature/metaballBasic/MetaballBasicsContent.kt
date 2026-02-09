package com.skul.yuriy.composeplayground.feature.metaballBasic

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import com.skul.yuriy.composeplayground.feature.metaballBasic.edge.GooeyEdgeScreen
import com.skul.yuriy.composeplayground.feature.metaballBasic.text.TextMeltScreen
import com.skul.yuriy.composeplayground.feature.metaballBasic.text.TextMeltState

@RequiresApi(Build.VERSION_CODES.S)
@Composable
internal fun MetaballBasicsContent(
    selectedTab: MetaballBasicsTab,
    textMeltState: TextMeltState,
    textMeltScrollState: ScrollState,
    modifier: Modifier = Modifier,
) {
    val fullModifier = modifier.fillMaxSize()
    when (selectedTab) {
        MetaballBasicsTab.GooeyEdge -> GooeyEdgeScreen(modifier = fullModifier)
        else -> TextMeltScreen(
            state = textMeltState,
            scrollState = textMeltScrollState,
            modifier = fullModifier
        )
    }
}
