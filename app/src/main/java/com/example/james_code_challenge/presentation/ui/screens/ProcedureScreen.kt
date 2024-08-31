package com.example.james_code_challenge.presentation.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.james_code_challenge.R
import com.example.james_code_challenge.data.model.FavouriteItem
import com.example.james_code_challenge.mock.MockData
import com.example.james_code_challenge.presentation.ui.MainActivity.Companion.PROCEDURE_LIST_TEST_TAG
import com.example.james_code_challenge.presentation.ui.MainActivity.Companion.PROGRESS_ICON_TEST_TAG
import com.example.james_code_challenge.presentation.ui.components.PhaseBottomSheet
import com.example.james_code_challenge.presentation.ui.components.ProcedureDetailCard
import com.example.james_code_challenge.presentation.viewmodel.MainViewModel

/***
 * Procedures screen which shows all available procedures, but highlights favourited items
 * @param uiState: Current state of screen - containing procedures, favourited DB items error states, etc
 * @param fetchFavourites: Lambda to query DB for users' favourite procedures
 * @param showBottomSheet: Current bottomSheet status
 * @param onFavouriteToggleEvent: Lambda to insert currently selected procedure into DB as a favourite
 * @param onFetchProcedureDetailEvent: Lambda sends API request to fetch details of currently selected procedure, to load into BottomSheet
 * @param isFavourite: queries if currently selected procedure is part of favourites DB
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProceduresScreen(
    modifier: Modifier = Modifier,
    uiState: MainViewModel.ProceduresState,
    fetchProcedureDetailEvent: (String) -> Unit,
    showBottomSheet: Boolean = false,
    onFavouriteToggleEvent: (FavouriteItem) -> Unit,
    fetchProceduresAndFavourites: () -> Unit,
    isFavourite: (String) -> Boolean
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheetState by remember { mutableStateOf(showBottomSheet) }
    var isFetchingProcedures by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    if (!isFetchingProcedures) { // Without this, fetchProceduresList() is called after every recomposition, causing erratic effects
        LaunchedEffect(Unit) {
            isFetchingProcedures = true
            fetchProceduresAndFavourites()
            isFetchingProcedures = false
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        // Bottomsheet
        if (showBottomSheetState && uiState.selectedProcedureDetail != null) {
            ModalBottomSheet(
                modifier = modifier
                    .fillMaxHeight(1.0f)
                    .verticalScroll(rememberScrollState()),
                onDismissRequest = {
                    showBottomSheetState = false
                },
                sheetState = sheetState
            ) {
                Column(
                    modifier = modifier.padding(vertical = 100.dp)
                ) {
                    val selectedProcedure = uiState.selectedProcedureDetail
                    PhaseBottomSheet(
                        procedureDetail = selectedProcedure,
                        favouritesList = uiState.favouriteItems,
                        onFavouriteToggleEvent = { onFavouriteToggleEvent(it) },
                        isFavourite = { selectedProcedure?.uuid?.let { isFavourite(it) } ?: false }
                    )
                }
            }
        }

        // Actual procedures UI
        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = modifier
                    .align(Alignment.Center)
                    .testTag(PROGRESS_ICON_TEST_TAG)
            )
        } else if (uiState.error != null) {
            println("Failed to getProcedureList -> ${uiState.error}") // Production apps should use Timber
            Text(
                text = stringResource(R.string.generic_error_message),
                modifier = modifier.align(Alignment.Center)
            )
        } else {
            ProceduresList(
                uiState = uiState,
                onShowBottomSheetEvent = { showBottomSheetState = true },
                onFetchProcedureDetailEvent = { fetchProcedureDetailEvent(it) },
                onFavouriteToggleEvent = { onFavouriteToggleEvent(it) },
                isFavourite = { isFavourite(it) }
            )

            // Offline snackbar
            if (uiState.isOffline) {
                val userOffline = stringResource(R.string.user_offline)
                LaunchedEffect(uiState.isOffline) {
                    snackbarHostState.showSnackbar(
                        userOffline
                    )
                }
                SnackbarHost(hostState = snackbarHostState)
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ProceduresList(
    modifier: Modifier = Modifier,
    uiState: MainViewModel.ProceduresState,
    onShowBottomSheetEvent: () -> Unit,
    onFetchProcedureDetailEvent: (String) -> Unit,
    onFavouriteToggleEvent: (FavouriteItem) -> Unit,
    isFavourite: (String) -> Boolean
) {
    val latestFavourites = remember(uiState.favouriteItems) {
        uiState.favouriteItems
    }
    LaunchedEffect(uiState.favouriteItems) {}

    LazyColumn(
        modifier = modifier
            .fillMaxHeight()
            .semantics {
                testTagsAsResourceId = true
            }
            .testTag(PROCEDURE_LIST_TEST_TAG)
    ) {
        items(
            items = uiState.items,
            key = { it.uuid }
        ) { procedure ->
            ProcedureDetailCard(
                procedure = procedure,
                favouritesList = latestFavourites,
                onCardClickEvent = {
                    onShowBottomSheetEvent()
                    onFetchProcedureDetailEvent(procedure.uuid)
                },
                onFavouriteToggleEvent = { onFavouriteToggleEvent(it) },
                isFavourite = { isFavourite(procedure.uuid) }
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun ProcedureListScreenPreview() {
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
}