package com.example.firefighters.viewmodels.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.firefighters.models.EmergencyModel
import com.example.firefighters.repositories.EmergencyRepository
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot

class EmergencyViewModel(private val repository: EmergencyRepository) : ViewModel() {

    fun getEmergenciesQuery(
        lastDocument: DocumentSnapshot?,
        lastFilter: String?,
        lastOrder: Query.Direction?,
        limitCount: Int
    ): LiveData<QuerySnapshot> {
        return repository.getEmergenciesQuery(lastDocument, lastFilter, lastOrder, limitCount)
    }

    fun getEmergenciesQuerySnapshot(
        lastFilter: String?,
        lastOrder: Query.Direction?
    ): LiveData<QuerySnapshot?> {
        return repository.getEmergenciesQuerySnapshot(lastFilter, lastOrder)
    }

    fun getLastQuerySnapshot(
        lastFilter: String?,
        lastOrder: Query.Direction?
    ): LiveData<QuerySnapshot?> {
        return repository.getLastQuerySnapshot(lastFilter, lastOrder)
    }

    fun getEmergencyWorkingOnModel(currentUnit: String?): LiveData<EmergencyModel?> {
        return repository.getEmergencyWorkingOnModel(currentUnit)
    }

    fun getEmergencyWorkingOn(currentUnit: String?): LiveData<QuerySnapshot?> {
        return repository.getEmergencyWorkingOn(currentUnit)
    }

    fun saveEmergency(emergencyModel: EmergencyModel?): LiveData<Int> {
        return repository.saveEmergency(emergencyModel!!)
    }

    fun deleteEmergency(emergencyModel: EmergencyModel?): LiveData<Int> {
        return repository.deleteEmergency(emergencyModel!!)
    }

    fun updateEmergency(emergencyModel: EmergencyModel?): LiveData<Int> {
        return repository.updateEmergency(emergencyModel!!)
    }

    class Factory(
        private val repository: EmergencyRepository
    ) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return EmergencyViewModel(repository) as T
        }
    }
}