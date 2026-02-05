package com.skul.yuriy.composeplayground.feature.customBlur

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.skul.yuriy.composeplayground.LocalNavController
import com.skul.yuriy.composeplayground.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomBlurScreen(
    modifier: Modifier = Modifier
) {
    val navController = LocalNavController.current
    var blurRadius by remember { mutableStateOf(2.dp) }
    var blurMode by remember { mutableStateOf(CustomBlurMode.Native) }
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
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Go Back"
                        )
                    }
                },
                title = {
                    Text(
                        text = stringResource(R.string.custom_blur)
                    )
                },
                actions = {
                    IconButton(onClick = { blurRadius = blurRadius.nextBlurStep() }) {
                        Text(
                            text = blurRadius.value.toInt().toString(),
                            color = Color.White
                        )
                    }
                }
            )

        },
        bottomBar = {
            CustomBlurBottomBar(
                selectedMode = blurMode,
                onModeSelected = { blurMode = it }
            )
        }
    ) { paddingValues ->
        CustomBlurScrollingContent(
            modifier = Modifier.padding(paddingValues),
            blurRadius = blurRadius,
            blurMode = blurMode
        )
    }
}

private fun androidx.compose.ui.unit.Dp.nextBlurStep(): androidx.compose.ui.unit.Dp {
    val steps = listOf(2, 4, 6, 8, 10, 12, 14, 16)
    val current = value.toInt()
    val index = steps.indexOf(current)
    val nextIndex = if (index == -1) 0 else (index + 1) % steps.size
    return steps[nextIndex].dp
}

enum class CustomBlurMode {
    Native,
    AglslAlphaLiniar,
    AgslAlphaGaussian,
}

@Composable
private fun CustomBlurBottomBar(
    selectedMode: CustomBlurMode,
    onModeSelected: (CustomBlurMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    BottomAppBar(
        modifier = modifier,
        containerColor = Color.Black,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextButton(onClick = { onModeSelected(CustomBlurMode.Native) }) {
                Text(
                    text = "Native",
                    color = if (selectedMode == CustomBlurMode.Native) Color.White else Color.Gray
                )
            }
            TextButton(onClick = { onModeSelected(CustomBlurMode.AglslAlphaLiniar) }) {
                Text(
                    text = "AGSL Linear",
                    color = if (selectedMode == CustomBlurMode.AglslAlphaLiniar) Color.White else Color.Gray
                )
            }
            TextButton(onClick = { onModeSelected(CustomBlurMode.AgslAlphaGaussian) }) {
                Text(
                    text = "AGSL Gaussian",
                    color = if (selectedMode == CustomBlurMode.AgslAlphaGaussian) Color.White else Color.Gray
                )
            }
        }
    }
}
