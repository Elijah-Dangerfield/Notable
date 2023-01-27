package com.dangerfield.editnote

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dangerfield.core.notesapi.NoteColor

@Composable
fun EditNoteRoute(
    viewModel: EditNoteViewModel,
    onDone: () -> Unit
) {
    val state = viewModel.stateStream.collectAsState()

    EditNoteScreen(
        state = state.value,
        onColorChanged = { viewModel.updateColor(it) },
        saveNote = { viewModel.save() },
        onDoneClicked = onDone,
        onTitleChanged = { viewModel.updateTitle(it) },
        onContentChanged = { viewModel.updateContent(it) }
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
    onContentChanged: (String) -> Unit
) {

    val isColorSectionVisible = remember { mutableStateOf(false) }
    val isDoneSectionVisible = remember { mutableStateOf(false) }
    val noteBackGround = animateIntAsState(targetValue = state.color)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(noteBackGround.value))
            .padding(12.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            ColorSectionMenuButton(isColorSectionVisible)
            AnimatedVisibility(visible = isDoneSectionVisible.value) {
                DoneMenuButton(onDoneClicked = {
                    saveNote()
                    onDoneClicked()
                })
            }
        }

        AnimatedVisibility(
            visible = isColorSectionVisible.value,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut() + slideOutVertically()

        ) {
            ColorSelector(
                modifier = Modifier.fillMaxWidth(),
                selectedColor = state.color,
                onColorSelected = onColorChanged,
                colors = NoteColor.values().map { it.argbValue }.toList()
            )
        }

        isDoneSectionVisible.value = !state.title.isNullOrEmpty() || !state.content.isNullOrEmpty()

        TitleField(state.title, onTitleChanged, saveNote)

        ContentField(state.content, onContentChanged, saveNote)
    }
}

@Composable
private fun TitleField(
    title: String?,
    onTitleChanged: (String) -> Unit,
    saveNote: () -> Unit
) {
    TextFieldWithHint(
        value = title ?: "",
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
    saveNote: () -> Unit
) {
    TextFieldWithHint(
        value = content ?: "",
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
private fun ColorSectionMenuButton(isColorSectionVisible: MutableState<Boolean>) {
    IconButton(onClick = {
        isColorSectionVisible.value = !isColorSectionVisible.value
    }) {
        Icon(
            imageVector = Icons.Filled.MoreVert,
            contentDescription = stringResource(R.string.color_section_ally),
            tint = MaterialTheme.colors.onSurface
        )
    }
}

@Composable
private fun DoneMenuButton(onDoneClicked: () -> Unit) {
    TextButton(onClick = {
        onDoneClicked()
    }) {
        Text(text = stringResource(R.string.done))
    }
}

@Preview
@Composable
fun previewEditNoteScreen() {
    EditNoteScreen(
        state = EditNoteViewModel.State("", "", NoteColor.Violet.argbValue),
        {},
        {},
        {},
        {},
        {}
    )
}
