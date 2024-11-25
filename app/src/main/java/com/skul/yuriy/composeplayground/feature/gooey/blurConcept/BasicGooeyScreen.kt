package com.skul.yuriy.composeplayground.feature.gooey.blurConcept

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.skul.yuriy.composeplayground.LocalNavController
import com.skul.yuriy.composeplayground.feature.gooey.MitosisButtonsSection
import com.skul.yuriy.composeplayground.util.regularComponents.CustomTopAppBar


@Composable
fun GooeyBasicScreen() {
    val navController: NavController = LocalNavController.current

    Scaffold(
        containerColor = Color.White,
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Surface(
                Modifier.fillMaxWidth(),
                shadowElevation = 8.dp
            ) {
                CustomTopAppBar(
                    navigationIconColor = Color.Black,
                    titleColor = Color.Black,
                    containerColor = Color.Transparent,
                    title = "Gooey Effect Across APIs",
                    onNavUp = { navController.navigateUp() },
                    dividerColor = Color.Transparent,
                    enableHorizontalDivider = false
                )
            }
        }
    ) { paddingValues ->
        GooeyBasicScreenContent(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        )
    }
}

@Composable
fun GooeyBasicScreenContent(modifier: Modifier) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        //android 13 example
        MitosisButtonsSection(
            modifier = Modifier.fillMaxWidth(),
            title = "RuntimeShader AGSL(RenderEffect)  and BlurEffect (Api lev 33 android 13)"
        ) {
            ExampleRuntimeShaderContent(Modifier.fillMaxWidth())
        }
        //Android 12
        MitosisButtonsSection(
            modifier = Modifier.fillMaxWidth(),
            title = "RenderEffect for Color filter and for blur (Api lev 31 android 12) "
        ) {
            ExampleRenderEffectShaderContent(Modifier.fillMaxWidth())
        }

        //regular example, tested android 10 Api lev 29
        MitosisButtonsSection(
            modifier = Modifier.fillMaxWidth(),
            title = "Regular Color Matrix filter with Native BlurMaskFilter (Pre-Api lev 31)"
        ) {
            ExampleStandardColorMatrixContent(Modifier.fillMaxWidth())
        }

        MitosisButtonsSection(
            modifier = Modifier.fillMaxWidth(),
            title = "Replacing Blur Mask with Circular Gradient for Pre-Android 10 Devices  (Pre-Api lev 29)"
        ) {
            ExampleLegacyContent(Modifier.fillMaxWidth())
        }

        MitosisButtonsSection(
            modifier = Modifier.fillMaxWidth(),
            title = "AGSL with color issue fix (Api lev 33 android 13)"
        ) {
            ExampleRuntimeRenderEffectColorFix(Modifier.fillMaxWidth())
        }
    }
}
