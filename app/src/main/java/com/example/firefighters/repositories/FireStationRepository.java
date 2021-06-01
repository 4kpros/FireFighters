package com.example.firefighters.repositories;

import android.app.Activity;

import com.example.firefighters.models.EmergencyModel;
import com.example.firefighters.models.FireStationModel;
import com.example.firefighters.models.FireStationModel;
import com.example.firefighters.tools.FirebaseManager;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

public class FireStationRepository {

    private static FireStationRepository instance;

    private boolean isLoading;
    private boolean canRunThread;
    private QuerySnapshot queryUpdates;
    private Query queryFireStationsLimit;
    private Integer lastVisiblePosition;
    DocumentSnapshot lastVisible;

    private ArrayList<FireStationModel> fireStations;

    public static FireStationRepository getInstance(){
        if (instance == null)
            instance = new FireStationRepository();
        return instance;
    }

    //These functions are used to bind live data and mutable live data with their views
    public MutableLiveData<QuerySnapshot> bindQueryUpdates(){
        MutableLiveData<QuerySnapshot> data = new MutableLiveData<>();
        data.setValue(queryUpdates);
        return data;
    }
    public MutableLiveData<ArrayList<FireStationModel>> bindFireStations(){
        MutableLiveData<ArrayList<FireStationModel>> data = new MutableLiveData<>();
        data.setValue(fireStations);
        return data;
    }
    public MutableLiveData<Boolean> bindIsLoading(){
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

    public void loadFireStations(Activity activity, int qte) {
    }

    public void updateFireStation(FireStationModel fireStationModel, Activity activity) {
    }

    public void uploadFireStation(FireStationModel fireStationModel, Activity activity) {
    }

    public void deleteFireStation(FireStationModel fireStationModel, Activity activity) {
    }

    public void clearFireStations() {
    }
}
