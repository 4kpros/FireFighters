package com.example.firefighters.viewmodels.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.firefighters.models.WaterPointModel
import com.example.firefighters.repositories.WaterPointRepository
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot

class WaterPointViewModel(private val mRepository: WaterPointRepository) : ViewModel() {

    val allWaterPointsQuery: LiveData<QuerySnapshot>
        get() = mRepository.allWaterPointsQuery

    fun getWaterPointsQuery(
        lastDocument: DocumentSnapshot?,
        limitCount: Int
    ): LiveData<QuerySnapshot> {
        return mRepository.getWaterPointsQuery(lastDocument, limitCount)
    }

    val waterPointsQuerySnapshot: LiveData<QuerySnapshot?>
        get() = mRepository.waterPointsQuerySnapshot

    fun getWaterPointModel(id: Int): LiveData<WaterPointModel?> {
        return mRepository.getWaterPointModel(id)
    }

    fun saveWaterPoint(waterPointModel: WaterPointModel?): LiveData<Int> {
        return mRepository.saveWaterPoint(waterPointModel!!)
    }

    fun deleteWaterPoint(waterPointModel: WaterPointModel?): LiveData<Int> {
        return mRepository.deleteWaterPoint(waterPointModel!!)
    }

    fun updateWaterPoint(waterPointModel: WaterPointModel?): LiveData<Int> {
        return mRepository.updateWaterPoint(waterPointModel!!)
    }

    class Factory(
        private val repository: WaterPointRepository
    ) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return WaterPointViewModel(repository) as T
        }
    }
}
