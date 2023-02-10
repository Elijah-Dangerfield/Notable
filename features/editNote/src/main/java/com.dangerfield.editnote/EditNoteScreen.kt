package com.dangerfield.editnote

import android.annotation.SuppressLint
import androidx.compose.animation.Animatable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector4D
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dangerfield.editnote.EditNoteViewModel.Event.NoteFinishedDeleting
import com.dangerfield.editnote.EditNoteViewModel.Event.NoteFinishedSaving
import com.dangerfield.notable.designsystem.PastelPurple
import kotlinx.coroutines.launch

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun EditNoteRoute(
    viewModel: EditNoteViewModel,
    onDone: () -> Unit
) {
    val state by viewModel.stateStream.collectAsStateWithLifecycle()
    val isLoading = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    fun saveAndExit() {
        viewModel.save()
        coroutineScope.launch {
            isLoading.value = true
            val eventState = viewModel.waitForState {
                it.events.any { event -> event is NoteFinishedSaving }
            }
            isLoading.value = false
            onDone()
            eventState.events.firstOrNull { it is NoteFinishedSaving }?.let {
                viewModel.resolveEvent(it.id)
            }
        }
    }

    fun deleteAndExit() {
        viewModel.delete()
        coroutineScope.launch {
            isLoading.value = true
            val eventState = viewModel.waitForState {
                it.events.any { event -> event == NoteFinishedDeleting }
            }
            isLoading.value = false
            onDone()
            eventState.events.firstOrNull { it is NoteFinishedDeleting }?.let {
                viewModel.resolveEvent(it.id)
            }
        }
    }

    EditNoteScreen(
        state = state,
        onColorChanged = { viewModel.updateColor(it) },
        saveNote = { viewModel.save() },
        onSaveClicked = { saveAndExit() },
        onTitleChanged = viewModel::updateTitle,
        onContentChanged = viewModel::updateContent,
        isBlockingSavingNote = isLoading.value,
        onDeleteClicked = { deleteAndExit() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
private fun EditNoteScreen(
    state: EditNoteViewModel.State,
    onColorChanged: (Int) -> Unit,
    saveNote: () -> Unit,
    onSaveClicked: () -> Unit,
    onTitleChanged: (String) -> Unit,
    onContentChanged: (String) -> Unit,
    isBlockingSavingNote: Boolean,
    onDeleteClicked: () -> Unit
) {

    val isColorSectionVisible = remember { mutableStateOf(false) }
    val color = remember { Animatable(Color(state.color)) }
    val coroutineScope = rememberCoroutineScope()
    val isNoteSavable = state.hasChanged && (!state.title.isNullOrEmpty() || !state.content.isNullOrEmpty())

    Scaffold(
        modifier = Modifier
            .background(color.value)
            .padding(12.dp),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.largeTopAppBarColors(containerColor = color.value),
                title = {},
                navigationIcon = { BackButton(color, onSaveClicked) },
                actions = {
                    TopAppBarActions(
                        isColorSectionVisible,
                        isBlockingSavingNote,
                        isNoteSavable,
                        onSaveClicked,
                        onDeleteClicked
                    )
                }
            )
        },
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color.value)
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

                NoteContent(
                    state,
                    onTitleChanged,
                    saveNote,
                    isBlockingSavingNote,
                    color,
                    isNoteSavable,
                    onContentChanged
                )
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
}

@Composable
private fun NoteContent(
    state: EditNoteViewModel.State,
    onTitleChanged: (String) -> Unit,
    saveNote: () -> Unit,
    isBlockingSavingNote: Boolean,
    color: Animatable<Color, AnimationVector4D>,
    isNoteSavable: Boolean,
    onContentChanged: (String) -> Unit
) {
    TitleField(
        state.title,
        onTitleChanged,
        saveNote,
        enabled = !isBlockingSavingNote,
        color.value
    )

    if (!isNoteSavable && state.updatedAt != null) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "${stringResource(id = R.string.last_updated)}: ${state.updatedAt}",
            style = MaterialTheme.typography.titleSmall,
            color = Color(
                ColorUtils.blendARGB(
                    color.value.toArgb(), Color.Black.toArgb(),
                    0.5F
                )
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
    }

    ContentField(
        state.content,
        onContentChanged,
        saveNote,
        enabled = !isBlockingSavingNote,
        color.value
    )
}

@Composable
private fun RowScope.TopAppBarActions(
    isColorSectionVisible: MutableState<Boolean>,
    isBlockingSavingNote: Boolean,
    isNoteSavable: Boolean,
    onSaveClicked: () -> Unit,
    onDeleteClicked: () -> Unit
) {
    ColorSectionMenuButton(isColorSectionVisible, isBlockingSavingNote)
    AnimatedVisibility(visible = isNoteSavable) {
        SaveButton(
            onDoneClicked = { onSaveClicked() },
            isBlockingSavingNote
        )
    }
    IconButton(
        onClick = { onDeleteClicked() },
    ) {
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = stringResource(R.string.delete_note),
            tint = Color.Black
        )
    }
}

@Composable
private fun BackButton(
    color: Animatable<Color, AnimationVector4D>,
    onSaveClicked: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color(ColorUtils.blendARGB(color.value.toArgb(), Color.Black.toArgb(), .5f)))
            .clickable { onSaveClicked() }
            .padding(5.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "navigate back to notes",
            tint = Color.White
        )
    }
}

@Composable
private fun TitleField(
    title: String?,
    onTitleChanged: (String) -> Unit,
    saveNote: () -> Unit,
    enabled: Boolean,
    noteColor: Color
) {
    TextFieldWithHint(
        value = title ?: "",
        enabled = enabled,
        textStyle = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
        hint = {
            Text(
                text = "Title",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                color = Color(ColorUtils.blendARGB(noteColor.toArgb(), Color.Black.toArgb(), .5f)),
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
    enabled: Boolean,
    noteColor: Color
) {
    TextFieldWithHint(
        value = content ?: "",
        enabled = enabled,
        textStyle = MaterialTheme.typography.titleMedium,
        hint = {
            Text(
                text = stringResource(R.string.type_something),
                style = MaterialTheme.typography.titleMedium,
                color = Color(ColorUtils.blendARGB(noteColor.toArgb(), Color.Black.toArgb(), .5f))
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
            tint = Color.Black
        )
    }
}

@Composable
private fun SaveButton(
    onDoneClicked: () -> Unit,
    isCurrentlySaving: Boolean,
) {
    TextButton(onClick = {
        if (!isCurrentlySaving) {
            onDoneClicked()
        }
    }) {
        Text(
            text = stringResource(id = R.string.save),
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Black
        )
    }
}

@Preview
@Composable
fun previewEditNoteScreen() {
    EditNoteScreen(
        state = EditNoteViewModel.State(
            "",
            "",
            PastelPurple.toArgb(),
            "Wed, July 4th, 2022 at 12:34PM GTM",
            emptyList(),
            false
        ),
        {},
        {},
        {},
        {},
        {},
        false,
        {}
    )
}
