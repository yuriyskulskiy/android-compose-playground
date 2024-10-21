package com.skul.yuriy.composeplayground.feature.rotationArk

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skul.yuriy.composeplayground.util.shadowborder.ArcPaddingType

@Composable
fun TopSettingsSection(
    selectedPaddingType: ArcPaddingType,
    onPaddingTypeSelected: (ArcPaddingType) -> Unit,
    showBorder: Boolean,
    onCheckedShowBorder: (Boolean) -> Unit
) {
    val paddingTypes = ArcPaddingType.entries

    Column(modifier = Modifier.padding(16.dp)) {
        paddingTypes.forEach { paddingType ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onPaddingTypeSelected(paddingType) }
                    .padding(vertical = 0.dp)
                    .height(36.dp)
            ) {
                RadioButton(
                    colors = RadioButtonDefaults.colors().copy(
                        selectedColor = Color.White,
                        unselectedColor = Color.Gray
                    ),
                    selected = (paddingType == selectedPaddingType),
                    onClick = { onPaddingTypeSelected(paddingType) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    fontSize = 12.sp,
                    text = paddingType.name,
                    color = if (paddingType == selectedPaddingType) {
                        Color.White
                    } else {
                        Color.Gray
                    }
                )
            }
        }

        // Checkbox for showing the border
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { onCheckedShowBorder(!showBorder) }
        ) {
            Checkbox(
                colors = CheckboxDefaults.colors().copy(
                    checkedBoxColor = Color.Transparent,
                    checkedBorderColor = Color.White
                ),
                checked = showBorder,
                onCheckedChange = onCheckedShowBorder
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Show border", color = if (showBorder) {
                    Color.White
                } else {
                    Color.Gray
                }
            )
        }
    }
}