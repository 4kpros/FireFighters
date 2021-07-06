package com.example.firefighters.viewmodels;

import com.example.firefighters.models.EmergencyModel;
import com.example.firefighters.repositories.EmergencyRepository;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class EmergencyViewModel extends ViewModel {

    private EmergencyRepository repository;

    public void init() {
        if (repository != null)
            return;
        repository = EmergencyRepository.getInstance();
    }
    public LiveData<QuerySnapshot> getEmergenciesQuery(DocumentSnapshot lastDocument, String lastFilter, Query.Direction lastOrder, int limitCount) {
        return repository.getEmergenciesQuery(lastDocument, lastFilter, lastOrder, limitCount);
    }
    public LiveData<QuerySnapshot> getEmergenciesQuerySnapshot(String lastFilter, Query.Direction lastOrder) {
        return repository.getEmergenciesQuerySnapshot(lastFilter, lastOrder);
    }

    public LiveData<EmergencyModel> getEmergencyWorkingOnModel(String  currentUnit) {
        return repository.getEmergencyWorkingOnModel(currentUnit);
    }
    public LiveData<QuerySnapshot> getEmergencyWorkingOn(String  currentUnit) {
        return repository.getEmergencyWorkingOn(currentUnit);
    }
    public LiveData<Integer> saveEmergency(EmergencyModel emergencyModel) {
        return repository.saveEmergency(emergencyModel);
    }
    public LiveData<Integer> deleteEmergency(EmergencyModel emergencyModel) {
        return repository.deleteEmergency(emergencyModel);
    }
    public LiveData<Integer> updateEmergency(EmergencyModel emergencyModel) {
        return repository.updateEmergency(emergencyModel);
    }

}