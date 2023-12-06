package com.example.firefighters.viewmodels.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.firefighters.models.UnitModel
import com.example.firefighters.repositories.UnitRepository
import com.google.firebase.firestore.QuerySnapshot

class UnitViewModel(private val mRepository: UnitRepository) : ViewModel() {

    fun getUnitModel(unitName: String?): LiveData<UnitModel?> {
        return mRepository.getUnitModel(unitName)
    }

    val unitsQuery: LiveData<QuerySnapshot>
        get() = mRepository.unitsQuery

    fun saveUnit(unitModel: UnitModel?): LiveData<Int> {
        return mRepository.saveUnit(unitModel!!)
    }

    class Factory(
        private val repository: UnitRepository
    ) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return UnitViewModel(repository) as T
        }
    }
}
