package com.skul.yuriy.composeplayground.feature.bottomEdge

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.skul.yuriy.composeplayground.LocalNavBackStack
import com.skul.yuriy.composeplayground.R
import com.skul.yuriy.composeplayground.navigation.navigateUp
import com.skul.yuriy.composeplayground.util.regularComponents.NavBackIconButton
import com.skul.yuriy.composeplayground.util.shadowborder.shadowWithClipIntersect


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomEdgeShadowScreen() {
    val navBackStack = LocalNavBackStack.current

    Column(
        Modifier
            .fillMaxSize()
            .background(Color.White)) {
        NavBackIconButton(
            Modifier
                .statusBarsPadding()
                .padding(24.dp),
            navigateBack = { navBackStack.navigateUp() }
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(64.dp),
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp)
        ) {

            //Rectangle bottom edge shadow
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors()
                    .copy(containerColor = Color.White),
                modifier = Modifier
                    .shadowWithClipIntersect(
                        elevation = 8.dp,
                        shape = RectangleShape,
                    ),
                title = { Text(stringResource(R.string.rectangle_bottom_edge)) },
                navigationIcon = {
                    NavBackIconButton(navigateBack = { navBackStack.navigateUp() })
                },
            )


            //Rounded corners bottom edge shadow
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors()
                    .copy(containerColor = Color.White),
                modifier = Modifier
                    .shadowWithClipIntersect(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(
                            bottomStart = 24.dp,
                            bottomEnd = 24.dp
                        )
                    ),
                title = {
                    Text(text = stringResource(R.string.rounded_corners_bottom_edge))
                },
                navigationIcon = {
                    NavBackIconButton(navigateBack = { navBackStack.navigateUp() })
                },
            )

            Row(
                horizontalArrangement = Arrangement
                    .spacedBy(32.dp, Alignment.CenterHorizontally),
                modifier = Modifier.fillMaxWidth()
            ) {

                //Square bottom edge shadow
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(150.dp, 150.dp)
                        .shadowWithClipIntersect(
                            elevation = 8.dp,
                            shape = RectangleShape
                        )
                        .background(color = Color.White)
                ) {
                    Text(
                        textAlign = TextAlign.Center,
                        text = stringResource(R.string.square_shape_bottom_shadow_edge)
                    )
                }

                //Circular bottom edge shadow
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(150.dp, 150.dp)
                        .shadowWithClipIntersect(
                            elevation = 8.dp,
                            shape = CircleShape
                        )
                        .background(color = Color.White)
                ) {
                    Text(
                        textAlign = TextAlign.Center,
                        text = stringResource(R.string.circle_shape_bottom_shadow)
                    )
                }
            }
        }
    }
}