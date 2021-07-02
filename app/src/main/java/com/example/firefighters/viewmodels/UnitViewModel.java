package com.example.firefighters.viewmodels;

import com.example.firefighters.models.EmergencyModel;
import com.example.firefighters.models.UnitModel;
import com.example.firefighters.repositories.EmergencyRepository;
import com.example.firefighters.repositories.UnitRepository;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class UnitViewModel extends ViewModel {

    private UnitRepository repository;

    public void init() {
        if (repository != null)
            return;
        repository = UnitRepository.getInstance();
    }
    public LiveData<UnitModel> getUnitModel(String unitName) {
        return repository.getUnitModel(unitName);
    }
    public LiveData<QuerySnapshot> getUnitsQuery() {
        return repository.getUnitsQuery();
    }

    public LiveData<Integer> saveUnit(UnitModel unitModel) {
        return repository.saveUnit(unitModel);
    }
}
