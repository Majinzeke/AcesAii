package com.mz.acesaii.presentation.screens.write

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import com.mz.acesaii.model.GameRecord
import com.mz.acesaii.model.Poker
import java.time.ZonedDateTime

@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class)
@Composable
fun WriteScreen(
    uiState: UiState,
    gameRecordImage: () -> String,
    pagerState: PagerState,
    onTitleChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onDeleteConfirmed: () -> Unit,
    onDateTimeUpdated: (ZonedDateTime) -> Unit,
    onBackPressed: () -> Unit,
    onSaveClicked: (Poker) -> Unit
    ) {

    LaunchedEffect(key1 = uiState.gameRecord){
        pagerState.scrollToPage(GameRecord.valueOf(uiState.gameRecord.name).ordinal)
    }



    Scaffold(
        topBar = {
            WriteTopBar(
                selectedPokerEntry = uiState.selectedPoker,
                gameRecordImage = gameRecordImage,
                onDeleteConfirmed = onDeleteConfirmed,
                onBackPressed = onBackPressed,
                )
        },
        content = {
            WriteContent(
                uiState = uiState,
                paddingValues = it ,
                pagerState = pagerState,
                title = uiState.title ,
                description = uiState.description ,
                onTitleChanged = onTitleChanged ,
                onDescriptionChanged =  onDescriptionChanged,
                onSaveClicked = onSaveClicked
            )
        }
    )
}