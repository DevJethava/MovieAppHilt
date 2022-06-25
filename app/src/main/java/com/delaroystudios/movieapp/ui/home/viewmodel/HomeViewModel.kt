package com.delaroystudios.movieapp.ui.home.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.delaroystudios.movieapp.data.MovieAppService
import com.delaroystudios.movieapp.data.model.MoviesResponse
import com.delaroystudios.movieapp.ui.home.repository.HomeRepository
import com.delaroystudios.movieapp.util.Resource
import com.delaroystudios.movieapp.util.hasInternetConnection
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeRepository: HomeRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    val moviePopular: MutableLiveData<Resource<MoviesResponse>> = MutableLiveData()

    fun fetchPopular(apikey: String, page: Int? = null) {
        moviePopular.postValue(Resource.Loading())
        viewModelScope.launch {
            try {
                val response = homeRepository.fetchPopular(apikey, page)
                moviePopular.postValue(Resource.Success(response.body()!!))
            } catch (ex: Exception) {
                when (ex) {
                    is IOException -> moviePopular.postValue(Resource.Error("Network Failure " + ex.localizedMessage))
                    else -> moviePopular.postValue(Resource.Error("Conversion Error"))
                }
            }
        }
    }

}