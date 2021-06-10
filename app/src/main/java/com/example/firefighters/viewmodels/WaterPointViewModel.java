package com.example.firefighters.viewmodels;

import android.app.Activity;

import com.example.firefighters.models.WaterPointModel;
import com.example.firefighters.repositories.WaterPointRepository;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class WaterPointViewModel extends ViewModel {

    private MutableLiveData<QuerySnapshot> queryMutableLiveData;
    private MutableLiveData<QuerySnapshot> querySnapshotMutableLiveData;
    private MutableLiveData<Boolean> isLoadingQueryMutableLiveData;
    private MutableLiveData<Boolean> isLoadingReadMutableLiveData;
    private MutableLiveData<Boolean> isLoadingWriteMutableLiveData;

    private WaterPointRepository repository;

    public void init() {
        if (repository != null)
            return;
        repository = WaterPointRepository.getInstance();

        queryMutableLiveData = repository.bindQuery();
        querySnapshotMutableLiveData = repository.bindQuerySnapshot();
        isLoadingQueryMutableLiveData = repository.bindIsLoadingQuery();
        isLoadingReadMutableLiveData = repository.bindIsLoadingRead();
        isLoadingWriteMutableLiveData = repository.bindIsLoadingWrite();
    }

    //Getters
    public LiveData<QuerySnapshot> getWaterSourcesQuery(Activity activity) {
        return queryMutableLiveData;
    }
    public LiveData<QuerySnapshot> getWaterSourcesQuerySnapshot(Activity activity) {
        return querySnapshotMutableLiveData;
    }

    public void loadWaterPoints(Activity activity){
        repository.loadWaterPointsQuery(activity);
    }
    public void loadWaterPointsSnapshot(Activity activity){
        repository.loadWaterPointsQuerySnapshot(activity);
    }
    public void saveWaterPoint(WaterPointModel waterPointModel, Activity activity) {
        repository.saveWaterPoint(waterPointModel, activity);
    }
    public void deleteWaterPoint(WaterPointModel waterPointModel, Activity activity) {
        repository.deleteWaterPoint(waterPointModel, activity);
    }

}
