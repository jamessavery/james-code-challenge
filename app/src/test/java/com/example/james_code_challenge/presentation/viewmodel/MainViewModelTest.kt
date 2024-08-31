package com.example.james_code_challenge.presentation.viewmodel

import android.content.Context
import com.example.james_code_challenge.domain.usecase.FavouritesUsecase
import com.example.james_code_challenge.domain.usecase.ProcedureUsecase
import com.example.james_code_challenge.mock.MockData
import com.example.james_code_challenge.util.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class MainViewModelTest {

    private val applicationContextMock = mockk<Context>(relaxed = true)
    private val procedureUsecaseMock = mockk<ProcedureUsecase>(relaxed = true)
    private val favouritesUsecaseMock = mockk<FavouritesUsecase>(relaxed = true)

    private lateinit var viewModel: MainViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = MainViewModel(
            applicationContextMock,
            procedureUsecase = procedureUsecaseMock,
            favouritesUsecase = favouritesUsecaseMock
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cancel()
    }

    @Test
    fun `WHEN fetchProcedureList() THEN emit success and appropriate state`() =
        runTest {
            // given
            val expectedProcedures = listOf(MockData.procedureMock)
            coEvery { procedureUsecaseMock.getProcedureList() } returns flowOf(
                Result.Success(
                    expectedProcedures
                )
            )
            coEvery { favouritesUsecaseMock.getAllFavoriteItems() } returns flowOf(
                listOf(MockData.favouriteItemMock)
            )

            // when
            viewModel.fetchProceduresAndFavourites()
            testDispatcher.scheduler.advanceUntilIdle()

            // then
            coVerify { procedureUsecaseMock.getProcedureList() }
            coVerify { favouritesUsecaseMock.getAllFavoriteItems() }
            assertEquals(viewModel.proceduresState.value.items, expectedProcedures)
            assertFalse(viewModel.proceduresState.value.isLoading)
            assertNull(viewModel.proceduresState.value.error)
        }

    @Test
    fun `WHEN fetchProcedureList() THEN emit failure and appropriate state`() =
        runTest {
            // given
            val exception = Throwable("failure")
            coEvery { procedureUsecaseMock.getProcedureList() } returns flowOf(
                Result.Error(
                    exception
                )
            )
            coEvery { favouritesUsecaseMock.getAllFavoriteItems() } returns flowOf(
                listOf(MockData.favouriteItemMock)
            )

            // when
            viewModel.fetchProceduresAndFavourites()
            testDispatcher.scheduler.advanceUntilIdle()

            // then
            coVerify { procedureUsecaseMock.getProcedureList() }
            coVerify { favouritesUsecaseMock.getAllFavoriteItems() }
            assertEquals(viewModel.proceduresState.value.error, exception.toString())
            assertFalse(viewModel.proceduresState.value.isLoading)
            assertNotNull(viewModel.proceduresState.value.error)
        }

    @Test
    fun `WHEN getProcedureDetail() THEN emit success and appropriate state`() = runTest {
        // given
        val procedureId = "procedureId"
        val expectedProcedureDetail = MockData.procedureDetailMock
        coEvery { procedureUsecaseMock.getProcedureDetail(procedureId) } returns flowOf(
            Result.Success(
                expectedProcedureDetail
            )
        )

        // when
        viewModel.fetchProcedureDetail(procedureId)
        testDispatcher.scheduler.advanceUntilIdle()

        // then
        coVerify { procedureUsecaseMock.getProcedureDetail(procedureId) }
        assertEquals(viewModel.proceduresState.value.selectedProcedureDetail, expectedProcedureDetail)
        assertFalse(viewModel.proceduresState.value.isLoading)
    }

    @Test
    fun `WHEN getProcedureDetail() THEN emit failure and appropriate state`() =
        runTest {
            // given
            val procedureId = "procedureId"
            val exception = Throwable("failure")
            coEvery { procedureUsecaseMock.getProcedureDetail(procedureId) } returns flowOf(
                Result.Error(
                    exception
                )
            )

            // when
            viewModel.fetchProcedureDetail(procedureId)
            testDispatcher.scheduler.advanceUntilIdle()

            // then
            coVerify { procedureUsecaseMock.getProcedureDetail(procedureId) }
            assertNull(viewModel.proceduresState.value.selectedProcedureDetail)
            assertFalse(viewModel.proceduresState.value.isLoading)
        }

    // D_N: Not going for 100% coverage here, hopefully the tests above & spread across the project demonstrate what I'm capable of
}