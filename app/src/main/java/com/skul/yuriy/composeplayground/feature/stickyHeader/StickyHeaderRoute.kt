package com.skul.yuriy.composeplayground.feature.stickyHeader

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
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
                        Text(text = stringResource(R.string.animated_sticky_header))
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


