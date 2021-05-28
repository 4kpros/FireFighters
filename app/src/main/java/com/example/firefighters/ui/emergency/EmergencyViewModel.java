package com.example.firefighters.ui.emergency;

import com.example.firefighters.models.HydrantModel;
import com.example.firefighters.repositories.HydrantRepository;

import java.util.ArrayList;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class EmergencyViewModel extends ViewModel {

    private MutableLiveData<ArrayList<HydrantModel>> hydrants;
    private HydrantRepository repository;

    private MutableLiveData<String> mText;

    public EmergencyViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is hydrants fragment");
    }

    public void init(){
        if (hydrants != null)
            return;
        repository = HydrantRepository.getInstance();
        hydrants = repository.getHydrants();
    }

    public LiveData<ArrayList<HydrantModel>> getHydrants(){
        return hydrants;
    }
}