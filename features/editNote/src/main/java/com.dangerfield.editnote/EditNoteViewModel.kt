package com.dangerfield.editnote

import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.SavedStateHandle
import com.dangerfield.core.notesapi.Note
import com.dangerfield.core.notesapi.NoteRepository
import com.dangerfield.core.notesapi.getReadableUpdatedAtDate
import com.dangerfield.core.ui.UdfViewModel
import com.dangerfield.notable.designsystem.NoteColors
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class EditNoteViewModel @Inject constructor(
    private val noteRepository: NoteRepository,
    savedStateHandle: SavedStateHandle
) : UdfViewModel<EditNoteViewModel.State, EditNoteViewModel.Action>() {

    private var noteId = checkNotNull(savedStateHandle.get<String>("noteId"))

    override val initialAction: Action = Action.LoadNote

    override val initialState = if (noteId != NewNoteId) {
        val noteColor = checkNotNull(savedStateHandle.get<Int>("noteColor"))
        State("", "", noteColor, null, emptyList(), false)
    } else {
        State(
            title = null,
            content = null,
            color = NoteColors.random().toArgb(),
            updatedAt = null,
            events = emptyList(),
            hasChanged = false
        )
    }

    override fun transformActionFlow(actionFlow: Flow<Action>): Flow<State> {
        return actionFlow.flatMapMerge {
            flow {
                when (it) {
                    is Action.EditColor -> handleEditColor(it.color)
                    is Action.EditContent -> handleEditContent(it.content)
                    is Action.EditTitle -> handleEditTitle(it.title)
                    is Action.LoadNote -> handleLoadNote()
                    is Action.SaveNote -> handleSaveNote()
                    is Action.ResolveEvent -> handleResolveEvent(it.id)
                    is Action.DeleteNote -> handleDeleteNote()
                }
            }
        }
    }

    fun updateColor(color: Int) = submitAction(Action.EditColor(color))

    fun updateTitle(title: String) {
        submitAction(Action.EditTitle(title))
    }

    fun updateContent(content: String) {
        submitAction(Action.EditContent(content))
    }

    fun save() = submitAction(Action.SaveNote)

    fun delete() = submitAction(Action.DeleteNote)

    fun resolveEvent(id: String) = submitAction(Action.ResolveEvent(id))

    private suspend fun FlowCollector<State>.handleDeleteNote() {
        val isNewUnsavedNote = noteId == NewNoteId
        if (!isNewUnsavedNote) {
            noteRepository.deleteNote(noteId)
        }
        emit(state.copy(events = state.events + listOf(Event.NoteFinishedDeleting)))
    }

    private suspend fun FlowCollector<State>.handleSaveNote() {
        val title = state.title
        val content = state.content
        val color = state.color

        if (!state.hasChanged) {
            emit(state.copy(events = state.events + listOf(Event.NoteFinishedSaving(true))))
        }

        if (title.isNullOrEmpty() && content.isNullOrEmpty()) {
            emit(state.copy(events = state.events + listOf(Event.NoteFinishedSaving(false))))
            return
        }

        val isNewUnsavedNote = noteId == NewNoteId
        val note = if (isNewUnsavedNote) {
            Note(
                title = title ?: "",
                content = content ?: "",
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis(),
                color = color,
                id = UUID.randomUUID().toString().also { noteId = it }
            )
        } else {
            val savedNote = noteRepository.getNote(noteId)
            savedNote?.copy(
                title = title ?: savedNote.title,
                content = content ?: savedNote.content,
                updatedAt = System.currentTimeMillis(),
                color = color,
            )
        } ?: run {
            emit(state.copy(events = state.events + listOf(Event.NoteFinishedSaving(false))))
            return
        }

        if (isNewUnsavedNote) noteRepository.createNote(note) else noteRepository.updateNote(note)
        emit(state.copy(events = state.events + listOf(Event.NoteFinishedSaving(true))))
    }

    private suspend fun FlowCollector<State>.handleLoadNote() {
        noteRepository.getNote(checkNotNull(noteId))?.let {
            emit(
                state.copy(
                    title = it.title,
                    content = it.content,
                    color = it.color,
                    updatedAt = it.getReadableUpdatedAtDate()
                )
            )
        }
    }

    private suspend fun FlowCollector<State>.handleEditTitle(title: String) =
        emit(state.copy(title = title, hasChanged = true))

    private suspend fun FlowCollector<State>.handleEditContent(content: String) =
        emit(state.copy(content = content, hasChanged = true))

    private suspend fun FlowCollector<State>.handleEditColor(color: Int) =
        emit(state.copy(color = color, hasChanged = true))

    private suspend fun FlowCollector<State>.handleResolveEvent(id: String) {
        emit(state.copy(events = state.events.filterNot { it.id == id }))
    }

    sealed class Action {
        object LoadNote : Action()
        class EditTitle(val title: String) : Action()
        class EditContent(val content: String) : Action()
        class EditColor(val color: Int) : Action()
        object SaveNote : Action()
        object DeleteNote : Action()
        class ResolveEvent(val id: String) : Action()
    }

    sealed class Event(val id: String = UUID.randomUUID().toString()) {
        class NoteFinishedSaving(val didSave: Boolean) : Event()
        object NoteFinishedDeleting : Event()
    }

    data class State(
        val title: String?,
        val content: String?,
        val color: Int,
        val updatedAt: String?,
        val events: List<Event>,
        val hasChanged: Boolean
    )
}
