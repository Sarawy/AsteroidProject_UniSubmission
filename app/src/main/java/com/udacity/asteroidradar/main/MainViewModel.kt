package com.udacity.asteroidradar.main

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.Constants.API_KEY
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.AppApi
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.repository.AppRepository
import kotlinx.coroutines.launch


class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = getDatabase(application)
    private val mainRepository = AppRepository(database)

    private val _pictureOfTheDay = MutableLiveData<PictureOfDay>()
    val pictureOfTheDay: LiveData<PictureOfDay>
        get() = _pictureOfTheDay

    private val _detailAsteroidNavigator = MutableLiveData<Asteroid?>()
    val detailAsteroidNavigator: MutableLiveData<Asteroid?>
        get() = _detailAsteroidNavigator

    private var _asteroidFilter = MutableLiveData(Constants.AsteroidsFilter.ALL)

    @RequiresApi(Build.VERSION_CODES.O)
    val asteroidList = Transformations.switchMap(_asteroidFilter) {

        when (it!!) {
            Constants.AsteroidsFilter.TODAY -> mainRepository.todayAsteroids
            Constants.AsteroidsFilter.WEEK -> mainRepository.weekAsteroids
            else -> mainRepository.allAsteroids
        }
    }

    init {
        viewModelScope.launch {
            getDayPicture()
            mainRepository.refreshAsteroidsRepository()
        }

    }

    private fun getDayPicture(){
        viewModelScope.launch {
            try {
                val result = AppApi.retrofitService.getPictureOfTheDay(API_KEY)
                _pictureOfTheDay.value = result
            }
            catch (ex:Exception){
                Log.i("error",ex.message.toString())
            }
        }
    }

    fun onChangeFilter(filter: Constants.AsteroidsFilter) {
        _asteroidFilter.postValue(filter)
    }

    fun onAsteroidClicked(asteroid: Asteroid) {
        _detailAsteroidNavigator.value = asteroid
    }

    fun onDisplayAsteroidCompleted() {
        _detailAsteroidNavigator.value = null
    }

}