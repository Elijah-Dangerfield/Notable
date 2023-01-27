package com.dangerfield.editnote

import androidx.compose.foundation.layout.Row
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
@Suppress("UnusedPrivateMember")
fun ColorSelector(
    modifier: Modifier = Modifier,
    selectedColor: Int,
    onColorSelected: (Int) -> Unit,
    colors: List<Int>
) {
    Text(text = "Color Selector Placeholder")
}

@Composable
fun ColorButton(
    color: Int,
    onColorSelected: (Int) -> Unit,
    selected: Boolean,
    colors: List<Int>
) {
    Row() {
        colors.forEach {
            RadioButton(
                selected = selected,
                onClick = { onColorSelected.invoke(color) },
                colors = RadioButtonDefaults.colors(
                    selectedColor = MaterialTheme.colors.primary,
                    unselectedColor = MaterialTheme.colors.onBackground
                )
            )
        }
    }
}
