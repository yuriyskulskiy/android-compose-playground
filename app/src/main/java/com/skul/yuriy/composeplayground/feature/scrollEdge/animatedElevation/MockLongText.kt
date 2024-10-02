package com.skul.yuriy.composeplayground.feature.scrollEdge.animatedElevation

fun lazyColumnTabDataMock(): List<String> {
    return generateMockListData(40, "This is lazy column data text")
}

private fun generateMockListData(size: Int, text: String): List<String> {
    return List(size) { text }
}

fun columnTabDataMock(): String {
    return generateRepeatedLongText("This is just a text inside regular column", 60)
}

private fun generateRepeatedLongText(text: String, times: Int): String {
    return buildString {
        repeat(times) {
            append("$text ")
        }
    }
}