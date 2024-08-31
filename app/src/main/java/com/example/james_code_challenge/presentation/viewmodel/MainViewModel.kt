package com.example.james_code_challenge.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.james_code_challenge.Constants.Companion.PROCEDURES_OFFLINE_JSON
import com.example.james_code_challenge.data.model.FavouriteItem
import com.example.james_code_challenge.data.model.Procedure
import com.example.james_code_challenge.data.model.ProcedureDetail
import com.example.james_code_challenge.domain.usecase.FavouritesUsecase
import com.example.james_code_challenge.domain.usecase.ProcedureUsecase
import com.example.james_code_challenge.util.JsonUtils
import com.example.james_code_challenge.util.Result
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import java.io.IOException
import java.lang.reflect.Type
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val procedureUsecase: ProcedureUsecase,
    private val favouritesUsecase: FavouritesUsecase
) : ViewModel() {

    private val _proceduresState = MutableStateFlow(ProceduresState())
    val proceduresState: StateFlow<ProceduresState> = _proceduresState.asStateFlow()

    fun fetchProceduresAndFavourites() {
        viewModelScope.launch {
            _proceduresState.emit(_proceduresState.value.copy(isLoading = true))

            val procedures = procedureUsecase.getProcedureList()
            val favourites = favouritesUsecase.getAllFavoriteItems()

            combine(procedures, favourites) { proceduresCall, favouriteItems ->
                when (proceduresCall) { // D_N: Perhaps not the best idea to rely on just the success of just API call here, but for sake of this technical
                    is Result.Success -> fetchProceduresAndFavouritesSuccess(proceduresCall.data, favouriteItems)
                    is Result.Error -> fetchProceduresAndFavouritesFailure(proceduresCall.exception)
                }
            }.launchIn(viewModelScope)
        }
    }

    private fun fetchProceduresAndFavouritesSuccess(
        procedureList: List<Procedure>,
        favouriteItems: List<FavouriteItem>
    ) {
        viewModelScope.launch {
            _proceduresState.emit(
                _proceduresState.value.copy(
                    isLoading = false,
                    items = procedureList,
                    favouriteItems = favouriteItems.map { it.procedure },
                    isOffline = false
                )
            )
        }
    }

    private fun fetchProceduresAndFavouritesFailure(exception: Throwable) {
        if(exception == IOException()) { // Init offline mode
            viewModelScope.launch {
                _proceduresState.emit(
                    _proceduresState.value.copy(
                        isLoading = false,
                        items = processOfflineData(),
                        isOffline = true
                    )
                )
            }
        } else { // else usual error handling
            viewModelScope.launch {
                _proceduresState.emit(
                    _proceduresState.value.copy(
                        isLoading = false,
                        error = exception.toString(),
                        isOffline = false
                    )
                )
            }
        }
    }

    // D_N: Bonus objective - so refraining from creating a usecase/following proper protocol (or even unit testing)
    // Is a basic impl, users can view the procedure list & even continue using the favourites feature,
    // but images & procedureDetails are not available
    private fun processOfflineData(): List<Procedure> {
        val jsonFileString =
            JsonUtils.getJsonFromAssets(context, PROCEDURES_OFFLINE_JSON)
        val listUserType: Type = object : TypeToken<List<Procedure>>() {}.type
        val proceduresResponse: List<Procedure> = Gson().fromJson(jsonFileString, listUserType)
        return proceduresResponse
    }

    fun fetchProcedureDetail(procedureId: String) {
        viewModelScope.launch {
            procedureUsecase.getProcedureDetail(procedureId).collect {
                when (it) {
                    is Result.Success -> onProcedureDetailSuccess(it.data)
                    is Result.Error -> onProcedureDetailFailure()
                }
            }
        }
    }

    private fun onProcedureDetailSuccess(procedureDetail: ProcedureDetail) {
        viewModelScope.launch {
            _proceduresState.emit(
                _proceduresState.value.copy(
                    isLoading = false,
                    selectedProcedureDetail = procedureDetail
                )
            )
        }
    }

    private fun onProcedureDetailFailure() {
        viewModelScope.launch {
            _proceduresState.emit(
                _proceduresState.value.copy(
                    isLoading = false,
                    selectedProcedureDetail = null
                )
            )
        }
    }

    fun toggleFavouriteItem(favouriteItem: FavouriteItem) {
        viewModelScope.launch {
            favouritesUsecase.toggleFavourite(favouriteItem)

            // Emit event with latest list of favourites for use in DB
            viewModelScope.launch {
                favouritesUsecase.getAllFavoriteItems().collect { fav ->
                    _proceduresState.emit(
                        _proceduresState.value.copy(
                            favouriteItems = fav.map { it.procedure }
                        )
                    )
                }
            }
        }
    }

    fun fetchFavouriteList() {
        viewModelScope.launch {
            favouritesUsecase.getAllFavoriteItems().collect { favouritesList ->
                val favouriteProcedures = favouritesList.map { it.procedure }
                _proceduresState.emit(
                    _proceduresState.value.copy(
                        items = favouriteProcedures,
                        favouriteItems = favouriteProcedures,
                    )
                )
            }
        }
    }

    fun isFavourite(currentUuid: String): Boolean {
        val currentFavourites: List<String> = proceduresState.value.favouriteItems.map { it.uuid }
        return currentFavourites.contains(currentUuid)
    }

    data class ProceduresState(
        val isLoading: Boolean = false,
        val items: List<Procedure> = emptyList(),
        val error: String? = null, // D_N: Would use an ErrorType on actual app
        val favouriteItems: List<Procedure> = emptyList(),
        var selectedProcedureDetail: ProcedureDetail? = null,
        var isOffline: Boolean = false
    )

}