package com.example.firefighters.viewmodels;

import com.example.firefighters.models.FireTruckModel;
import com.example.firefighters.repositories.FireTruckRepository;

import java.util.ArrayList;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class FireTruckViewModel extends ViewModel {

    MutableLiveData<ArrayList<FireTruckModel>> fireTrucksMutableLiveData;
    MutableLiveData<Boolean> isLoadingMutableLiveData;
    private FireTruckRepository repository;

    public void init() {
        if (repository != null)
            return;
        repository = new FireTruckRepository();
        fireTrucksMutableLiveData = repository.bindFireTrucks();
        isLoadingMutableLiveData = repository.bindIsLoading();
    }
}
