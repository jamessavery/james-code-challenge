package com.example.james_code_challenge.data.repository

import com.example.james_code_challenge.data.database.dao.FavouriteItemDao
import com.example.james_code_challenge.data.model.FavouriteItem
import javax.inject.Inject

class FavouritesRepositoryImpl @Inject constructor(
    private val favouriteItemDao: FavouriteItemDao
) : FavouritesRepository {

    // D_N: Would usually place error handling for DB operations at this layer
    override suspend fun insertFavouriteItem(favouriteItem: FavouriteItem) {
        favouriteItemDao.insertFavoriteItem(favouriteItem)
    }

    override suspend fun isFavourite(uuid: String): Boolean {
        return favouriteItemDao.isFavourite(uuid)
    }

    override suspend fun getAllFavoriteItems(): List<FavouriteItem> {
        return favouriteItemDao.getAllFavoriteItems()
    }

    override suspend fun deleteFavoriteItem(uuid: String) {
        favouriteItemDao.deleteFavoriteItem(uuid)
    }

}

interface FavouritesRepository {
    suspend fun insertFavouriteItem(favouriteItem: FavouriteItem)
    suspend fun isFavourite(uuid: String): Boolean
    suspend fun getAllFavoriteItems(): List<FavouriteItem>
    suspend fun deleteFavoriteItem(uuid: String)
}