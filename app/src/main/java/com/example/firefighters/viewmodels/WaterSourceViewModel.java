package com.example.firefighters.viewmodels;

import android.app.Activity;

import com.example.firefighters.models.WaterSourceModel;
import com.example.firefighters.repositories.WaterSourceRepository;

import java.util.ArrayList;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class WaterSourceViewModel extends ViewModel {

    private MutableLiveData<ArrayList<WaterSourceModel>> waterSourcesMutableLiveData;
    private MutableLiveData<Boolean> isLoadingMutableLiveData;
    private MutableLiveData<Integer> lastVisiblePositionMutableLiveData;
    private WaterSourceRepository repository;

    public void init() {
        if (waterSourcesMutableLiveData != null)
            return;
        repository = WaterSourceRepository.getInstance();
        waterSourcesMutableLiveData = repository.bindWaterSources();
        isLoadingMutableLiveData = repository.bindIsLoading();
        lastVisiblePositionMutableLiveData = repository.bindLastVisiblePosition();
    }

    //Getters
    public LiveData<ArrayList<WaterSourceModel>> getWaterSources() {
        return waterSourcesMutableLiveData;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoadingMutableLiveData;
    }

    //Setters
    public void firstLoad(Activity activity, int qte) {
        repository.firstLoad(activity, qte);
    }

    public void loadWaterSources(Activity activity, int qte) {
        repository.loadWaterSources(activity, qte);
    }

    public void clearWaterSources() {
        repository.clearWaterSources();
    }

    public void uploadWaterSource(WaterSourceModel waterSourceModel, Activity activity) {
        repository.uploadWaterSource(waterSourceModel, activity);
    }

    public void updateWaterSource(WaterSourceModel waterSourceModel, Activity activity) {
        repository.updateWaterSource(waterSourceModel, activity);
    }

    public void deleteWaterSource(WaterSourceModel waterSourceModel, Activity activity) {
        repository.deleteWaterSource(waterSourceModel, activity);
    }
}
