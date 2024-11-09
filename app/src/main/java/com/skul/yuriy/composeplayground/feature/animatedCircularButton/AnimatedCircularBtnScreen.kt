package com.skul.yuriy.composeplayground.feature.animatedCircularButton

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.skul.yuriy.composeplayground.LocalNavController
import com.skul.yuriy.composeplayground.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimatedCircularBtnScreen() {
    val navController: NavController = LocalNavController.current
    Column(modifier = Modifier.fillMaxSize()) {

        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors().copy(
                containerColor = Color.Black,
                navigationIconContentColor = Color.Black
            ),
            navigationIcon = {
                IconButton(
                    colors = IconButtonDefaults.iconButtonColors().copy(
                        contentColor = Color.White
                    ),
                    onClick = {
                        navController.navigateUp()
                    }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Go Back"
                    )
                }
            },
            title = {
                Text(
                    color = Color.White,
                    text = stringResource(R.string.animated_circular_button)
                )
            }
        )


        AnimatedCircleButtonScreenContent(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        )

    }
}