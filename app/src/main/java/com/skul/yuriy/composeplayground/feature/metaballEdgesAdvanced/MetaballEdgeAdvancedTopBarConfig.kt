package com.skul.yuriy.composeplayground.feature.metaballEdgesAdvanced

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.skul.yuriy.composeplayground.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MetaballEdgeAdvancedTopBar(
    selectedMode: MetaballEdgeAdvancedMode,
    onBackClick: () -> Unit,
    onModeSelected: (MetaballEdgeAdvancedMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Black),
    ) {
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                navigationIconContentColor = Color.White,
                titleContentColor = Color.White,
                containerColor = Color.Black,
            ),
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.go_back),
                    )
                }
            },
            title = {
                Text(text = stringResource(R.string.metaball_edge_advanced))
            },
        )

        MetaballEdgeAdvancedModeSwitch(
            selectedMode = selectedMode,
            onModeSelected = onModeSelected,
        )
    }
}

@Composable
private fun MetaballEdgeAdvancedModeSwitch(
    selectedMode: MetaballEdgeAdvancedMode,
    onModeSelected: (MetaballEdgeAdvancedMode) -> Unit,
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.25f),
                shape = RoundedCornerShape(14.dp),
            )
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        MetaballEdgeAdvancedModeButton(
            title = "Gooey",
            selected = selectedMode == MetaballEdgeAdvancedMode.Gooey,
            onClick = { onModeSelected(MetaballEdgeAdvancedMode.Gooey) },
            modifier = Modifier.weight(1f),
        )
        MetaballEdgeAdvancedModeButton(
            title = "Melt",
            selected = selectedMode == MetaballEdgeAdvancedMode.Melt,
            onClick = { onModeSelected(MetaballEdgeAdvancedMode.Melt) },
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun MetaballEdgeAdvancedModeButton(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TextButton(
        onClick = onClick,
        modifier = modifier.background(
            color = if (selected) Color.White else Color.Transparent,
            shape = RoundedCornerShape(10.dp),
        ),
    ) {
        Text(
            text = title,
            color = if (selected) Color.Black else Color.White,
            fontWeight = FontWeight.SemiBold,
        )
    }
}
