package com.skul.yuriy.composeplayground.feature.animatedRectButton

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.skul.yuriy.composeplayground.LocalNavBackStack
import com.skul.yuriy.composeplayground.R
import com.skul.yuriy.composeplayground.navigation.navigateUp
import com.skul.yuriy.composeplayground.util.cornerRedLinearGradient2
import com.skul.yuriy.composeplayground.util.regularComponents.CustomTopAppBar

@Composable
fun AnimatedRectBtnScreen() {
    val navBackStack = LocalNavBackStack.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = cornerRedLinearGradient2())
            .navigationBarsPadding()
    ) {

        CustomTopAppBar(
            modifier = Modifier.fillMaxWidth(),
            onNavUp = { navBackStack.navigateUp() },
            containerColor = Color.Transparent,
            title = stringResource(R.string.animated_rect_button)
        )

        AnimatedRectButtonScreenContent(
            modifier = Modifier
                .fillMaxSize()
        )
    }
}
