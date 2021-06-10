package com.example.firefighters.repositories;

import android.app.Activity;
import android.widget.Toast;

import com.example.firefighters.models.WaterPointModel;
import com.example.firefighters.tools.ConstantsValues;
import com.example.firefighters.tools.FirebaseManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

public class WaterPointRepository {

    private static WaterPointRepository instance;

    private QuerySnapshot query;
    private QuerySnapshot querySnapshot;
    private boolean isLoadingQuery;
    private boolean isLoadingRead;
    private boolean isLoadingWrite;

    public static WaterPointRepository getInstance() {
        if (instance == null)
            instance = new WaterPointRepository();
        return instance;
    }

    public MutableLiveData<QuerySnapshot> bindQuery() {
        MutableLiveData<QuerySnapshot> mutableLiveData = new MutableLiveData<>();
        mutableLiveData.setValue(query);
        return mutableLiveData;
    }
    public MutableLiveData<QuerySnapshot> bindQuerySnapshot() {
        MutableLiveData<QuerySnapshot> mutableLiveData = new MutableLiveData<>();
        mutableLiveData.setValue(querySnapshot);
        return mutableLiveData;
    }
    public MutableLiveData<Boolean> bindIsLoadingQuery() {
        MutableLiveData<Boolean> mutableLiveData = new MutableLiveData<>();
        mutableLiveData.setValue(isLoadingQuery);
        return mutableLiveData;
    }
    public MutableLiveData<Boolean> bindIsLoadingRead() {
        MutableLiveData<Boolean> mutableLiveData = new MutableLiveData<>();
        mutableLiveData.setValue(isLoadingRead);
        return mutableLiveData;
    }
    public MutableLiveData<Boolean> bindIsLoadingWrite() {
        MutableLiveData<Boolean> mutableLiveData = new MutableLiveData<>();
        mutableLiveData.setValue(isLoadingWrite);
        return mutableLiveData;
    }

    public void loadWaterPointsQuery(Activity activity) {
        if (isLoadingQuery)
            return;
        isLoadingQuery = true;
        FirebaseManager.getInstance().getFirebaseFirestoreInstance()
                .collection(ConstantsValues.WATER_POINTS_COLLECTION)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                        isLoadingQuery = false;
                        query = task.getResult();
                    }
                });
    }
    public void loadWaterPointsQuerySnapshot(Activity activity) {if (isLoadingQuery)
        FirebaseManager.getInstance().getFirebaseFirestoreInstance()
                .collection(ConstantsValues.WATER_POINTS_COLLECTION)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                        querySnapshot = value;
                    }
                });
    }
    public void saveWaterPoint(WaterPointModel waterPointModel, Activity activity) {
        if (isLoadingWrite)
            return;
        isLoadingWrite = true;
        FirebaseManager.getInstance().getFirebaseFirestoreInstance()
                .collection(ConstantsValues.WATER_POINTS_COLLECTION)
                .add(waterPointModel)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DocumentReference> task) {
                        isLoadingWrite = false;
                        if (task.isSuccessful()){
                            Toast.makeText(activity, "Water point saved !", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(activity, "Not sent !", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    public void deleteWaterPoint(WaterPointModel waterPointModel, Activity activity) {
        if (isLoadingWrite)
            return;
        isLoadingWrite = true;
        FirebaseManager.getInstance().getFirebaseFirestoreInstance()
                .collection(ConstantsValues.WATER_POINTS_COLLECTION)
                .whereEqualTo("id", waterPointModel.getId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (DocumentSnapshot document: task.getResult()) {
                                document.getReference()
                                        .delete()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull @NotNull Task<Void> task) {
                                                isLoadingWrite = false;
                                                if(task.isSuccessful()){
                                                    Toast.makeText(activity, "Water point deleted !", Toast.LENGTH_SHORT).show();
                                                }else{
                                                    Toast.makeText(activity, "Not deleted !", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        }else{
                            isLoadingWrite = false;
                            Toast.makeText(activity, "Not deleted !", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
