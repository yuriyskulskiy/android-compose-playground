package com.skul.yuriy.composeplayground.feature.vectorIconShadow

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.skul.yuriy.composeplayground.LocalNavBackStack
import com.skul.yuriy.composeplayground.navigation.navigateUp
import com.skul.yuriy.composeplayground.util.regularComponents.CustomTopAppBar

@Composable
fun VectorDrawableShadowScreen() {
    val navBackStack = LocalNavBackStack.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .navigationBarsPadding()
    ) {

        CustomTopAppBar(
            modifier = Modifier.fillMaxWidth(),
            onNavUp = { navBackStack.navigateUp() },
            title = "Vector drawable shadow"
        )

        VectorDrawableScreenContent(
            modifier = Modifier
                .fillMaxSize()
        )
    }
}

@Composable
fun VectorDrawableScreenContent(
    modifier: Modifier = Modifier
) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        DropShadowSection()
        HorizontalDivider(thickness = 1.dp, color = Color.Gray)
        SpreadHaloShadowSection()
        Spacer(modifier = Modifier.height(150.dp))
    }
}