package com.skul.yuriy.composeplayground.feature.gooey

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun MitosisButtonsSection(
    modifier: Modifier = Modifier,
    title: String,
    style: TextStyle = LocalTextStyle.current,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Display the content passed as a parameter
        content()

        // Display the title below the content
        Text(
            modifier = Modifier
                .align(Alignment.Start)
                .padding(horizontal = 16.dp)
                .padding(bottom = 8.dp),
            textAlign = TextAlign.Start,
            text = title, color = Color.Black,
            style = style

        )
        HorizontalDivider(
            Modifier
                .padding(vertical = 0.dp, horizontal = 16.dp)
        )
    }
}