package com.example.james_code_challenge.data.repository

import com.example.james_code_challenge.data.database.dao.FavouriteItemDao
import com.example.james_code_challenge.data.model.FavouriteItem
import com.example.james_code_challenge.mock.MockData
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class FavouritesRepositoryImplTest {

    private val favouriteItemDaoMock = mockk<FavouriteItemDao>(relaxed = true)

    private lateinit var favouritesRepository: FavouritesRepository

    @Before
    fun setUp() {
        favouritesRepository = FavouritesRepositoryImpl(favouriteItemDaoMock)
    }

    @Test
    fun `WHEN insertFavouriteItem() THEN Dao should insertFavoriteItem()`() = runTest {
        // given
        val favouriteItem = FavouriteItem(uuid = "123", procedure = MockData.procedureMock)

        // when
        favouritesRepository.insertFavouriteItem(favouriteItem)

        // then
        coVerify { favouriteItemDaoMock.insertFavoriteItem(favouriteItem) }
    }

    @Test
    fun `WHEN getAllFavoriteItems() THEN Dao should getAllFavoriteItems()`() = runTest {
        // given
        val expectedItems = listOf(FavouriteItem(uuid = "123", procedure = MockData.procedureMock))
        coEvery { favouriteItemDaoMock.getAllFavoriteItems() } returns expectedItems

        // when
        val actualItems = favouritesRepository.getAllFavoriteItems()

        // then
        assert(actualItems == expectedItems)
        coVerify { favouriteItemDaoMock.getAllFavoriteItems() }
    }

    @Test
    fun `WHEN deleteFavoriteItem() THEN Dao should deleteFavoriteItem()`() = runTest {
        // given
        val uuid = "123"

        // when
        favouritesRepository.deleteFavoriteItem(uuid)

        // then
        coVerify { favouriteItemDaoMock.deleteFavoriteItem(uuid) }
    }

    @Test
    fun `WHEN isFavourite() THEN Dao should isFavourite()`() = runTest {
        // given
        val uuid = "123"

        // when
        favouritesRepository.isFavourite(uuid)

        // then
        coVerify { favouriteItemDaoMock.isFavourite(uuid) }
    }

}