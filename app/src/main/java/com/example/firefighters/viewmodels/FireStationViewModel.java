package com.example.firefighters.viewmodels;

import android.app.Activity;

import com.example.firefighters.models.FireStationModel;
import com.example.firefighters.models.FireStationModel;
import com.example.firefighters.repositories.FireStationRepository;
import com.example.firefighters.repositories.FireStationRepository;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class FireStationViewModel extends ViewModel {


    private MutableLiveData<QuerySnapshot> queryMutableLiveData;
    private MutableLiveData<QuerySnapshot> querySnapshotMutableLiveData;
    private MutableLiveData<Boolean> isLoadingQueryMutableLiveData;
    private MutableLiveData<Boolean> isLoadingReadMutableLiveData;
    private MutableLiveData<Boolean> isLoadingWriteMutableLiveData;

    private FireStationRepository repository;

    public void init() {
        if (repository != null)
            return;
        repository = FireStationRepository.getInstance();

        queryMutableLiveData = repository.bindQuery();
        querySnapshotMutableLiveData = repository.bindQuerySnapshot();
        isLoadingQueryMutableLiveData = repository.bindIsLoadingQuery();
        isLoadingReadMutableLiveData = repository.bindIsLoadingRead();
        isLoadingWriteMutableLiveData = repository.bindIsLoadingWrite();
    }

    public MutableLiveData<QuerySnapshot> getFireStationsQuery(Activity activity) {
        return queryMutableLiveData;
    }
    public MutableLiveData<QuerySnapshot> getFireStationsQuerySnapshot(Activity activity) {
        return querySnapshotMutableLiveData;
    }

    public void loadFireStationsQuery(Activity activity) {
        repository.loadFireStationsQuery(activity);
    }
    public void loadFireStationsQuerySnapshot(Activity activity) {
        repository.loadFireStationsQuerySnapshot(activity);
    }

    public void saveFireStationPoint(FireStationModel fireStationModel, Activity activity) {
        repository.saveFireStationPoint(fireStationModel, activity);
    }
    public void deleteFireStationPoint(FireStationModel fireStationModel, Activity activity) {
        repository.deleteFireStationPoint(fireStationModel, activity);
    }
}
