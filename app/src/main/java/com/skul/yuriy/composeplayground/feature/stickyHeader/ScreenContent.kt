package com.skul.yuriy.composeplayground.feature.stickyHeader

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skul.yuriy.composeplayground.R

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ScreenContent(
    modifier: Modifier = Modifier,
    sections: List<Section>,
    toggleExpanded: (String) -> Unit
) {
    val lazyListState = rememberLazyListState()
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp),
        modifier = modifier,
        state = lazyListState,

        ) {
        sections.forEach { section ->
            stickyHeader(key = "header-${section.id}") {
                val isHeaderAtTop by
                rememberStickyHeaderActive(lazyListState, key = "header-${section.id}")

                val elevation by animateDpAsState(
                    targetValue = if (isHeaderAtTop) 4.dp else 0.dp,
                    label = "sticky-header-elevation-animation"
                )

                HeaderContent(
                    section = section,
                    toggleExpanded = toggleExpanded,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 0.dp, vertical = 4.dp)
                        .shadow(
                            elevation = elevation,
                            clip = true,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { toggleExpanded(section.id) }
                        .background(Color.White)
                        .padding(start = 16.dp),
                )
            }

            items(items = section.list,
                key = { it.id }) { item ->
                AnimatedVisibility(
                    visible = section.isExpanded,
                    enter = expandVertically(animationSpec = tween()),
                    exit = shrinkVertically(animationSpec = tween())
                ) {
                    Text(
                        modifier = Modifier.padding(vertical = 4.dp),
                        text = item.text,
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 24.sp)
                    )
                }
            }
        }
    }
}

@Composable
fun HeaderContent(
    section: Section,
    toggleExpanded: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = section.header,
            style = MaterialTheme.typography.titleLarge.copy(fontStyle = FontStyle.Italic),
            modifier = Modifier.weight(1f)
        )

        IconButton(
            onClick = {
                toggleExpanded(section.id)
            }
        ) {
            Icon(
                painter = painterResource(
                    id = if (section.isExpanded) {
                        R.drawable.ic_unfold_less_24
                    } else {
                        R.drawable.ic_unfold_more_24
                    }
                ),
                contentDescription = if (section.isExpanded) "Collapse" else "Expand"
            )
        }
    }
}


@Composable
fun rememberStickyHeaderActive(state: LazyListState, key: Any): State<Boolean> = remember(state) {
    derivedStateOf {
        val items = state.layoutInfo.visibleItemsInfo
        val header = items.getOrNull(0) ?: return@derivedStateOf false
        val nextElement = items.getOrNull(1) ?: return@derivedStateOf false

//        if ((item.key as String).startsWith("header")) return@derivedStateOf false
//        if (header.key == key) {
//            Log.e("WTF","next element key "+item.key)
//            if (header.offset < 0 || header.offset > 0) return@derivedStateOf false
//            return@derivedStateOf header.size > item.offset
//        } else {
//            return@derivedStateOf false
//        }
        header.key == key && header.offset == 0 && header.size > nextElement.offset
//        header.key == key
//                && item.offset < header.size && !(item.key as String).startsWith("header")
    }
}