package com.skul.yuriy.composeplayground.feature.metaballEdgeText.conceptSubscreen

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import com.skul.yuriy.composeplayground.LocalNavBackStack
import com.skul.yuriy.composeplayground.LocalSharedTransitionScope
import com.skul.yuriy.composeplayground.R
import com.skul.yuriy.composeplayground.navigation.navigateUp
import com.skul.yuriy.composeplayground.util.motion.rememberBottomBarMotion
import com.skul.yuriy.composeplayground.util.motion.withAnimatedBottomInset
import com.skul.yuriy.composeplayground.util.regularComponents.CustomTopAppBar
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun TextMetaballConceptScreen(
    modifier: Modifier = Modifier
) {
    val navBackStack = LocalNavBackStack.current
    val sharedTransitionScope = LocalSharedTransitionScope.current
    var blurRadiusDp by rememberSaveable { mutableFloatStateOf(0f) }
    var alphaFilterPercent by rememberSaveable { mutableFloatStateOf(0f) }
    var blurEnabled by rememberSaveable { mutableStateOf(true) }
    var alphaEnabled by rememberSaveable { mutableStateOf(true) }
    var showBottomBar by rememberSaveable { mutableStateOf(false) }
    val density = LocalDensity.current
    var bottomBarHeightDp by remember { mutableStateOf(0.dp) }
    val motion = rememberBottomBarMotion(
        visible = showBottomBar,
        barHeight = bottomBarHeightDp,
        onHidden = {}
    )
    val containerModifier = if (sharedTransitionScope != null) {
        with(sharedTransitionScope) {
            Modifier
                .fillMaxSize()
                .sharedBounds(
                    sharedContentState = rememberSharedContentState(key = TextMetaballConceptSharedKey),
                    animatedVisibilityScope = LocalNavAnimatedContentScope.current
                )
        }
    } else {
        Modifier.fillMaxSize()
    }
    LaunchedEffect(bottomBarHeightDp, showBottomBar) {
        if (!showBottomBar && bottomBarHeightDp > 0.dp) {
            delay(180)
            showBottomBar = true
        }
    }

    Scaffold(
        modifier = modifier.then(containerModifier),
        containerColor = Color.White,
        topBar = {
            CustomTopAppBar(
                title = stringResource(R.string.text_metabal_concept_title),
                onNavUp = { navBackStack.navigateUp() },
                containerColor = Color.White,
                navigationIconColor = Color.Black,
                titleColor = Color.Black,
                dividerColor = Color.LightGray
            )
        },
        bottomBar = {
            ConceptControlsBottomBar(
                blurRadiusDp = blurRadiusDp,
                onBlurRadiusChange = { blurRadiusDp = it },
                blurEnabled = blurEnabled,
                onBlurEnabledChange = { blurEnabled = it },
                alphaFilterPercent = alphaFilterPercent,
                onAlphaFilterChange = { alphaFilterPercent = it },
                alphaEnabled = alphaEnabled,
                onAlphaEnabledChange = { alphaEnabled = it },
                modifier = Modifier
                    .onSizeChanged { size ->
                        bottomBarHeightDp = with(density) { size.height.toDp() }
                    }
                    .graphicsLayer {
                        alpha = if (bottomBarHeightDp == 0.dp) 0f else 1f
                        translationY = with(density) {
                            val hiddenOffsetPx = bottomBarHeightDp.toPx()
                            if (showBottomBar) {
                                motion.barTranslationY.toPx()
                            } else {
                                hiddenOffsetPx
                            }
                        }
                    },
            )
        }
    ) { paddingValues ->
        val animatedPadding = paddingValues.withAnimatedBottomInset(motion.contentBottomInset)
        ConceptBlurTextContent(
            blurRadiusDp = blurRadiusDp,
            blurEnabled = blurEnabled,
            alphaEnabled = alphaEnabled,
            alphaFilterPercent = alphaFilterPercent,
            modifier = Modifier
                .fillMaxSize()
                .padding(animatedPadding)
        )
    }
}
