package com.example.firefighters.viewmodels;

import android.app.Activity;

import com.example.firefighters.models.EmergencyModel;
import com.example.firefighters.repositories.EmergencyRepository;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class EmergencyViewModel extends ViewModel {

    private EmergencyRepository repository;

    public void init() {
        if (repository != null)
            return;
        repository = EmergencyRepository.getInstance();
    }
    public LiveData<QuerySnapshot> getEmergenciesQuery(Activity activity) {
        return repository.getEmergenciesQuery(activity);
    }
    public LiveData<QuerySnapshot> getEmergenciesQuerySnapshot(Activity activity) {
        return repository.getEmergenciesQuerySnapshot(activity);
    }

    public LiveData<Integer> saveEmergency(EmergencyModel emergencyModel, Activity activity) {
        return repository.saveEmergency(emergencyModel, activity);
    }
    public LiveData<Integer> deleteEmergency(EmergencyModel emergencyModel, Activity activity) {
        return repository.deleteEmergency(emergencyModel, activity);
    }
    public void setFilter(String filter) {
        repository.setFilter(filter);
    }
    public void setOrder(Query.Direction order) {
        repository.setOrder(order);
    }

}