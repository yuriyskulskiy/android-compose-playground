package com.skul.yuriy.composeplayground.feature.metaballEdgesRegular

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.skul.yuriy.composeplayground.R

@Composable
internal fun MetaballEdgeTextBottomBar(
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BottomAppBar(
        containerColor = Color.Black,
        modifier = modifier
    ) {
        val previousLabel = stringResource(R.string.metaball_basics_prev)
        val nextLabel = stringResource(R.string.metaball_basics_next)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            TextButton(onClick = onPrevious) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = previousLabel,
                    tint = Color.White
                )
                Text(
                    text = previousLabel,
                    color = Color.White,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            TextButton(onClick = onNext) {
                Text(
                    text = nextLabel,
                    color = Color.White,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Icon(
                    imageVector = Icons.Filled.ArrowForward,
                    contentDescription = nextLabel,
                    tint = Color.White
                )
            }
        }
    }
}
