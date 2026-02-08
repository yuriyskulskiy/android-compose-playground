package com.skul.yuriy.composeplayground.feature.metaballBasic

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.skul.yuriy.composeplayground.R
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.shape.RoundedCornerShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MetaballBasicsTopBar(
    tabs: List<MetaballBasicsTab>,
    selectedTab: MetaballBasicsTab,
    onTabSelected: (MetaballBasicsTab) -> Unit,
    onNavUp: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                navigationIconContentColor = Color.White,
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
            actions = {}
        )
        TabRow(
            selectedTabIndex = selectedTab.ordinal,
            containerColor = Color.Black,
            contentColor = Color.White,
            divider = {},
            indicator = { tabPositions ->
                Box(
                    modifier = Modifier
                        .tabIndicatorOffset(tabPositions[selectedTab.ordinal])
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
