package com.skul.yuriy.composeplayground.feature.metaballEdgeText

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.skul.yuriy.composeplayground.LocalNavBackStack
import com.skul.yuriy.composeplayground.feature.metaballEdgeText.text.rememberTextMeltState
import com.skul.yuriy.composeplayground.navigation.navigateToTextMetabalConcept
import com.skul.yuriy.composeplayground.navigation.navigateUp


@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MetaballEdgeTextScreen(
    modifier: Modifier = Modifier,
) {
    val navBackStack = LocalNavBackStack.current
    var selectedTabOrdinal by rememberSaveable { mutableIntStateOf(MetaballEdgeTextTab.GooeyEdge.ordinal) }
    val selectedTab = MetaballEdgeTextTab.entries.getOrElse(selectedTabOrdinal) { MetaballEdgeTextTab.GooeyEdge }
    val textMeltState = rememberTextMeltState()
    val textMeltScrollState = rememberSaveable(saver = ScrollState.Saver) { ScrollState(initial = 0) }
    var shouldComposeBottomBar by remember { mutableStateOf(false) }
    val tabs = MetaballEdgeTextTab.entries

    val bottomBarVisible = selectedTab == MetaballEdgeTextTab.TextMelt

    val density = LocalDensity.current
    var bottomBarHeightDp by remember { mutableStateOf(0.dp) }

    val motion = rememberBottomBarMotion(
        visible = bottomBarVisible,
        barHeight = bottomBarHeightDp,
        onHidden = {
            shouldComposeBottomBar = false
        }
    )

    if (bottomBarVisible && !shouldComposeBottomBar) {
        shouldComposeBottomBar = true
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.White,
        topBar = {
            MetaballEdgeTextTopBar(
                tabs = tabs,
                selectedTab = selectedTab,
                onTabSelected = { selectedTabOrdinal = it.ordinal },
                onNavUp = { navBackStack.navigateUp() },
                shouldShowInfoAction = selectedTab == MetaballEdgeTextTab.TextMelt,
                onInfoClick = { navBackStack.navigateToTextMetabalConcept() },
            )
        },
        bottomBar = {
            if (shouldComposeBottomBar) {
                MetaballEdgeTextBottomBar(
                    modifier = Modifier
                        .onSizeChanged { size ->
                            bottomBarHeightDp = with(density) { size.height.toDp() }
                        }
                        .graphicsLayer {
                            translationY = with(density) { motion.barTranslationY.toPx() }
                        },
                    onPrevious = { textMeltState.previous() },
                    onNext = { textMeltState.next() },
                )
            }
        }
    ) { paddingValues: PaddingValues ->
        val animatedPadding = paddingValues.withAnimatedBottomInset(motion.contentBottomInset)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(animatedPadding)
        ) {
            MetaballEdgeTextContent(
                selectedTab = selectedTab,
                textMeltState = textMeltState,
                textMeltScrollState = textMeltScrollState
            )
        }
    }
}
