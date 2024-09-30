package com.skul.yuriy.composeplayground.starter

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.skul.yuriy.composeplayground.LocalNavController
import com.skul.yuriy.composeplayground.R
import com.skul.yuriy.composeplayground.navigation.navigateToMetaballsScreen
import com.skul.yuriy.composeplayground.navigation.navigateToParallax
import com.skul.yuriy.composeplayground.navigation.navigateToStickyHeaderStateTracker


@Composable
fun StarterRoute() {
    StarterScreen(modifier = Modifier.fillMaxSize())
}

@Composable
fun StarterScreen(
    modifier: Modifier = Modifier
) {

    NavigationContent(modifier = modifier)
}

@Composable
fun NavigationContent(modifier: Modifier) {
    Column(
        modifier = modifier
            .statusBarsPadding()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.Start
    ) {
        val navController = LocalNavController.current
        NavigationItem(
            text = stringResource(R.string.parallax_scroll_list),
            onClick = { navController.navigateToParallax() })
        NavigationItem(text = stringResource(R.string.metaballs),
            onClick = { navController.navigateToMetaballsScreen() })
        NavigationItem(text = stringResource(R.string.sticky_header_state_tracker),
            onClick = { navController.navigateToStickyHeaderStateTracker() })

    }
}

@Composable
fun NavigationItem(modifier: Modifier = Modifier, text: String, onClick: () -> Unit) {

    OutlinedCard(
        onClick = onClick,
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth()
            .wrapContentHeight(),

//        colors = CardDefaults.cardColors().copy(containerColor = LightWhite),
        border = BorderStroke(1.dp, Color.DarkGray),
        shape = CircleShape
    ) {
        Box(
            contentAlignment = Alignment.CenterStart,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),

            ) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleLarge
            )
        }
    }

}


