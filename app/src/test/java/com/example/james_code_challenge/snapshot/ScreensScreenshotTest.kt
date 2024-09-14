package com.example.james_code_challenge.snapshot

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import coil.Coil
import coil.ImageLoader
import coil.annotation.ExperimentalCoilApi
import coil.test.FakeImageLoaderEngine
import com.android.ide.common.rendering.api.SessionParams
import com.example.james_code_challenge.MainCoroutineRule
import com.example.james_code_challenge.mock.MockData
import com.example.james_code_challenge.presentation.ui.screens.FavouritesScreen
import com.example.james_code_challenge.presentation.ui.screens.ProceduresScreen
import com.example.james_code_challenge.presentation.viewmodel.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ScreensScreenshotTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_3A,
        renderingMode = SessionParams.RenderingMode.NORMAL,
        showSystemUi = false,
        maxPercentDifference = 0.1,
    )

    @Before
    @OptIn(ExperimentalCoilApi::class, ExperimentalCoroutinesApi::class)
    fun setup() {
        Dispatchers.setMain(Dispatchers.Unconfined)

        val engine = FakeImageLoaderEngine.Builder()
            .default(ColorDrawable(Color.BLUE))
            .build()

        val imageLoader = ImageLoader.Builder(paparazzi.context)
            .components { add(engine) }
            .build()

        Coil.setImageLoader(imageLoader)
    }

    @After
    fun after() {
        Coil.reset()
    }

    @Test
    fun test_proceduresScreen_happyPath() {
        paparazzi.snapshot {
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
    }

    @Test
    fun test_proceduresScreen_emptyProcedures() {
        paparazzi.snapshot {
            ProceduresScreen(
                uiState = MainViewModel.ProceduresState(
                    items = emptyList()
                ),
                fetchProcedureDetailEvent = {},
                onFavouriteToggleEvent = {},
                fetchProceduresAndFavourites = {},
                isFavourite = { true }
            )
        }
    }

    @Test
    fun test_proceduresScreen_error() {
        paparazzi.snapshot {
            ProceduresScreen(
                uiState = MainViewModel.ProceduresState(
                    items = emptyList(),
                    error = "Error"
                ),
                fetchProcedureDetailEvent = {},
                onFavouriteToggleEvent = {},
                fetchProceduresAndFavourites = {},
                isFavourite = { true }
            )
        }
    }

    @Test
    fun test_favouritesScreen_happyPath() {
        paparazzi.snapshot {
            FavouritesScreen(
                uiState = MainViewModel.ProceduresState(
                    items = listOf(
                        MockData.procedureMock,
                        MockData.procedureMock.copy(uuid = "1"),
                        MockData.procedureMock.copy(uuid = "2"),
                        MockData.procedureMock.copy(uuid = "3"),
                        MockData.procedureMock.copy(uuid = "4")
                    ),
                    favouriteItems = listOf(
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
    }

    @Test
    fun test_favouritesScreen_emptyProcedures() {
        paparazzi.snapshot {
            FavouritesScreen(
                uiState = MainViewModel.ProceduresState(
                    items = emptyList()
                ),
                onFavouriteToggleEvent = {},
                fetchFavourites = {},
                onFetchProcedureDetailEvent = {},
                isFavourite = { true }
            )
        }
    }

    @Test
    fun test_favouritesScreen_error() {
        paparazzi.snapshot {
            FavouritesScreen(
                uiState = MainViewModel.ProceduresState(
                    items = emptyList(),
                    error = "error"
                ),
                onFavouriteToggleEvent = {},
                fetchFavourites = {},
                onFetchProcedureDetailEvent = {},
                isFavourite = { true }
            )
        }
    }

}

