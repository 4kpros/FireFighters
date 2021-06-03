package com.example.firefighters.viewmodels;

import com.example.firefighters.models.FireStationModel;
import com.example.firefighters.repositories.FireStationRepository;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class FireStationViewModel extends ViewModel {

    FireStationRepository repository;

    private MutableLiveData<ArrayList<FireStationModel>> fireStationsMutableLiveData;
    private MutableLiveData<Boolean> isLoadingMutableLiveData;
    private MutableLiveData<QuerySnapshot> queryUpdatesMutableLiveData;
    private MutableLiveData<Integer> lastVisiblePositionMutableLiveData;

    public void init() {
        if (repository != null)
            return;
        repository = new FireStationRepository();
        queryUpdatesMutableLiveData = repository.bindQueryUpdates();
        fireStationsMutableLiveData = repository.bindFireStations();
        isLoadingMutableLiveData = repository.bindIsLoading();
        lastVisiblePositionMutableLiveData = repository.bindLastVisiblePosition();
    }
}
