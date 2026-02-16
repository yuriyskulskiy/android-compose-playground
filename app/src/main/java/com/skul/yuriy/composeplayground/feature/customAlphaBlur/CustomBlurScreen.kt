package com.skul.yuriy.composeplayground.feature.customAlphaBlur

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.skul.yuriy.composeplayground.LocalNavBackStack
import com.skul.yuriy.composeplayground.feature.customAlphaBlur.state.CustomBlurAction
import com.skul.yuriy.composeplayground.feature.customAlphaBlur.state.CustomBlurDataState
import com.skul.yuriy.composeplayground.feature.customAlphaBlur.state.reduce
import com.skul.yuriy.composeplayground.navigation.navigateUp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomBlurScreen(
    modifier: Modifier = Modifier
) {
    val navBackStack = LocalNavBackStack.current
    var state by rememberSaveable(
        stateSaver = CustomBlurDataState.Saver
    ) {
        mutableStateOf(CustomBlurDataState())
    }
    val onAction: (CustomBlurAction) -> Unit = { action ->
        state = state.reduce(action)
    }

    Scaffold(
        modifier = modifier,
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    navigationIconContentColor = Color.White,
                    titleContentColor = Color.White,
                    containerColor = Color.Black
                ),
                navigationIcon = {
                    IconButton(onClick = { navBackStack.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Go Back"
                        )
                    }
                },
                title = {
                    Text(
                        text = state.selectedPage.displayName()
                    )
                },
                actions = {
                    DarkBarOutlinedButton(
                        label = "${state.blurRadiusDp.value.toInt()} dp",
                        onClick = { onAction(CustomBlurAction.ChangeBlurRadius) },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            )

        },
        bottomBar = {
            CustomBlurBottomBar(
                selectedPage = state.selectedPage,
                selectedMode = state.blurMode,
                onModeSelected = { onAction(CustomBlurAction.SelectBlurMode(it)) },
                selectedKernelQuality = state.blurKernelQuality,
                onKernelQualitySelected = {
                    onAction(CustomBlurAction.SelectBlurKernelQuality(it))
                },
                showBackward = !state.isFirstPage,
                showForward = !state.isLastPage,
                onBackward = {
                    onAction(CustomBlurAction.PreviousPage)
                },
                onForward = {
                    onAction(CustomBlurAction.NextPage)
                }
            )
        }
    ) { paddingValues ->
        CustomBlurScrollingContent(
            modifier = Modifier
                .padding(paddingValues),
            blurRadius = state.blurRadiusDp,
            blurMode = state.blurMode,
            selectedPage = state.selectedPage,
            selectedKernelQuality = state.blurKernelQuality
        )
    }
}
