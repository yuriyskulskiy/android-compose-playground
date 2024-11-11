package com.skul.yuriy.composeplayground.util.regularComponents

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp


@Composable
fun RadioTextButton(
    offsetValue: Dp,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null, // Nullable onClick argument
    selectedColor: Color = Color.Green,
    unselectedColor: Color = Color.Gray.copy(alpha = 0.6f),
    selectedTextColor: Color = Color.White,
    unselectedTextColor: Color = Color.Gray
) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .then(
                if (onClick != null) {
                    Modifier.clickable { onClick() }
                } else {
                    Modifier
                }
            )
    ) {
        Text(
            text = "${offsetValue.value.toInt()}",
            color = if (isSelected) selectedTextColor else unselectedTextColor
        )
        RadioButton(
            selected = isSelected,
            onClick = onClick, // Assign directly without invoking it
            colors = RadioButtonDefaults.colors(
                selectedColor = selectedColor,
                unselectedColor = unselectedColor
            )
        )
    }
}
