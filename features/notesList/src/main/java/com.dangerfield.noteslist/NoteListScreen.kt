package com.dangerfield.noteslist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dangerfield.core.notesapi.Note
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun NoteListRoute(
    viewModel: NotesListViewModel,
    onNoteSelected: (Note) -> Unit,
    onAddNoteSelected: () -> Unit
) {
    val state by viewModel.stateStream.collectAsStateWithLifecycle()

    NoteListScreen(
        state = state,
        onDeleteClicked = { viewModel.deleteNote(it) },
        onOrderChanged = { viewModel.updateSortOrder(it) },
        onRestoreNote = { viewModel.restoreLastDeletedNote() },
        onAddNoteSelected = onAddNoteSelected,
        onNoteSelected = onNoteSelected
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NoteListScreen(
    state: NotesListViewModel.State,
    onDeleteClicked: (Note) -> Unit,
    onOrderChanged: (NotesListViewModel.NoteOrder) -> Unit,
    onRestoreNote: () -> Unit,
    onAddNoteSelected: () -> Unit,
    onNoteSelected: (Note) -> Unit
) {
    val isOrderSectionVisible = remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier,
        floatingActionButton = { CreateNoteButton(onAddNoteSelected) },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.your_notes)) },
                actions = {
                    OrderSectionMenuButton(isOrderSectionVisible)
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) {

        Column(
            modifier = Modifier.padding(
                top = it.calculateTopPadding(),
                bottom = it.calculateBottomPadding(),
                start = it.calculateStartPadding(LayoutDirection.Ltr),
                end = it.calculateEndPadding(LayoutDirection.Ltr)
            )
        ) {
            AnimatedVisibility(
                visible = isOrderSectionVisible.value,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()

            ) {
                OrderSection(
                    modifier = Modifier.fillMaxWidth(),
                    onOrderChanged = onOrderChanged,
                    selectedOrder = state.noteOrder
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Notes(
                state,
                onDeleteClicked,
                scope,
                snackbarHostState,
                onRestoreNote,
                onNoteSelected
            )
        }
    }
}

@Composable
private fun Notes(
    state: NotesListViewModel.State,
    onDeleteClicked: (Note) -> Unit,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    onRestoreNote: () -> Unit,
    onNoteSelected: (Note) -> Unit
) {
    LazyColumn(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(state.notes) { note ->
            NoteItem(
                note = note,
                onDeleteClicked = { note ->
                    onDeleteClicked(note)
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = "Note Deleted",
                            actionLabel = "Undo"
                        ).let { result ->
                            if (result == SnackbarResult.ActionPerformed) {
                                onRestoreNote()
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNoteSelected(note) }
            )
        }
    }
}

@Composable
private fun OrderSectionMenuButton(isOrderSectionVisible: MutableState<Boolean>) {
    IconButton(onClick = {
        isOrderSectionVisible.value = !isOrderSectionVisible.value
    }) {
        Icon(
            imageVector = Icons.Filled.Menu,
            contentDescription = "Show Ordering Options for Notes",
            tint = MaterialTheme.colors.onSurface
        )
    }
}

@Composable
fun CreateNoteButton(onAddNoteSelected: () -> Unit) {
    FloatingActionButton(
        onClick = onAddNoteSelected,
        backgroundColor = MaterialTheme.colors.primary
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = stringResource(R.string.create_new_note),
            tint = MaterialTheme.colors.onPrimary
        )
    }
}

@Composable
@Preview
fun PreviewNotesScreen() {
    NoteListScreen(
        state = NotesListViewModel.State(
            notes = listOf(
                Note(
                    title = "TitleTitleTitleTitleTitleTitleTitleTitleTitleTitle",
                    content = "Content",
                    createdAt = 0L,
                    updatedAt = 0L,
                    color = Color.Red.toArgb(),
                    id = "ID123"
                ),
                Note(
                    title = "Title",
                    content = "ContentContentContentContentContentContentContentContentContentContent" +
                        "ContentContentContentContentContentContentContentContentContentContentContent" +
                        "ContentContentContentContentContentContentContentContentContentContentContent" +
                        "ContentContentContentContentContentContentContentContentContentContentContent",
                    createdAt = 0L,
                    updatedAt = 0L,
                    color = Color.Blue.toArgb(),
                    id = "ID123"
                )
            ),
            messages = listOf(),
            noteOrder = NotesListViewModel.NoteOrder.Alphabetic
        ),
        onDeleteClicked = {},
        onOrderChanged = {},
        onRestoreNote = {},
        onAddNoteSelected = {},
        onNoteSelected = {}
    )
}
