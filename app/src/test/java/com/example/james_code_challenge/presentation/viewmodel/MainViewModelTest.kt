package com.example.james_code_challenge.presentation.viewmodel

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
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
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

    private val procedureUsecaseMock = mockk<ProcedureUsecase>(relaxed = true)

    private lateinit var viewModel: MainViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = MainViewModel(procedureUsecaseMock)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cancel()
    }

    @Test
    fun `GIVEN fetchProcedureList() WHEN loads on init THEN emit success and appropriate state`() =
        runBlocking {
            // given
            val expectedProcedures = listOf(MockData.procedureMock)
            coEvery { procedureUsecaseMock.getProcedureList() } returns flowOf(
                Result.Success(
                    expectedProcedures
                )
            )

            // when
            testDispatcher.scheduler.advanceUntilIdle()

            // then
            coVerify { procedureUsecaseMock.getProcedureList() }
            assertEquals(viewModel.uiState.value.items, expectedProcedures)
            assertFalse(viewModel.uiState.value.isLoading)
            assertNull(viewModel.uiState.value.error)
        }

    @Test
    fun `GIVEN fetchProcedureList() WHEN loads on init THEN emit failure and appropriate state`() =
        runBlocking {
            // given
            val exception = Throwable("failure")
            coEvery { procedureUsecaseMock.getProcedureList() } returns flowOf(
                Result.Error(
                    exception
                )
            )

            // when
            testDispatcher.scheduler.advanceUntilIdle()

            // then
            coVerify { procedureUsecaseMock.getProcedureList() }
            assertEquals(viewModel.uiState.value.error, exception.toString())
            assertFalse(viewModel.uiState.value.isLoading)
            assertNotNull(viewModel.uiState.value.error)
        }

    @Test
    fun `WHEN getProcedureDetail() THEN emit success and appropriate state`() = runBlocking {
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
        assertEquals(viewModel.uiState.value.selectedProcedureDetail, expectedProcedureDetail)
        coVerify { procedureUsecaseMock.getProcedureDetail(procedureId) }
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `WHEN getProcedureDetail() THEN emit failure and appropriate state`() =
        runBlocking {
            // given
            val procedureId = "procedureId"
            val exception = Throwable("failure")
            coEvery { procedureUsecaseMock.getProcedureList() } returns flowOf(
                Result.Error(
                    exception
                )
            )

            // when
            viewModel.fetchProcedureDetail(procedureId)
            testDispatcher.scheduler.advanceUntilIdle()

            // then
            coVerify { procedureUsecaseMock.getProcedureDetail(procedureId) }
            assertNull(viewModel.uiState.value.selectedProcedureDetail)
            assertFalse(viewModel.uiState.value.isLoading)
        }
}