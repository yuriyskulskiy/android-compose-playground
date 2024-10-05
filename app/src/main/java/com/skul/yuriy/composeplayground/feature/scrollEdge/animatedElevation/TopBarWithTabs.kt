package com.skul.yuriy.composeplayground.feature.scrollEdge.animatedElevation

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.skul.yuriy.composeplayground.LocalNavController
import com.skul.yuriy.composeplayground.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarWithTabs(
    modifier: Modifier = Modifier,
    selectedTab: Tab,
    onTabSelected: (Tab) -> Unit,
) {
    val navController = LocalNavController.current
    Column(modifier = modifier) {
        TopAppBar(
            navigationIcon = {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Go Back"
                    )
                }
            },
            title = {
                Text(text = stringResource(R.string.animated_elevation))
            }
        )
        TabRow(selectedTabIndex = selectedTab.value,
            divider = { /* No divider */ },
            indicator = { /* No indicator */ }  //just for article demo - in real project I would keep indicator
        ) {
            Tab(
                text = {
                    Text(
                        style = MaterialTheme.typography.labelLarge,
                        text = stringResource(R.string.regular_column),
                        color = if (selectedTab is Tab.TabColumn) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                selected = selectedTab == Tab.TabColumn,
                onClick = { onTabSelected(Tab.TabColumn) }
            )
            Tab(
                text = {
                    Text(
                        style = MaterialTheme.typography.labelLarge,
                        text = stringResource(R.string.lazy_column),
                        color = if (selectedTab == Tab.TabLazColumn) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                selected = selectedTab == Tab.TabLazColumn,
                onClick = { onTabSelected(Tab.TabLazColumn) }
            )
        }
    }
}
