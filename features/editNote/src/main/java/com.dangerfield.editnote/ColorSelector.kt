package com.dangerfield.editnote

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import com.dangerfield.notable.designsystem.NoteColors

const val DarkRatio = 0.5f

@Composable
fun ColorSelector(
    currentColor: Int,
    onColorSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        NoteColors.forEach { color ->
            Box(
                modifier = Modifier
                    .size(45.dp)
                    .shadow(15.dp, RoundedCornerShape(10.dp))
                    .clip(RoundedCornerShape(10.dp))
                    .background(color)
                    .border(
                        width = 3.dp,
                        color = if (currentColor == color.toArgb()) {
                            Color(ColorUtils.blendARGB(currentColor, Color.Black.toArgb(), DarkRatio))
                        } else Color.Transparent,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .clickable {
                        onColorSelected(color.toArgb())
                    }
            )
        }
    }
}
