package com.dangerfield.noteslist

import android.util.Log
import com.dangerfield.core.notesapi.Note
import com.dangerfield.core.notesapi.NoteRepository
import com.dangerfield.core.ui.UdfViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class NotesListViewModel @Inject constructor(
    private val noteRepository: NoteRepository
) : UdfViewModel<NotesListViewModel.State, NotesListViewModel.Action>() {

    private var lastDeletedNote: Note? = null

    override val initialState: State = State(emptyList(), NoteOrder.LastEdited, emptyList(), true)

    override var initialAction: Action = Action.Load

    override fun transformActionFlow(actionFlow: Flow<Action>): Flow<State> {
        return actionFlow.flatMapMerge {
            Log.d("Elijah", "Got action: $it")
            flow {
                when (it) {
                    Action.Load -> handleLoadNotes().also { emit(state.copy(isLoading = false)) }
                    is Action.Sort -> handleUpdateNoteOrder(it.orderBy)
                    is Action.RemoveMessage -> handleMessageUpdate(it.id)
                    is Action.DeleteNote -> handleDeleteNote(it.note)
                    is Action.RestoreLastDeletedNote -> handleRestoreNote()
                }
            }
        }
    }

    private suspend fun handleRestoreNote() {
        lastDeletedNote?.let {
            noteRepository.createNote(it)
            lastDeletedNote = null
        }
    }

    private suspend fun handleDeleteNote(note: Note) {
        noteRepository.deleteNote(note.id)
        lastDeletedNote = note
    }

    private suspend fun FlowCollector<State>.handleMessageUpdate(id: String) {
        val currentMessages = stateStream.value.messages.toMutableList()
        currentMessages.removeAll { it.id == id }
        emit(stateStream.value.copy(messages = currentMessages))
    }

    fun updateSortOrder(order: NoteOrder) = submitAction(Action.Sort(order))

    fun deleteNote(note: Note) = submitAction(Action.DeleteNote(note))

    fun restoreLastDeletedNote() = submitAction(Action.RestoreLastDeletedNote)

    fun messageReceived(id: String) = submitAction(Action.RemoveMessage(id))

    private suspend fun FlowCollector<State>.handleLoadNotes() {
        emitAll(
            noteRepository
                .getNotesStream()
                .map {
                    Log.d("Elijah", "Notes size found? ${it.size}")
                    val sortedNotes = getSortedNotes(stateStream.value.noteOrder, it)
                    stateStream.value.copy(notes = sortedNotes)
                }
                .catch {
                    Log.d("Elijah", "Error fetching notes ${it.message}")
                    val currentMessages = stateStream.value.messages.toMutableList()
                    currentMessages.add(NotesListMessage.NoteLoadingError)
                    emit(stateStream.value.copy(messages = currentMessages))
                }
        )
    }

    private suspend fun FlowCollector<State>.handleUpdateNoteOrder(order: NoteOrder) {
        val state = stateStream.value
        emit(state.copy(noteOrder = order, notes = getSortedNotes(order, state.notes)))
    }

    private fun getSortedNotes(order: NoteOrder, notes: List<Note>) = when (order) {
        NoteOrder.LastEdited -> notes.sortedByDescending { it.updatedAt }
        NoteOrder.FirstCreated -> notes.sortedByDescending { it.createdAt }
        NoteOrder.Color -> notes.sortedBy { it.color }
        NoteOrder.Alphabetic -> notes.sortedBy { it.title }
    }

    sealed class Action {
        object Load : Action()
        class RemoveMessage(val id: String) : Action()
        class DeleteNote(val note: Note) : Action()
        object RestoreLastDeletedNote : Action()
        class Sort(val orderBy: NoteOrder) : Action()
    }

    enum class NoteOrder { LastEdited, FirstCreated, Color, Alphabetic }

    data class State(
        val notes: List<Note>,
        val noteOrder: NoteOrder,
        val messages: List<NotesListMessage>,
        val isLoading: Boolean
    )

    sealed class NotesListMessage(val id: String) {
        object NoteLoadingError : NotesListMessage(UUID.randomUUID().toString())
        data class RecoverLastDeletedNote(val note: Note) : NotesListMessage(UUID.randomUUID().toString())
    }
}
