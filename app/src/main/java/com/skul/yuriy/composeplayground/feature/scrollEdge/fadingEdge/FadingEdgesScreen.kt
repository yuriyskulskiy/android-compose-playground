package com.skul.yuriy.composeplayground.feature.scrollEdge.fadingEdge

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.skul.yuriy.composeplayground.LocalNavController
import com.skul.yuriy.composeplayground.R
import com.skul.yuriy.composeplayground.util.ScreenBackground
import com.skul.yuriy.composeplayground.util.fadingTopBottomEdgesDp

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun FadingEdgesRoute() {
    ScreenBackground(
        modifier = Modifier.fillMaxSize(),
        imageRes = R.drawable.forest
    ) {
        FadingEdgesScreen(
            modifier = Modifier.fillMaxSize()
        )
    }
}


@Composable
fun FadingEdgesScreen(
    modifier: Modifier = Modifier
) {
    val navController = LocalNavController.current
    Scaffold(
        modifier = modifier,
        containerColor = Color.Transparent,
        topBar = {
            FadingEdgesTopBar(onNavigateUp = { navController.navigateUp() })
        },
        bottomBar = { BottomBar() }
    ) { paddingValues ->


        //switch for column or lazyColumn scroll content
        val showLazyListExample = true

        if (showLazyListExample) {
            LazyListScreenContent(
                modifier = Modifier.padding(paddingValues)
            )
        } else {
            RegularScrollColumn(
                modifier = Modifier.padding(paddingValues)
            )
        }

//        WrongWay(Modifier.padding(paddingValues)) //don't do this
    }
}


@Composable
fun LazyListScreenContent(
    modifier: Modifier = Modifier
) {

    val lazyListState = rememberLazyListState()
    val isAtTop: Boolean by remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex == 0 && lazyListState.firstVisibleItemScrollOffset == 0
        }
    }

    val isAtBottom by remember {
        derivedStateOf {
            val layoutInfo = lazyListState.layoutInfo
            val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()

            // Check if the last visible item is the last item AND if it's fully visible
            lastVisibleItem != null &&
                    lastVisibleItem.index == layoutInfo.totalItemsCount - 1 &&
                    lastVisibleItem.offset + lastVisibleItem.size <= layoutInfo.viewportEndOffset
        }
    }

    // Animated Dp values for the top and bottom fade heights
    val topFadeHeight by animateDpAsState(
        if (isAtTop) 0.dp else 150.dp,
        animationSpec = tween(durationMillis = 600),
        label = ""
    )

    val bottomFadeHeight by animateDpAsState(

        if (isAtBottom) 0.dp else 150.dp,
        animationSpec = tween(durationMillis = 600),
        label = ""
    )

    LazyColumn(
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
        state = lazyListState,

        modifier = modifier
            .fillMaxSize()
            .fadingTopBottomEdgesDp(
                topFadeHeight,
                bottomFadeHeight
            )
            .clipToBounds(),
    ) {
        item {
            Text(
                color = Color.White,
                text = stringResource(R.string.very_long_mock_text).trimIndent(),
                fontWeight = FontWeight.Bold
            )
        }
    }
}


@Composable
fun RegularScrollColumn(
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    // Track if the column is scrolled to the top
    val isAtTop by remember {
        derivedStateOf {
            scrollState.value == 0
        }
    }

    // Track if the column is scrolled to the bottom
    val isAtBottom by remember {
        derivedStateOf {
            scrollState.value == scrollState.maxValue
        }
    }

    // Animated Dp values for the top and bottom fades
    val topFadeHeight by animateDpAsState(
        targetValue = if (isAtTop) 0.dp else 150.dp, label = ""
    )

    val bottomFadeHeight by animateDpAsState(
        targetValue = if (isAtBottom) 0.dp else 150.dp, label = ""
    )

    Box(
        modifier
            .fillMaxSize()
            .fadingTopBottomEdgesDp(
                topFadeHeight = topFadeHeight,
                bottomFadeHeight = bottomFadeHeight
            )
            .clipToBounds()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState) // Apply scroll state
        ) {
            Text(
                color = Color.White,
                text = stringResource(R.string.very_long_mock_text).trimIndent(),
                modifier = Modifier.padding(16.dp),
                fontWeight = FontWeight.Normal
            )
        }
    }
}


// wrong solution
@Composable
fun WrongWay(modifier: Modifier = Modifier) {
    Box(
        modifier
            .fillMaxSize()
            .clipToBounds()
    ) {
        // Scrollable content inside the Box
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .clipToBounds()
        ) {
            // Long Lorem Ipsum text
            Text(
                color = Color.White,
                text = stringResource(R.string.very_long_mock_text).trimIndent(),
                modifier = Modifier.padding(16.dp),
                fontWeight = FontWeight.Normal
            )
        }

        // Top fading edge (gradient)
        Box(
            Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Black, Color.Transparent) // Gradient colors
                    )
                )
        )

        // Bottom fading edge (gradient)
        Box(
            Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .height(100.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black) // Gradient colors
                    )
                )
        )
    }
}
