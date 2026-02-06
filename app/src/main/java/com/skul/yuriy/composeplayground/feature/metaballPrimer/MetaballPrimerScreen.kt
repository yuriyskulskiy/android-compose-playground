package com.skul.yuriy.composeplayground.feature.metaballPrimer

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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.skul.yuriy.composeplayground.LocalNavController
import com.skul.yuriy.composeplayground.R
import com.skul.yuriy.composeplayground.feature.metaballPrimer.edge.GooeyEdgeScreen
import com.skul.yuriy.composeplayground.feature.metaballPrimer.text.TextMeltScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MetaballPrimerScreen(
    modifier: Modifier = Modifier,
) {
    val navController = LocalNavController.current
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf(
        stringResource(R.string.metaball_primer_tab_edge_embrace),
        stringResource(R.string.metaball_primer_tab_text_melt),
    )

    Scaffold(
        modifier = modifier,
        containerColor = Color.White,
        topBar = {
            Column {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        navigationIconContentColor = Color.White,
                        titleContentColor = Color.White,
                        containerColor = Color.Black
                    ),
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Go Back"
                            )
                        }
                    },
                    title = {
                        Text(text = stringResource(R.string.metaball_primer_title))
                    },
                )
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Black,
                    contentColor = Color.White,
                    indicator = { tabPositions ->
                        Box(
                            modifier = Modifier
                                .tabIndicatorOffset(tabPositions[selectedTab])
                                .fillMaxSize()
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                                .border(
                                    width = 1.dp,
                                    color = Color.White,
                                    shape = androidx.compose.foundation.shape.RoundedCornerShape(999.dp)
                                )
                        )
                    }
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    text = title,
                                    color = if (selectedTab == index) {
                                        Color.White
                                    } else {
                                        Color.White.copy(alpha = 0.8f)
                                    },
                                    fontWeight = if (selectedTab == index) {
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
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (selectedTab) {
                0 -> GooeyEdgeScreen(modifier = Modifier.fillMaxSize())
                else -> TextMeltScreen(modifier = Modifier.fillMaxSize())
            }
        }
    }
}
