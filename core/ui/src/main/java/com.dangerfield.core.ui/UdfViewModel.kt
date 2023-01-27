package com.dangerfield.core.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

const val STATE_STREAM_NO_SUB_TIMEOUT = 5_000L

abstract class UdfViewModel<STATE, ACTION> : ViewModel() {

    private val actionStream = MutableSharedFlow<ACTION>()

    protected abstract val initialState: STATE

    open val initialAction: ACTION? = null

    protected fun submitAction(action: ACTION) = viewModelScope.launch(Dispatchers.Main) {
        actionStream.emit(action)
    }

    val state: STATE
        get() = stateStream.value

    val stateStream by lazy {
        transformActionFlow(actionStream)
            .catch { Log.d("Elijah", "state stream emitted an error ${it.message}") }
            .onCompletion { Log.d("Elijah", "state stream unexpectedly completed") }
            .onStart { initialAction?.let { submitAction(it) } }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(STATE_STREAM_NO_SUB_TIMEOUT),
                initialState
            )
    }

    protected abstract fun transformActionFlow(actionFlow: Flow<ACTION>): Flow<STATE>
}
