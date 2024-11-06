package com.skul.yuriy.composeplayground.feature.stickyHeader

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.skul.yuriy.composeplayground.LocalNavController
import com.skul.yuriy.composeplayground.R


@Composable
fun StickyHeaderRoute(
    viewModel: StickyHeaderViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    StickyHeaderScreen(
        uiState,
        toggleExpanded = { viewModel.expandSection(it) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StickyHeaderScreen(
    uiState: UiStateStickyScreen.Data,
    toggleExpanded: (String) -> Unit
) {

    val navController: NavController = LocalNavController.current

    Scaffold(
        containerColor = Color.White,
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Surface(
                Modifier.fillMaxWidth(),
                shadowElevation = 8.dp
            ) {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Go Back"
                            )
                        }
                    },
                    title = {
                        Text(text = stringResource(R.string.animated_elevation))
                    }
                )
            }
        }
    ) { paddingValues ->

        ScreenContent(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            sections = uiState.data,
            toggleExpanded = toggleExpanded
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ScreenContent(
    modifier: Modifier = Modifier,
    sections: List<Section>,
    toggleExpanded: (String) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp),
        modifier = modifier

    ) {
        // Loop through each section
        sections.forEach { section ->

            stickyHeader {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 0.dp, vertical = 4.dp)
                        .shadow(elevation = 0.dp, clip = true, shape = RoundedCornerShape(8.dp))
                        .clip(RoundedCornerShape(8.dp)) // Clip to rounded corners
                        .clickable { toggleExpanded(section.id) }
                        .background(Color.White)
                        .padding(start = 16.dp),

                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = section.header,
                        style = MaterialTheme.typography.titleLarge.copy(fontStyle = FontStyle.Italic),
                        modifier = Modifier.weight(1f) // Makes the text occupy the left side
                    )

                    IconButton(
                        onClick = {
                            // Toggle the isExpanded state for this section
                            toggleExpanded(section.id)
                        }
                    ) {
                        Icon(
                            imageVector = if (section.isExpanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                            contentDescription = if (section.isExpanded) "Collapse" else "Expand"
                        )
                    }
                }
            }

            items(items = section.list,
                key = { it.id }) { item ->
                AnimatedVisibility(
                    visible = section.isExpanded,
                    enter =
//                    fadeIn(animationSpec = tween()) +
                    expandVertically(animationSpec = tween()),
                    exit =
//                    fadeOut(animationSpec = tween()) +
                    shrinkVertically(animationSpec = tween())
                ) {
                    Text(
                        text = item.text,
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 24.sp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                }
            }
        }
    }
}


