package com.example.firefighters.repositories;

import android.app.Activity;

import com.example.firefighters.models.WaterSourceModel;

import java.util.ArrayList;

import androidx.lifecycle.MutableLiveData;

public class WaterSourceRepository {

    private static WaterSourceRepository instance;

    private ArrayList<WaterSourceModel> waterSources;
    private Boolean isLoading;
    private int lastVisiblePosition;

    public static WaterSourceRepository getInstance(){
        if (instance == null)
            instance = new WaterSourceRepository();
        return instance;
    }

    public MutableLiveData<ArrayList<WaterSourceModel>> bindWaterSources(){
        MutableLiveData<ArrayList<WaterSourceModel>> mutableLiveData = new MutableLiveData<>();
        mutableLiveData.setValue(waterSources);
        return mutableLiveData;
    }
    public MutableLiveData<Boolean> bindIsLoading() {
        MutableLiveData<Boolean> mutableLiveData = new MutableLiveData<>();
        mutableLiveData.setValue(isLoading);
        return mutableLiveData;
    }
    public MutableLiveData<Integer> bindLastVisiblePosition() {
        MutableLiveData<Integer> mutableLiveData = new MutableLiveData<>();
        mutableLiveData.setValue(lastVisiblePosition);
        return mutableLiveData;
    }

    public void firstLoad(Activity activity, int qte) {
    }

    public void loadWaterSources(Activity activity, int qte) {
    }

    public void updateWaterSource(WaterSourceModel waterSourceModel, Activity activity) {
    }

    public void uploadWaterSource(WaterSourceModel waterSourceModel, Activity activity) {
    }

    public void deleteWaterSource(WaterSourceModel waterSourceModel, Activity activity) {
    }

    public void clearWaterSources() {
    }

}
