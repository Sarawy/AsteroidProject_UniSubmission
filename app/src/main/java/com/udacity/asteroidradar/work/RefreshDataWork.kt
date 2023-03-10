package com.udacity.asteroidradar.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.repository.AppRepository
import retrofit2.HttpException

class RefreshDataWork(appContext: Context,params: WorkerParameters): CoroutineWorker(appContext,params) {
    companion object {
        const val WORK_NAME = "RefreshDataWorker"
    }

    override suspend fun doWork(): Result {
        val database = getDatabase(applicationContext)
        val repository = AppRepository(database)

        return try {
            repository.refreshAsteroidsRepository()
            Result.success()
        }
        catch (e:HttpException){
            Result.retry()
        }
    }

}