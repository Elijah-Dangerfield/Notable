package com.dangerfield.noteslist

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import com.dangerfield.core.common.doNothing
import com.dangerfield.core.notesapi.Note

@Composable
@Suppress("MagicNumber")
fun NoteItem(
    note: Note,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 10.dp,
    cutCornerSize: Dp = 30.dp,
    onDeleteClicked: (Note) -> Unit
) {

    Box(modifier = modifier) {

        /*
        using match parent size as fillMaxSize will impact the parent to stretch to max size
        match parent says to just let the box be however big its going to be and then make this canvas match it

        using canvas is great for custom views
         */
        Canvas(modifier = Modifier.matchParentSize()) {
            val clipPath = Path().apply {
                // from 0,0 to
                lineTo(size.width - cutCornerSize.toPx(), 0f)
                // from x,y of last line ending to
                lineTo(size.width, cutCornerSize.toPx())
                lineTo(size.width, size.height)
                lineTo(0f, size.height)
                // draws from last line to 0,0. CLoses the path
                close()
            }

            clipPath(clipPath) {
                drawRoundRect(
                    color = Color(note.color),
                    size = size,
                    cornerRadius = CornerRadius(cornerRadius.toPx())
                )

                drawRoundRect(
                    color = Color(ColorUtils.blendARGB(note.color, 0x000000, 0.2f)),
                    size = Size(cutCornerSize.toPx() + 100f, cutCornerSize.toPx() + 100f),
                    topLeft = Offset(size.width - cutCornerSize.toPx(), -100f),
                    cornerRadius = CornerRadius(cornerRadius.toPx())
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(end = 32.dp)
        ) {
            Text(
                text = note.title,
                style = MaterialTheme.typography.h6,
                color = MaterialTheme.colors.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = note.content,
                style = MaterialTheme.typography.body1,
                color = MaterialTheme.colors.onSurface,
                maxLines = 10,
                overflow = TextOverflow.Ellipsis
            )
        }

        IconButton(
            onClick = { onDeleteClicked(note) },
            modifier = Modifier.align(Alignment.BottomEnd)
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = stringResource(R.string.delete_note),
                tint = MaterialTheme.colors.onSurface
            )
        }
    }
}

@Preview
@Composable
fun previewNoteItem() {
    NoteItem(
        note = Note(
            title = "Title",
            content = "Very long content",
            createdAt = 0L,
            updatedAt = 0L,
            color = Color.Red.toArgb(),
            id = "ID123"
        )
    ) {
        doNothing()
    }
}
