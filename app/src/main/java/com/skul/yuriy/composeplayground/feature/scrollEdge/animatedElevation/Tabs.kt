package com.skul.yuriy.composeplayground.feature.scrollEdge.animatedElevation;

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RegularColumnTab(
    modifier: Modifier = Modifier,
    columnTabUiData: String,
) {
    Column(
        modifier
            .padding(16.dp)
    ) {
        Text(
            text = columnTabUiData,
            style = MaterialTheme.typography.bodyLarge

        )
    }
}


@Composable
fun LazyColumnTab(
    modifier: Modifier = Modifier,
    lazyColumnTabUiData: List<String>,
    lazyListState: LazyListState
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(16.dp),
        state = lazyListState
    ) {
        items(items = lazyColumnTabUiData) { itemText ->
            Text(
                text = itemText,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

