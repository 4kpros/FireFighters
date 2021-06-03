package com.example.firefighters.viewmodels;

import android.app.Activity;

import com.example.firefighters.models.EmergencyModel;
import com.example.firefighters.repositories.EmergencyRepository;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class EmergencyViewModel extends ViewModel {

    private MutableLiveData<ArrayList<EmergencyModel>> emergenciesMutableLiveData;
    private MutableLiveData<Boolean> isLoadingMutableLiveData;
    private MutableLiveData<String> filterMutableLiveData;
    private MutableLiveData<Query.Direction> orderMutableLiveData;
    private MutableLiveData<QuerySnapshot> queryUpdatesMutableLiveData;
    private EmergencyRepository repository;
    private MutableLiveData<Integer> lastVisiblePositionMutableLiveData;


    public void init() {
        if (repository != null)
            return;
        repository = EmergencyRepository.getInstance();
        queryUpdatesMutableLiveData = repository.bindQueryUpdates();
        emergenciesMutableLiveData = repository.bindEmergencies();
        isLoadingMutableLiveData = repository.bindIsLoading();
        filterMutableLiveData = repository.bindLastFilter();
        orderMutableLiveData = repository.bindLastOrder();
        lastVisiblePositionMutableLiveData = repository.bindLastVisiblePosition();
    }

    //Getters
    public LiveData<QuerySnapshot> getQueryEmergencies() {
        return queryUpdatesMutableLiveData;
    }

    public LiveData<ArrayList<EmergencyModel>> getEmergencies() {
        return emergenciesMutableLiveData;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoadingMutableLiveData;
    }

    public LiveData<Integer> getLastVisibleIemPosition() {
        return lastVisiblePositionMutableLiveData;
    }

    public LiveData<String> getFilter() {
        return filterMutableLiveData;
    }

    public LiveData<Query.Direction> getOrder() {
        return orderMutableLiveData;
    }

    //Setters
    public void firstLoad(Activity activity, int qte) {
        repository.firstLoad(activity, qte);
    }

    public void loadEmergencies(Activity activity, int qte) {
        repository.loadEmergencies(activity, qte);
    }

    public void setFilterEmergencies(String filter) {
        repository.setFilter(filter);
    }

    public void setOrderEmergencies(Query.Direction order) {
        repository.setOrder(order);
    }

    public void clearEmergencies() {
        repository.clearEmergencies();
    }

    public void uploadEmergency(EmergencyModel emergencyModel, Activity activity) {
        repository.uploadEmergency(emergencyModel, activity);
    }

    public void updateEmergency(EmergencyModel emergencyModel, Activity activity) {
        repository.updateEmergency(emergencyModel, activity);
    }

    public void deleteEmergency(EmergencyModel emergencyModel, Activity activity) {
        repository.deleteEmergency(emergencyModel, activity);
    }
}