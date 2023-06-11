package com.mz.acesaii.navigation

import android.util.Log
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState
import com.mz.acesaii.data.repository.MongoDB
import com.mz.acesaii.model.GameRecord
import com.mz.acesaii.presentation.components.DisplayAlertDialog
import com.mz.acesaii.presentation.screens.auth.AuthenticationScreen
import com.mz.acesaii.presentation.screens.auth.AuthenticationViewModel
import com.mz.acesaii.presentation.screens.home.HomeScreen
import com.mz.acesaii.presentation.screens.home.HomeViewModel
import com.mz.acesaii.presentation.screens.write.WriteScreen
import com.mz.acesaii.presentation.screens.write.WriteViewModel
import com.mz.acesaii.util.Constants.APP_ID
import com.mz.acesaii.util.Constants.WRITE_SCREEN_ARGUMENT_KEY
import com.stevdzasan.messagebar.rememberMessageBarState
import com.stevdzasan.onetap.rememberOneTapSignInState
import io.realm.kotlin.mongodb.App
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun SetupNavGraph(startDestination: String, navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    )
    {
        authenticationRoute(
            navigateToHome = {
                navController.popBackStack()
                navController.navigate(Screen.Home.route)
            }

        )
        homeRoute(
            navigateToWrite = {
                navController.navigate(Screen.Write.route)
            },
            navigateToAuth = {
                navController.popBackStack()
                navController.navigate(Screen.Authentication.route)
            },
            navigateToWriteWithArgs = {
                navController.navigate(Screen.Write.passPokerId(pokerId = it))
            }
        )
        writeRoute(
            navigateBack = {
                navController.popBackStack()
            }
        )


    }

}


fun NavGraphBuilder.authenticationRoute(navigateToHome: () -> Unit) {
    composable(route = Screen.Authentication.route) {
        val viewModel: AuthenticationViewModel = viewModel()
        val loadingState by viewModel.loadingState
        val oneTapState = rememberOneTapSignInState()
        val authenticated by viewModel.authenticated
        val messageBarState = rememberMessageBarState()

        AuthenticationScreen(
            loadingState = loadingState,
            oneTapState = oneTapState,
            messageBarState = messageBarState,
            onButtonClicked = {
                oneTapState.open()
                viewModel.setLoading(true)
            },
            onTokenIdReceived = { tokenId ->
                viewModel.signInWithMongoAtlas(
                    tokenId = tokenId,
                    onSuccess = {
                        messageBarState.addSuccess("successfully Authed")
                        viewModel.setLoading(false)
                    },
                    onError = {
                        messageBarState.addError(it)
                        viewModel.setLoading(false)
                    }
                )
            },
            onDialogDismissed = { message ->
                messageBarState.addError(Exception(message))
                viewModel.setLoading(false)
            },
            authenticated = authenticated,
            navigateToHome = navigateToHome
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
fun NavGraphBuilder.homeRoute(
    navigateToWrite: () -> Unit,
    navigateToWriteWithArgs: (String) -> Unit,
    navigateToAuth: () -> Unit
) {
    composable(route = Screen.Home.route) {
        val viewModel: HomeViewModel = viewModel()
        val entries by viewModel.entries
        var signOutDialogOpened by remember {
            mutableStateOf(false)
        }
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val scope = rememberCoroutineScope()
        HomeScreen(
            entries = entries,
            drawerState = drawerState,
            onMenuClicked = {
                           scope.launch {
                               drawerState.open()
                               navigateToAuth()
                           }
            },
            onSignedOutClicked = {
                signOutDialogOpened = true },
            navigateToWrite = navigateToWrite,
            navigateToWriteWithArgs = navigateToWriteWithArgs,

        )
        DisplayAlertDialog(
            title = "Sign Out",
            message = "Are you sure?",
            dialogOpened = signOutDialogOpened,
            onDialogClosed = { signOutDialogOpened = false },
            onYesClicked = {
                scope.launch (Dispatchers.IO){
                    val user = App.create(APP_ID).currentUser
                    if (user != null){
                        user.logOut()
                        withContext(Dispatchers.Main){
                            navigateToAuth()
                        }
                    }
                }
            }
        )

        LaunchedEffect(key1 = Unit){
            MongoDB.configureTheRealm()
        }

        DisplayAlertDialog(
            title = "Sign Out",
            message = "Are you sure?",
            dialogOpened = signOutDialogOpened  ,
            onDialogClosed = { signOutDialogOpened = false},
            onYesClicked = {
                scope.launch(Dispatchers.IO) {
                    val user = App.create(APP_ID).currentUser
                    if (user != null){
                        user.logOut()
                        navigateToAuth()
                    }

                }
            }
        )
    }
}

@OptIn(ExperimentalPagerApi::class)
fun NavGraphBuilder.writeRoute(navigateBack: () -> Unit) {
    composable(
        route = Screen.Write.route,
        arguments = listOf(navArgument(name = WRITE_SCREEN_ARGUMENT_KEY) {
            type = NavType.StringType
            nullable = true
            defaultValue = null
        })
    ) {
        val viewModel: WriteViewModel = viewModel()
        val uiState = viewModel.uiState
        val pagerState = rememberPagerState()
        val pageNumber by remember { derivedStateOf { pagerState.currentPage } }

        LaunchedEffect(key1 = uiState){
            Log.d("SelectedPoker", "${uiState.selectedPokerId}")
        }

        WriteScreen(
            uiState = uiState,
            gameRecordImage = {GameRecord.values()[pageNumber].name},
            onDeleteConfirmed = { },
            onBackPressed = navigateBack,
            pagerState = pagerState,
            onTitleChanged = { viewModel.setTitle(title = it) },
            onDescriptionChanged = { viewModel.setDescription(description = it)},
            onSaveClicked = {
                viewModel.insertEntry(
                    poker = it.apply { gameRecord = GameRecord.values()[pageNumber].name },
                    onSuccess = { navigateBack() },
                    onError = {}
                )

            },
            onDateTimeUpdated = { }
        )
    }
}

