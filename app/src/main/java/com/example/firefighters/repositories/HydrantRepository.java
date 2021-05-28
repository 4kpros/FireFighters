package com.example.firefighters.repositories;

import com.example.firefighters.models.HydrantModel;

import java.util.ArrayList;

import androidx.lifecycle.MutableLiveData;

public class HydrantRepository {

    private static HydrantRepository instance;
    private ArrayList<HydrantModel> dataset = new ArrayList<>();

    /**
     * Constructor for design pattern single
     * @return instance
     */
    public static HydrantRepository getInstance(){
        if (instance == null)
            instance = new HydrantRepository();
        return instance;
    }

    public MutableLiveData<ArrayList<HydrantModel>> getHydrants(){
        setHydrants();
        MutableLiveData<ArrayList<HydrantModel>> data = new MutableLiveData<>();
        data.setValue(dataset);
        return data;
    }

    private void setHydrants() {
        dataset.add(new HydrantModel());
        dataset.add(new HydrantModel());
        dataset.add(new HydrantModel());
        dataset.add(new HydrantModel());
        dataset.add(new HydrantModel());
        dataset.add(new HydrantModel());
        dataset.add(new HydrantModel());
        dataset.add(new HydrantModel());
        dataset.add(new HydrantModel());
        dataset.add(new HydrantModel());
        dataset.add(new HydrantModel());
        dataset.add(new HydrantModel());
        dataset.add(new HydrantModel());
        dataset.add(new HydrantModel());
        dataset.add(new HydrantModel());
        dataset.add(new HydrantModel());
        dataset.add(new HydrantModel());
        dataset.add(new HydrantModel());
    }

    public void filterHydrantsBy(String filter){

    }

    public void reorderHydrantsBy(String order) {
        //Now we reorder the data
    }
}
