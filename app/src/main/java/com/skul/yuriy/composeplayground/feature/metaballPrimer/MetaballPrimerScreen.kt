package com.skul.yuriy.composeplayground.feature.metaballPrimer

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.skul.yuriy.composeplayground.LocalNavController
import com.skul.yuriy.composeplayground.R
import com.skul.yuriy.composeplayground.feature.metaballPrimer.edge.GooeyEdgeScreen
import com.skul.yuriy.composeplayground.feature.metaballPrimer.text.TextMeltScreen
import com.skul.yuriy.composeplayground.feature.metaballPrimer.text.rememberTextMeltState

@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MetaballPrimerScreen(
    modifier: Modifier = Modifier,
) {
    val navController = LocalNavController.current
    var selectedTab by remember { mutableIntStateOf(0) }
    var showBottomBar by remember { mutableStateOf(true) }
    val textMeltState = rememberTextMeltState()
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
                    actions = {
                        IconButton(onClick = { showBottomBar = !showBottomBar }) {
                            Icon(
                                imageVector = Icons.Filled.Settings,
                                contentDescription = "Toggle Bottom Bar"
                            )
                        }
                    }
                )
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Black,
                    contentColor = Color.White,
                    divider = {},
                    indicator = { tabPositions ->
                        Box(
                            modifier = Modifier
                                .tabIndicatorOffset(tabPositions[selectedTab])
                                .fillMaxSize()
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                                .border(
                                    width = 1.dp,
                                    color = Color.White,
                                    shape = RoundedCornerShape(50)
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
        bottomBar = {
            AnimatedVisibility(
                visible = if (selectedTab == 0) showBottomBar else true,
                enter = fadeIn() + slideInVertically { it },
                exit = fadeOut() + slideOutVertically { it },
            ) {
                BottomAppBar(containerColor = Color.Black) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        TextButton(onClick = { textMeltState.previous() }) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Previous",
                                tint = Color.White
                            )
                            Text(
                                text = "Previous",
                                color = Color.White,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                        TextButton(onClick = { textMeltState.next() }) {
                            Text(
                                text = "Next",
                                color = Color.White,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Icon(
                                imageVector = Icons.Filled.ArrowForward,
                                contentDescription = "Next",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues: PaddingValues ->
        val bottom = paddingValues.calculateBottomPadding()
        Log.e("WTF", " bottom = " + bottom)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.Green)

        ) {
//            when (selectedTab) {
//                0 -> GooeyEdgeScreen(modifier = Modifier.fillMaxSize())
//                else -> TextMeltScreen(
//                    state = textMeltState,
//                    modifier = Modifier.fillMaxSize()
//                )
//            }
        }
    }
}
