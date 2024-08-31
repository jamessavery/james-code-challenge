package com.example.james_code_challenge.data.repository

import com.example.james_code_challenge.data.model.Procedure
import com.example.james_code_challenge.mock.MockData
import com.example.james_code_challenge.services.api.ProcedureApi
import com.example.james_code_challenge.util.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.IOException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.net.HttpURLConnection.HTTP_INTERNAL_ERROR

class ProcedureRepositoryImplTest {

    private val procedureApiMock = mockk<ProcedureApi>(relaxed = true)
    private lateinit var procedureRepository: ProcedureRepository

    private val mockProcedure = MockData.procedureMock

    @Before
    fun setup() {
        procedureRepository = ProcedureRepositoryImpl(procedureApiMock)
    }

    @Test
    fun `GIVEN getProcedureList() THEN returns Success when API call is successful`() = runTest {
        // given
        val mockList = listOf(mockProcedure, mockProcedure.copy(uuid="uuid"))
        coEvery { procedureApiMock.getProcedureList() } returns Response.success(mockList)

        // when
        val result = procedureRepository.getProcedureList().first()

        // then
        assert(result is Result.Success)
        val successResult = result as Result.Success
        assertEquals(successResult.data, mockList)
        coVerify { procedureApiMock.getProcedureList() }
    }

    @Test
    fun `GIVEN getProcedureList() THEN returns Error when API call fails with HTTP error code`() = runTest {
        // given
        coEvery { procedureApiMock.getProcedureList() } throws HttpException(Response.error<List<Procedure>>(HTTP_INTERNAL_ERROR, "".toResponseBody()))

        // when
        val result = procedureRepository.getProcedureList().first()

        // then
        assert(result is Result.Error)
        val errorResult = result as Result.Error
        val isCorrectErrorCode = errorResult.exception.message?.contains(HTTP_INTERNAL_ERROR.toString())
        assertTrue(isCorrectErrorCode!!)
        coVerify { procedureApiMock.getProcedureList() }
    }

    @Test
    fun `GIVEN getProcedureList() returns Error when API call throws an exception`() = runTest {
        // given
        val expectedException = IOException("Test IOException")
        coEvery { procedureApiMock.getProcedureList() } throws expectedException

        // when
        val result = procedureRepository.getProcedureList().first()

        // then
        assert(result is Result.Error)
        val errorResult = result as Result.Error
        assert(errorResult.exception == expectedException)
        coVerify { procedureApiMock.getProcedureList() }
    }

    @Test
    fun `GIVEN getProcedureList() THEN returns Error when API call as Result is null`() = runTest {
        // given
        coEvery { procedureApiMock.getProcedureList() } returns Response.success(null)

        // when
        val result = procedureRepository.getProcedureList().first()

        // then
        assert(result is Result.Error)
        coVerify { procedureApiMock.getProcedureList() }
    }

    // Skipping tests for getProcedureDetail(), hopefully above demonstrates how I write tests for this kind of logic

}