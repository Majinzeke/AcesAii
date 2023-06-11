package com.mz.acesaii.presentation.screens.home

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mz.acesaii.data.repository.Entries
import com.mz.acesaii.data.repository.MongoDB
import com.mz.acesaii.model.RequestState
import kotlinx.coroutines.launch

class HomeViewModel: ViewModel() {

    var entries: MutableState<Entries> = mutableStateOf(RequestState.Idle)

    init {
        observeAllEntries()
    }

    private fun observeAllEntries(){
        viewModelScope.launch {
            MongoDB.getAllPokerEntries().collect() { result ->
                entries.value = result
            }
        }
    }
}