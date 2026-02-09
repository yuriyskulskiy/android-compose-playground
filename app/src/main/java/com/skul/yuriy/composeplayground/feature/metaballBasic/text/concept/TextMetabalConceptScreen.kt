package com.skul.yuriy.composeplayground.feature.metaballBasic.text.concept

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import com.skul.yuriy.composeplayground.LocalNavBackStack
import com.skul.yuriy.composeplayground.LocalSharedTransitionScope
import com.skul.yuriy.composeplayground.R
import com.skul.yuriy.composeplayground.navigation.navigateUp
import com.skul.yuriy.composeplayground.util.regularComponents.CustomTopAppBar

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun TextMetabalConceptScreen(
    modifier: Modifier = Modifier
) {
    val navBackStack = LocalNavBackStack.current
    val sharedTransitionScope = LocalSharedTransitionScope.current
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

    Box(
        modifier = modifier.then(containerModifier)
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
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
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "hello wotld")
            }
        }
    }
}
