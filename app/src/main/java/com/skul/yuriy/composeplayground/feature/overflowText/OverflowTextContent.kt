package com.skul.yuriy.composeplayground.feature.overflowText

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.skul.yuriy.composeplayground.feature.overflowText.investigate.implementation.FlowText_try2

@Composable
internal fun OverflowTextContent(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 16.dp),
    ) {
        Spacer(Modifier.size(20.dp))
        FlowText_try2(
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(40.dp))
    }
}
