package com.skul.yuriy.composeplayground.feature.rotationArk

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
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
import com.skul.yuriy.composeplayground.R
import com.skul.yuriy.composeplayground.util.shadowborder.ArcPaddingType


@Composable
fun AnimatedArkScreen() {
    var selectedPaddingType by remember { mutableStateOf(ArcPaddingType.HALF_INSIDE_HALF_OUTSIDE) }
    var showBorder by remember { mutableStateOf(false) }
    var bodyWidth by remember { mutableStateOf(4.dp) }
    var shadowWidth by remember { mutableStateOf(24.dp) }
    var blurRadius by remember { mutableStateOf(12.dp) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {
        TopSettingsSection(
            selectedPaddingType = selectedPaddingType,
            onPaddingTypeSelected = { selectedPaddingType = it },
            onCheckedShowBorder = { showBorder = it },
            showBorder = showBorder
        )
        HorizontalDivider(Modifier.padding(horizontal = 16.dp))
        Spacer(Modifier.size(48.dp))

        RotationBox(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(200.dp)
                .then(
                    if (showBorder) {
                        Modifier.border(
                            BorderStroke(1.dp, color = Color.White),
                            shape = CircleShape
                        )
                    } else {
                        Modifier
                    }
                ),
            selectedPaddingType = selectedPaddingType,
            innerBodyWidth = bodyWidth,
            shadowWidth = shadowWidth,
            blurRadius = blurRadius,
        ) {
            Text(
                text =
                stringResource(R.string.pressed),
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.size(48.dp))
        HorizontalDivider(Modifier.padding(horizontal = 16.dp))
        // Body Width  slider
        SliderSection(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 4.dp)
                .padding(horizontal = 16.dp),
            label = stringResource(R.string.body),
            currentValue = bodyWidth,
            valueRange = 1..12,
            stepSize = 1,
            onValueChange = { newWidth -> bodyWidth = newWidth }
        )

        // Shadow Width Slider
        SliderSection(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            label = stringResource(R.string.halo),
            currentValue = shadowWidth,
            valueRange = 12..56,
            stepSize = 2,
            onValueChange = { newWidth -> shadowWidth = newWidth }
        )

        // BlurRadius Slider
        SliderSection(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),

            label = stringResource(R.string.blur),
            currentValue = blurRadius,
            valueRange = 6..56,
            stepSize = 1,
            onValueChange = { newWidth -> blurRadius = newWidth }
        )
    }
}

