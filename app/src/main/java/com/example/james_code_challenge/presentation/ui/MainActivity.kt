package com.example.james_code_challenge.presentation.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.james_code_challenge.R
import com.example.james_code_challenge.mock.MockData
import com.example.james_code_challenge.presentation.ui.components.ProceduresScaffold
import com.example.james_code_challenge.presentation.ui.screens.FavouritesScreen
import com.example.james_code_challenge.presentation.ui.screens.ProceduresScreen
import com.example.james_code_challenge.presentation.ui.theme.Jamescode_challengeTheme
import com.example.james_code_challenge.presentation.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Jamescode_challengeTheme {
                ProceduresApp()
            }
        }
    }

    companion object {
        const val PROCEDURE_LIST_TEST_TAG = "procedure_list_test_tag"
        const val FAVOURITES_TEST_TAG = "favourites_test_tag"
        const val PROGRESS_ICON_TEST_TAG = "progress_icon_test_tag"
        const val FAVOURITE_BUTTON_TEST_TAG = "favourite_button_test_tag"
    }
}

@Composable
fun ProceduresApp() {
    val viewModel = hiltViewModel<MainViewModel>()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val favourited = stringResource(R.string.snackbar_favourited)
    val unfavourited = stringResource(R.string.snackbar_unfavourited)

    val proceduresUiState by viewModel.proceduresState.collectAsStateWithLifecycle()

    ProceduresScaffold(
        snackbarHostState = snackbarHostState,
        procedureScreen = {
            ProceduresScreen(
                uiState = proceduresUiState,
                fetchProcedureDetailEvent = { viewModel.fetchProcedureDetail(it) },
                onFavouriteToggleEvent = {
                    viewModel.toggleFavouriteItem(
                        it
                    )

                    scope.launch {
                        val msg = if (viewModel.isFavourite(it.uuid)) unfavourited else favourited
                        snackbarHostState.showSnackbar(
                            message = msg
                        )
                    }
                },
                fetchProceduresAndFavourites = { viewModel.fetchProceduresAndFavourites() },
                isFavourite = { viewModel.isFavourite(it) }
            )
        },
        favouritesScreen = {
            FavouritesScreen(
                uiState = proceduresUiState,
                onFavouriteToggleEvent = {
                    viewModel.toggleFavouriteItem(
                        it
                    )

                    scope.launch {
                        val msg = if (viewModel.isFavourite(it.uuid)) unfavourited else favourited
                        snackbarHostState.showSnackbar(
                            message = msg
                        )
                    }
                },
                fetchFavourites = {
                    viewModel.fetchFavouriteList()
                },
                onFetchProcedureDetailEvent = {
                    viewModel.fetchProcedureDetail(it)
                },
                isFavourite = { viewModel.isFavourite(it) }
            )
        }
    )
}

@Composable
@Preview
fun ProcedureAppPreview() {
    ProceduresScaffold(
        snackbarHostState = SnackbarHostState(),
        procedureScreen = {
            ProceduresScreen(
                uiState = MainViewModel.ProceduresState(
                    items = listOf(
                        MockData.procedureMock,
                        MockData.procedureMock.copy(uuid = "1"),
                        MockData.procedureMock.copy(uuid = "2"),
                        MockData.procedureMock.copy(uuid = "3"),
                        MockData.procedureMock.copy(uuid = "4")
                    )
                ),
                fetchProcedureDetailEvent = {},
                onFavouriteToggleEvent = {},
                fetchProceduresAndFavourites = {},
                isFavourite = { true }
            )
        },
        favouritesScreen = {
            FavouritesScreen(
                uiState = MainViewModel.ProceduresState(
                    items = listOf(
                        MockData.procedureMock,
                        MockData.procedureMock.copy(uuid = "1"),
                        MockData.procedureMock.copy(uuid = "2"),
                        MockData.procedureMock.copy(uuid = "3"),
                        MockData.procedureMock.copy(uuid = "4")
                    )
                ),
                onFavouriteToggleEvent = {},
                fetchFavourites = {},
                onFetchProcedureDetailEvent = {},
                isFavourite = { true }
            )
        }
    )
}