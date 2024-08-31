package com.example.james_code_challenge.domain.usecase

import com.example.james_code_challenge.data.database.dao.FavouriteItemDao
import com.example.james_code_challenge.data.model.FavouriteItem
import com.example.james_code_challenge.data.repository.FavouritesRepository
import com.example.james_code_challenge.data.repository.FavouritesRepositoryImpl
import com.example.james_code_challenge.mock.MockData
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.spyk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class FavouritesUsecaseImplTest {

    private val repositoryMock = mockk<FavouritesRepository>(relaxed = true)
    private val daoMock = mockk<FavouriteItemDao>(relaxed = true)
    private lateinit var useCase: FavouritesUsecase

    @Before
    fun setup() {
        useCase = FavouritesUsecaseImpl(repositoryMock)
    }

    @Test
    fun `WHEN toggleFavourite() THEN should call right repository methods`() = runTest {
        // given
        val uuid = "123"
        val favouriteItem = FavouriteItem(uuid, MockData.procedureMock)

        val favouritesRepository = FavouritesRepositoryImpl(daoMock)
        val spyRepositoryMock = spyk(favouritesRepository)
        useCase = FavouritesUsecaseImpl(spyRepositoryMock)

        coEvery { spyRepositoryMock.isFavourite(uuid) } returns true
        coEvery { spyRepositoryMock.deleteFavoriteItem(uuid) } returns Unit
        coEvery { daoMock.deleteFavoriteItem(uuid) } returns Unit

        // when
        useCase.toggleFavourite(favouriteItem)

        // then
        coVerify { spyRepositoryMock.deleteFavoriteItem(uuid) }
        coVerify(exactly = 0) { spyRepositoryMock.insertFavouriteItem(any()) }

        //and given
        coEvery { spyRepositoryMock.isFavourite(uuid) } returns false

        // and when
        useCase.toggleFavourite(favouriteItem)

        // and then
        coVerify { spyRepositoryMock.insertFavouriteItem(favouriteItem) }
    }

    @Test
    fun `WHEN isFavourite() THEN returns correct repository result`() = runTest {
        // given
        val uuid = "123"
        val isFavourite = true
        coEvery { repositoryMock.isFavourite(uuid) } returns true

        // when
        val result = useCase.isFavourite(uuid)

        // then
        assertEquals(isFavourite, result)
        coVerify { repositoryMock.isFavourite(uuid) }
    }

    @Test
    fun `WHEN getAllFavoriteItems() THEN returns repository result`() = runTest {
        // given
        val favouriteItems = listOf(FavouriteItem(uuid = "123", MockData.procedureMock))
        coEvery { repositoryMock.getAllFavoriteItems() } returns favouriteItems

        // when
        val result = useCase.getAllFavoriteItems().first()

        // then
        assertEquals(favouriteItems, result)
        coVerify { repositoryMock.getAllFavoriteItems() }
    }

}