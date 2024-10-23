package com.skul.yuriy.composeplayground.util.regularComponents

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.skul.yuriy.composeplayground.R

@Composable
fun NavBackIconButton(
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit
) {
    IconButton(onClick = navigateBack, modifier = modifier) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = stringResource(R.string.go_back)
        )
    }
}