package com.skul.yuriy.composeplayground.feature.metaballEdgeHorizontalScroll

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.skul.yuriy.composeplayground.feature.metaballEdgeHorizontalScroll.sections.CircularItemsSection
import com.skul.yuriy.composeplayground.feature.metaballEdgeHorizontalScroll.sections.HorizontalTextSection
import com.skul.yuriy.composeplayground.feature.metaballEdgeHorizontalScroll.sections.PlainIconsSection

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun MetaballEdgeHorizontalScrollContent(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
) {
    Column(
        modifier = modifier
            .background(Color.White)
            .padding(contentPadding)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        CircularItemsSection()
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, bottom = 12.dp),
            thickness = 1.dp,
            color = Color.Black
        )
        PlainIconsSection()
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, bottom = 12.dp),
            thickness = 1.dp,
            color = Color.Black
        )
        HorizontalTextSection()
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            thickness = 1.dp,
            color = Color.Black
        )
    }
}
