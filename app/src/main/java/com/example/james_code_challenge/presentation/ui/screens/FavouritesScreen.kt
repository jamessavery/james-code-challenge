package com.example.james_code_challenge.presentation.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.james_code_challenge.R
import com.example.james_code_challenge.data.model.FavouriteItem
import com.example.james_code_challenge.mock.MockData
import com.example.james_code_challenge.presentation.ui.MainActivity.Companion.FAVOURITES_TEST_TAG
import com.example.james_code_challenge.presentation.ui.components.PhaseBottomSheet
import com.example.james_code_challenge.presentation.ui.components.ProcedureDetailCard
import com.example.james_code_challenge.presentation.viewmodel.MainViewModel

/***
 * Favourites screen which shows just the users' favourited procedures
 * TODO explain all params
 *
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavouritesScreen(
    modifier: Modifier = Modifier,
    uiState: MainViewModel.ProceduresState,
    fetchFavourites: () -> Unit,
    showBottomSheet: Boolean = false,
    onFavouriteToggleEvent: (FavouriteItem) -> Unit,
    onFetchProcedureDetailEvent: (String) -> Unit,
    isFavourite: (String) -> Boolean
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheetState by remember { mutableStateOf(showBottomSheet) }
    var isFetchingFavourites by remember { mutableStateOf(false) }

    if (!isFetchingFavourites) {
        LaunchedEffect(Unit) {
            isFetchingFavourites = true
            fetchFavourites() // Fetch latest favourites every time land on favourites screen
            isFetchingFavourites = false
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        // Bottomsheet
        if (showBottomSheetState && uiState.selectedProcedureDetail != null) {
            ModalBottomSheet(
                modifier = modifier.fillMaxHeight(1.0f),
                onDismissRequest = {
                    showBottomSheetState = false
                },
                sheetState = sheetState
            ) {
                Column(
                    modifier = modifier.padding(vertical = 100.dp)
                ) {
                    PhaseBottomSheet(
                        procedureDetail = uiState.selectedProcedureDetail,
                        favouritesList = uiState.items,
                        onFavouriteToggleEvent = { onFavouriteToggleEvent(it) },
                        isFavourite = { isFavourite(it) }
                    )
                }
            }
        }

        // Actual favourites UI
        if (uiState.items.isEmpty()) {
            Text(
                text = stringResource(R.string.favourites_empty_text),
                modifier = modifier.align(Alignment.Center)
            )
        } else {
            FavouritesList(
                uiState = uiState,
                onShowBottomSheetEvent = { showBottomSheetState = true },
                onFetchProcedureDetailEvent = { onFetchProcedureDetailEvent(it) },
                onFavouriteToggleEvent = { onFavouriteToggleEvent(it) },
                isFavourite = { isFavourite(it) }
            )
        }
    }
}

@Composable
fun FavouritesList(
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
            .testTag(FAVOURITES_TEST_TAG)
    ) {
        items(
            items = latestFavourites,
            key = { it.uuid }
        ) { procedure ->
            ProcedureDetailCard(
                procedure = procedure,
                favouritesList = latestFavourites,
                onCardClickEvent = {
                    onShowBottomSheetEvent()
                    onFetchProcedureDetailEvent(procedure.uuid)
                },
                onFavouriteToggleEvent = {
                    onFavouriteToggleEvent(it)
                },
                isFavourite = { isFavourite(it) }
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun FavouritesScreenPreview() {
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
        fetchFavourites = {},
        onFavouriteToggleEvent = {},
        onFetchProcedureDetailEvent = {},
        isFavourite = { true }
    )
}