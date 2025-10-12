package com.soyvictorherrera.scorecount.ui.matchhistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soyvictorherrera.scorecount.di.DefaultDispatcher
import com.soyvictorherrera.scorecount.domain.model.Match
import com.soyvictorherrera.scorecount.domain.usecase.GetMatchesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MatchHistoryViewModel
    @Inject
    constructor(
        private val getMatchesUseCase: GetMatchesUseCase,
        @DefaultDispatcher private val dispatcher: CoroutineDispatcher
    ) : ViewModel() {
        private val _matches = MutableStateFlow<List<Match>>(emptyList())
        val matches: StateFlow<List<Match>> = _matches

        init {
            viewModelScope.launch(dispatcher) {
                getMatchesUseCase()
                    .catch { _matches.value = emptyList() }
                    .collect { _matches.value = it }
            }
        }
    }
