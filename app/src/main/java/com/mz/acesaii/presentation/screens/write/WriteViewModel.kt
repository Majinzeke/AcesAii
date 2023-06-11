package com.mz.acesaii.presentation.screens.write

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mz.acesaii.data.repository.MongoDB
import com.mz.acesaii.model.GameRecord
import com.mz.acesaii.model.Poker
import com.mz.acesaii.model.RequestState
import com.mz.acesaii.util.Constants.WRITE_SCREEN_ARGUMENT_KEY
import org.mongodb.kbson.ObjectId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WriteViewModel(
    private val savedStateHandle: SavedStateHandle
): ViewModel() {

    var uiState by mutableStateOf(UiState())
        private set

    init {
        getPokerIdArgument()
        fetchSelectedEntry()
    }

    private fun getPokerIdArgument(){

        uiState = uiState.copy(
            selectedPokerId = savedStateHandle.get<String>(
                key = WRITE_SCREEN_ARGUMENT_KEY
            )
        )
    }

    private fun fetchSelectedEntry(){
        if (uiState.selectedPokerId != null){
            viewModelScope.launch(Dispatchers.Main){
                val entry = MongoDB.getSelectedPokerEntry(pokerId = ObjectId.invoke(uiState.selectedPokerId!!))
                if (entry is RequestState.Success){
                    setSelectedEntry(poker = entry.data)
                    setTitle(title = entry.data.title)
                    setDescription(description = entry.data.description)
                    setGameRecord(gameRecord = GameRecord.valueOf(entry.data.gameRecord))

                }
            }
        }
    }

    private fun setSelectedEntry(poker: Poker){
        uiState = uiState.copy(selectedPoker = poker)
    }


    fun setTitle(title: String){
        uiState = uiState.copy(title = title)
    }

    fun setDescription(description: String){
        uiState = uiState.copy(description = description)
    }

    private fun setGameRecord(gameRecord: GameRecord){
        uiState = uiState.copy(gameRecord = gameRecord)
    }

    fun insertEntry(
        poker: Poker,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ){
        viewModelScope.launch(Dispatchers.IO){
            val result = MongoDB.addNewPokerEntry(poker = poker)
            if (result is RequestState.Success){
                withContext(Dispatchers.Main){
                    onSuccess()
                }
            }else if (result is RequestState.Error){
                withContext(Dispatchers.Main){
                    onError(result.error.message.toString())
                }
            }
        }
    }

}

data class UiState(
    val selectedPokerId: String? = null,
    val selectedPoker: Poker? = null,
    val title: String = "",
    val description: String = "",
    val gameRecord: GameRecord = GameRecord.StartUpDialog
)