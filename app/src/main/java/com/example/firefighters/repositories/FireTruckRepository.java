package com.example.firefighters.repositories;

import android.app.Activity;

import com.example.firefighters.models.FireTruckModel;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import androidx.lifecycle.MutableLiveData;

public class FireTruckRepository {

    private static FireTruckRepository instance;

    private ArrayList<FireTruckModel> fireTrucks;
    private Boolean isLoading;
    private QuerySnapshot queryUpdates;
    private Integer lastVisiblePosition;

    public static FireTruckRepository getInstance() {
        if (instance == null)
            instance = new FireTruckRepository();
        return instance;
    }

    //These functions are used to bind live data and mutable live data with their views
    public MutableLiveData<QuerySnapshot> bindQueryUpdates() {
        MutableLiveData<QuerySnapshot> data = new MutableLiveData<>();
        data.setValue(queryUpdates);
        return data;
    }

    public MutableLiveData<ArrayList<FireTruckModel>> bindFireTrucks() {
        MutableLiveData<ArrayList<FireTruckModel>> mutableLiveData = new MutableLiveData<>();
        mutableLiveData.setValue(fireTrucks);
        return mutableLiveData;
    }

    public MutableLiveData<Boolean> bindIsLoading() {
        MutableLiveData<Boolean> data = new MutableLiveData<>();
        data.setValue(isLoading);
        return data;
    }

    public MutableLiveData<Integer> bindLastVisiblePosition() {
        MutableLiveData<Integer> data = new MutableLiveData<>();
        data.setValue(lastVisiblePosition);
        return data;
    }

    public void firstLoad(Activity activity, int qte) {
    }

    public void loadFireTrucks(Activity activity, int qte) {
    }

    public void updateFireTruck(FireTruckModel fireTruckModel, Activity activity) {
    }

    public void uploadFireTruck(FireTruckModel fireTruckModel, Activity activity) {
    }

    public void deleteFireTruck(FireTruckModel fireTruckModel, Activity activity) {
    }

    public void clearFireTrucks() {
    }
}
