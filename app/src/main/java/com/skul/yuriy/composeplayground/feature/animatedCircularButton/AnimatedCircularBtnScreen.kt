package com.skul.yuriy.composeplayground.feature.animatedCircularButton

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.skul.yuriy.composeplayground.LocalNavController
import com.skul.yuriy.composeplayground.R
import com.skul.yuriy.composeplayground.util.regularComponents.CustomTopAppBar

@Composable
fun AnimatedCircularBtnScreen() {
    val navController: NavController = LocalNavController.current
    Column(modifier = Modifier.fillMaxSize()) {

        CustomTopAppBar(
            modifier = Modifier.fillMaxWidth(),
            onNavUp = { navController.navigateUp() },
            title = stringResource(R.string.animated_circular_button)
        )

        AnimatedCircleButtonScreenContent(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        )
    }
}