package com.dangerfield.editnote

import android.annotation.SuppressLint
import androidx.compose.animation.Animatable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dangerfield.core.notesapi.NoteColor
import kotlinx.coroutines.launch

@Composable
fun EditNoteRoute(
    viewModel: EditNoteViewModel,
    onDone: () -> Unit
) {
    val state = viewModel.stateStream.collectAsState()
    val isBlockingSavingNote = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    EditNoteScreen(
        state = state.value,
        onColorChanged = { viewModel.updateColor(it) },
        saveNote = { viewModel.save() },
        onDoneClicked = {
            viewModel.save()
            coroutineScope.launch {
                isBlockingSavingNote.value = true
                val id = viewModel.waitForNoteFinishedSaving()
                isBlockingSavingNote.value = false
                onDone()
                id?.let { eventId -> viewModel.resolveEvent(eventId) }
            }
        },
        onTitleChanged = { viewModel.updateTitle(it) },
        onContentChanged = { viewModel.updateContent(it) },
        isBlockingSavingNote = isBlockingSavingNote.value
    )
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
private fun EditNoteScreen(
    state: EditNoteViewModel.State,
    onColorChanged: (Int) -> Unit,
    saveNote: () -> Unit,
    onDoneClicked: () -> Unit,
    onTitleChanged: (String) -> Unit,
    onContentChanged: (String) -> Unit,
    isBlockingSavingNote: Boolean
) {

    val isColorSectionVisible = remember { mutableStateOf(false) }
    val color = remember { Animatable(Color(state.color)) }
    val coroutineScope = rememberCoroutineScope()
    val isDoneSectionVisible = !state.title.isNullOrEmpty() || !state.content.isNullOrEmpty()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color.value)
            .padding(12.dp)
    ) {
        Column(
            modifier = Modifier.matchParentSize()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                ColorSectionMenuButton(isColorSectionVisible, isBlockingSavingNote)
                AnimatedVisibility(visible = isDoneSectionVisible) {
                    DoneMenuButton(
                        onDoneClicked = { onDoneClicked() },
                        isBlockingSavingNote
                    )
                }
            }

            AnimatedVisibility(
                visible = isColorSectionVisible.value,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()

            ) {
                ColorSelector(
                    onColorSelected = {
                        coroutineScope.launch {
                            color.animateTo(Color(it), animationSpec = tween(500))
                        }
                        onColorChanged(it)
                    },
                    currentColor = state.color
                )
            }

            TitleField(state.title, onTitleChanged, saveNote, enabled = !isBlockingSavingNote)

            ContentField(state.content, onContentChanged, saveNote, enabled = !isBlockingSavingNote)
        }

        if (isBlockingSavingNote) {
            CircularProgressIndicator(
                modifier = Modifier
                    .height(50.dp)
                    .width(50.dp)
                    .align(Alignment.Center)
            )
        }
    }
}

@Composable
private fun TitleField(
    title: String?,
    onTitleChanged: (String) -> Unit,
    saveNote: () -> Unit,
    enabled: Boolean
) {
    TextFieldWithHint(
        value = title ?: "",
        enabled = enabled,
        textStyle = TextStyle(
            fontSize = MaterialTheme.typography.h4.fontSize,
            fontWeight = MaterialTheme.typography.h4.fontWeight,
            color = Color.Black
        ),
        hint = {
            Text(
                text = "Title",
                fontSize = MaterialTheme.typography.h4.fontSize,
                fontWeight = MaterialTheme.typography.h4.fontWeight,
                color = Color.DarkGray
            )
        },
        onValueChange = onTitleChanged,
        onDone = { saveNote() }
    )
}

@Composable
private fun ContentField(
    content: String?,
    onContentChanged: (String) -> Unit,
    saveNote: () -> Unit,
    enabled: Boolean
) {
    TextFieldWithHint(
        value = content ?: "",
        enabled = enabled,
        textStyle = TextStyle(
            fontSize = MaterialTheme.typography.h6.fontSize,
            fontWeight = MaterialTheme.typography.h6.fontWeight,
            color = Color.Black
        ),
        hint = {
            Text(
                text = "Note",
                fontSize = MaterialTheme.typography.h6.fontSize,
                fontWeight = MaterialTheme.typography.h6.fontWeight,
                color = Color.DarkGray
            )
        },
        onValueChange = onContentChanged,
        onDone = { saveNote() }
    )
}

@Composable
private fun ColorSectionMenuButton(
    isColorSectionVisible: MutableState<Boolean>,
    isBlockingSavingNote: Boolean
) {
    IconButton(onClick = {
        if (!isBlockingSavingNote) {
            isColorSectionVisible.value = !isColorSectionVisible.value
        }
    }) {
        Icon(
            imageVector = Icons.Filled.MoreVert,
            contentDescription = stringResource(R.string.color_section_ally),
            tint = MaterialTheme.colors.onSurface
        )
    }
}

@Composable
private fun DoneMenuButton(
    onDoneClicked: () -> Unit,
    isBlockingSavingNote: Boolean
) {
    TextButton(onClick = {
        if (!isBlockingSavingNote) {
            onDoneClicked()
        }
    }) {
        Text(text = stringResource(R.string.done))
    }
}

@Preview
@Composable
fun previewEditNoteScreen() {
    EditNoteScreen(
        state = EditNoteViewModel.State("", "", NoteColor.Violet.argbValue, emptyList()),
        {},
        {},
        {},
        {},
        {},
        false
    )
}
