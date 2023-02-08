package com.dangerfield.editnote

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import com.dangerfield.core.notesapi.Note
import com.dangerfield.core.notesapi.NoteColor
import com.dangerfield.core.notesapi.NoteRepository
import com.dangerfield.core.ui.UdfViewModel
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

    private var noteId = savedStateHandle.get<String>("noteId")

    override val initialAction: Action = Action.LoadNote

    override val initialState = if (noteId != NEW_NOTE_ID) {
        val noteColor = checkNotNull(savedStateHandle.get<Int>("noteColor"))
        State("", "", noteColor, emptyList())
    } else {
        State(null, null, NoteColor.values().random().argbValue, emptyList())
    }

    override fun transformActionFlow(actionFlow: Flow<Action>): Flow<State> {
        return actionFlow.flatMapMerge {
            Log.d("EditNote", "Received the action : $it")
            flow {
                when (it) {
                    is Action.EditColor -> handleEditColor(it.color)
                    is Action.EditContent -> handleEditContent(it.content)
                    is Action.EditTitle -> handleEditTitle(it.title)
                    is Action.LoadNote -> handleLoadNote()
                    is Action.SaveNote -> handleSaveNote()
                    is Action.ResolveEvent -> handleResolveEvent(it.id)
                }
            }
        }
    }

    fun updateColor(color: Int) = submitAction(Action.EditColor(color))

    fun updateTitle(title: String) = submitAction(Action.EditTitle(title))

    fun updateContent(content: String) = submitAction(Action.EditContent(content))

    fun save() = submitAction(Action.SaveNote)

    suspend fun waitForNoteFinishedSaving() = waitForState {
        it.events.contains(Event.NoteFinishedSaving)
    }.events.find { it is Event.NoteFinishedSaving }?.id

    fun resolveEvent(id: String) = submitAction(Action.ResolveEvent(id))

    private suspend fun FlowCollector<State>.handleSaveNote() {
        val title = state.title
        val content = state.content
        val color = state.color

        if (title.isNullOrEmpty() && content.isNullOrEmpty()) return // TODO produce error

        val isNewUnsavedNote = noteId == NEW_NOTE_ID
        val note = if (isNewUnsavedNote) {
            Log.d("Elijah", "Note was marked as a new unsaved note. This should only log ONCE")
            Note(
                title = title ?: "",
                content = content ?: "",
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis(),
                color = color,
                id = UUID.randomUUID().toString()
            )
        } else {
            val savedNote = noteRepository.getNote(checkNotNull(noteId))
            savedNote?.copy(
                title = title ?: savedNote.title,
                content = content ?: savedNote.content,
                updatedAt = System.currentTimeMillis(),
                color = color,
            )
        } ?: return // TODO create error

        Log.d("Elijah", "Saving note: $note")

        if (isNewUnsavedNote) noteRepository.createNote(note) else noteRepository.updateNote(note)
        emit(state.copy(events = state.events + listOf(Event.NoteFinishedSaving)))
    }

    private suspend fun FlowCollector<State>.handleLoadNote() {
        Log.d("EditNote", "Load note with the id: \"$noteId\"")

        noteRepository.getNote(checkNotNull(noteId))?.let {
            Log.d("EditNote", "got the note from the repository, emitting state and setting local note")
            emit(state.copy(title = it.title, content = it.content, color = it.color))
        } ?: Log.d("EditNote", "The note returned null from the repository")
    }

    private suspend fun FlowCollector<State>.handleEditTitle(title: String) = emit(state.copy(title = title))

    private suspend fun FlowCollector<State>.handleEditContent(content: String) = emit(state.copy(content = content))

    private suspend fun FlowCollector<State>.handleEditColor(color: Int) = emit(state.copy(color = color))

    private suspend fun FlowCollector<State>.handleResolveEvent(id: String) {
        emit(state.copy(events = state.events.filterNot { it.id == id }))
    }

    sealed class Action {
        object LoadNote : Action()
        class EditTitle(val title: String) : Action()
        class EditContent(val content: String) : Action()
        class EditColor(val color: Int) : Action()
        object SaveNote : Action()
        class ResolveEvent(val id: String) : Action()
    }

    sealed class Event(val id: String = UUID.randomUUID().toString()) {
        object NoteFinishedSaving : Event()
    }

    data class State(
        val title: String?,
        val content: String?,
        val color: Int,
        val events: List<Event>
    )
}
