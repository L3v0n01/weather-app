package com.la.weather.core.common.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.la.weather.core.common.util.Logger
import com.la.weather.core.model.Resource
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class BaseViewModel<S : UiState, E : UiEvent, EF : UiEffect> : ViewModel() {

    abstract fun initialState(): S

    abstract fun handleEvent(event: E)

    private val _uiState: MutableStateFlow<S> = MutableStateFlow(initialState())
    val uiState: StateFlow<S> = _uiState.asStateFlow()

    private val _effects: Channel<EF> = Channel(Channel.BUFFERED)
    val effects: Flow<EF> = _effects.receiveAsFlow()

    protected fun <T> execute(
        block: suspend () -> Resource<T>,
        onSuccess: suspend (T) -> Unit,
        onError: (message: String, throwable: Throwable?) -> Unit = { msg, t ->
            Logger.e(this::class.simpleName.orEmpty(), msg, t)
        },
    ) {
        viewModelScope.launch {
            val result = runCatching { block() }.getOrElse { e ->
                Resource.Error(e.message.orEmpty(), e)
            }

            when (result) {
                is Resource.Success -> onSuccess(result.data)
                is Resource.Error -> onError(result.message, result.throwable)
            }
        }
    }

    protected fun updateState(reducer: S.() -> S) {
        _uiState.update { state -> state.reducer() }
    }

    protected suspend fun sendEffect(effect: EF) {
        _effects.send(effect)
    }
}
