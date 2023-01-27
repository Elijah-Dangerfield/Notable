package com.dangerfield.noteslist

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dangerfield.core.common.doNothing
import com.dangerfield.noteslist.NotesListViewModel.NoteOrder.Alphabetic
import com.dangerfield.noteslist.NotesListViewModel.NoteOrder.Color
import com.dangerfield.noteslist.NotesListViewModel.NoteOrder.FirstCreated
import com.dangerfield.noteslist.NotesListViewModel.NoteOrder.LastEdited

@Composable
fun OrderSection(
    modifier: Modifier = Modifier,
    selectedOrder: NotesListViewModel.NoteOrder = LastEdited,
    onOrderChanged: (NotesListViewModel.NoteOrder) -> Unit
) {
    Column(modifier = modifier) {
        Row(modifier = Modifier.fillMaxWidth()) {
            DefaultRadioButton(
                text = stringResource(R.string.title),
                selected = selectedOrder == Alphabetic,
                onSelect = { onOrderChanged(Alphabetic) }
            )

            Spacer(modifier = Modifier.width(8.dp))

            DefaultRadioButton(
                text = stringResource(R.string.recently_edited),
                selected = selectedOrder == LastEdited,
                onSelect = { onOrderChanged(LastEdited) }
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Row(modifier = Modifier.fillMaxWidth()) {

            DefaultRadioButton(
                text = stringResource(R.string.color),
                selected = selectedOrder == Color,
                onSelect = { onOrderChanged(Color) }
            )

            Spacer(modifier = Modifier.width(8.dp))

            DefaultRadioButton(
                text = stringResource(R.string.recently_created),
                selected = selectedOrder == FirstCreated,
                onSelect = { onOrderChanged(FirstCreated) }
            )
        }
    }
}

@Preview
@Composable
fun PreviewOrderSection() {
    OrderSection(onOrderChanged = { doNothing() })
}
