package com.skul.yuriy.composeplayground.feature.metaballEdgesAndText

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import com.skul.yuriy.composeplayground.LocalSharedTransitionScope
import com.skul.yuriy.composeplayground.R
import com.skul.yuriy.composeplayground.feature.metaballEdgesAndText.textScreen.TextMetaballConceptSharedKey

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
internal fun MetaballEdgeTextTopBar(
    tabs: List<MetaballEdgeTextTab>,
    selectedTab: MetaballEdgeTextTab,
    onTabSelected: (MetaballEdgeTextTab) -> Unit,
    onNavUp: () -> Unit,
    shouldShowInfoAction: Boolean,
    onInfoClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val sharedTransitionScope = LocalSharedTransitionScope.current
    val infoActionModifier = if (sharedTransitionScope != null) {
        with(sharedTransitionScope) {
            Modifier.sharedBounds(
                sharedContentState = rememberSharedContentState(key = TextMetaballConceptSharedKey),
                animatedVisibilityScope = LocalNavAnimatedContentScope.current
            )
        }
    } else {
        Modifier
    }

    Column(modifier = modifier) {
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                navigationIconContentColor = Color.White,
                actionIconContentColor = Color.White,
                titleContentColor = Color.White,
                containerColor = Color.Black
            ),
            navigationIcon = {
                IconButton(onClick = onNavUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.go_back)
                    )
                }
            },
            title = {
                Text(text = stringResource(R.string.metaball_primer_title))
            },
            actions = {
                AnimatedVisibility(
                    visible = shouldShowInfoAction,
                    enter = fadeIn() + scaleIn(),
                    exit = fadeOut() + scaleOut()
                ) {
                    IconButton(
                        onClick = onInfoClick,
                        modifier = infoActionModifier
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = stringResource(R.string.metaball_primer_open_concept_info)
                        )
                    }
                }
            }
        )
        PrimaryTabRow(
            selectedTabIndex = selectedTab.ordinal,
            containerColor = Color.Black,
            contentColor = Color.White,
            divider = {},
            indicator = {
                Box(
                    modifier = Modifier
                        .tabIndicatorOffset(selectedTab.ordinal)
                        .fillMaxSize()
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                        .border(
                            width = 1.dp,
                            color = Color.White,
                            shape = RoundedCornerShape(percent = 50)
                        )
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab.ordinal == index,
                    onClick = { onTabSelected(tabs[index]) },
                    text = {
                        Text(
                            text = stringResource(tabs[index].titleRes),
                            color = if (selectedTab.ordinal == index) {
                                Color.White
                            } else {
                                Color.White.copy(alpha = 0.8f)
                            },
                            fontWeight = if (selectedTab.ordinal == index) {
                                FontWeight.Bold
                            } else {
                                FontWeight.Normal
                            }
                        )
                    }
                )
            }
        }
    }
}
