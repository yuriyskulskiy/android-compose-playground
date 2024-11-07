package com.skul.yuriy.composeplayground.feature.stickyHeader

import java.util.UUID


data class Section(
    val id: String = UUID.randomUUID().toString(),
    val header: String,
    val list: List<Item>,
    val isExpanded: Boolean = true
)

data class Item(
    val id: String = UUID.randomUUID().toString(),
    val text: String
)