package com.example.james_code_challenge.presentation.viewmodel

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.james_code_challenge.MainCoroutineRule
import com.example.james_code_challenge.domain.usecase.FavouritesUsecase
import com.example.james_code_challenge.domain.usecase.ProcedureUsecase
import com.example.james_code_challenge.mock.MockData
import com.example.james_code_challenge.domain.util.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class MainViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private val applicationContextMock = mockk<Context>(relaxed = true)
    private val procedureUsecaseMock = mockk<ProcedureUsecase>(relaxed = true)
    private val favouritesUsecaseMock = mockk<FavouritesUsecase>(relaxed = true)

    private lateinit var viewModel: MainViewModel

    @Before
    fun setup() {
        viewModel = MainViewModel(
            applicationContextMock,
            procedureUsecase = procedureUsecaseMock,
            favouritesUsecase = favouritesUsecaseMock
        )
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
            advanceUntilIdle()

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
            advanceUntilIdle()

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
        advanceUntilIdle()

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
            advanceUntilIdle()

            // then
            coVerify { procedureUsecaseMock.getProcedureDetail(procedureId) }
            assertNull(viewModel.proceduresState.value.selectedProcedureDetail)
            assertFalse(viewModel.proceduresState.value.isLoading)
        }

    // D_N: Not going for 100% coverage here, hopefully the tests above & spread across the project demonstrate what I'm capable of
}