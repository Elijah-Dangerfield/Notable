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

    private var note: Note? = null
    private val noteId = savedStateHandle.get<String>("noteId")
    private val noteColor = savedStateHandle.get<Int>("noteColor")

    override val initialState = if (noteId != NEW_NOTE_ID && noteColor != null) {
        Log.d("Elijah", "")
        // TODO this will cause a UI flash of the hints. I should be able to let the view know we are loading
        State("", "", noteColor)
    } else {
        State(null, null, NoteColor.values().random().argbValue)
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
                }
            }
        }
    }

    fun updateColor(color: Int) = submitAction(Action.EditColor(color))

    fun updateTitle(title: String) = submitAction(Action.EditTitle(title))

    fun updateContent(content: String) = submitAction(Action.EditContent(content))

    fun save() = submitAction(Action.SaveNote)

    private suspend fun handleSaveNote() {
        val title = state.title
        val content = state.content
        val color = state.color

        if (title.isNullOrEmpty() && content.isNullOrEmpty()) return

        val isNewUnsavedNote = noteId == NEW_NOTE_ID && note == null
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
            note ?: noteRepository.getNote(checkNotNull(noteId))
        } ?: return // TODO create error

        Log.d("Elijah", "Saving note: $note")

        if (isNewUnsavedNote) noteRepository.createNote(note) else noteRepository.updateNote(note)
        this.note = note
    }

    private suspend fun FlowCollector<State>.handleLoadNote() = noteRepository.getNote(checkNotNull(noteId))?.let {
        note = it
        emit(State(it.title, it.content, it.color))
    }

    private suspend fun FlowCollector<State>.handleEditTitle(title: String) = emit(state.copy(title = title))

    private suspend fun FlowCollector<State>.handleEditContent(content: String) = emit(state.copy(content = content))

    private suspend fun FlowCollector<State>.handleEditColor(color: Int) = emit(state.copy(color = color))

    sealed class Action {
        object LoadNote : Action()
        class EditTitle(val title: String) : Action()
        class EditContent(val content: String) : Action()
        class EditColor(val color: Int) : Action()
        object SaveNote : Action()
    }

    data class State(
        val title: String?,
        val content: String?,
        val color: Int
    )
}
