package com.example.firefighters.repositories;

import android.app.Activity;
import android.widget.Toast;

import com.example.firefighters.models.WaterPointModel;
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
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class WaterPointRepository {

    private static WaterPointRepository instance;

    public static WaterPointRepository getInstance() {
        if (instance == null)
            instance = new WaterPointRepository();
        return instance;
    }

    public LiveData<QuerySnapshot> getAllWaterPointsQuery() {
        MutableLiveData<QuerySnapshot> data = new MutableLiveData<>();
        FirebaseManager.getInstance().getFirebaseFirestoreInstance()
                .collection(ConstantsValues.WATER_POINTS_COLLECTION)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                        data.setValue(task.getResult());
                    }
                });
        return data;
    }

    public LiveData<QuerySnapshot> getWaterPointsQuery(DocumentSnapshot lastDocument, int limitCount) {
        MutableLiveData<QuerySnapshot> data = new MutableLiveData<>();
        if (lastDocument != null){
            FirebaseManager.getInstance().getFirebaseFirestoreInstance()
                    .collection(ConstantsValues.WATER_POINTS_COLLECTION)
                    .startAfter(lastDocument)
                    .limit(limitCount)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                            data.setValue(task.getResult());
                        }
                    });
        }else{
            FirebaseManager.getInstance().getFirebaseFirestoreInstance()
                    .collection(ConstantsValues.WATER_POINTS_COLLECTION)
                    .limit(limitCount)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                            data.setValue(task.getResult());
                        }
                    });
        }
        return data;
    }
    public LiveData<QuerySnapshot> getWaterPointsQuerySnapshot() {
        MutableLiveData<QuerySnapshot> data = new MutableLiveData<>();
        FirebaseManager.getInstance().getFirebaseFirestoreInstance()
                .collection(ConstantsValues.WATER_POINTS_COLLECTION)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                        data.setValue(value);
                    }
                });
        return data;
    }
    public LiveData<Integer> saveWaterPoint(WaterPointModel waterPointModel) {
        MutableLiveData<Integer> data = new MutableLiveData<>();
        FirebaseManager.getInstance().getFirebaseFirestoreInstance()
                .collection(ConstantsValues.WATER_POINTS_COLLECTION)
                .limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            long lastId = 0;
                            if (task.getResult().size() > 0) {
                                WaterPointModel wp = task.getResult().getDocuments().get(0).toObject(WaterPointModel.class);
                                if (wp != null)
                                    lastId = wp.getId();
                            }
                            waterPointModel.setId(lastId+1);
                            FirebaseManager.getInstance().getFirebaseFirestoreInstance()
                                    .collection(ConstantsValues.WATER_POINTS_COLLECTION)
                                    .add(waterPointModel)
                                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                        @Override
                                        public void onComplete(@NonNull @NotNull Task<DocumentReference> task) {
                                            if (task.isSuccessful()){
                                                data.setValue(1);
                                            }else{
                                                data.setValue(-1);
                                            }
                                        }
                                    });
                        }else {
                            data.setValue(-1);
                        }
                    }
                });

        return data;
    }
    public LiveData<Integer> deleteWaterPoint(WaterPointModel waterPointModel) {
        MutableLiveData<Integer> data = new MutableLiveData<>();
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
                                                if(task.isSuccessful()){
                                                    data.setValue(1);
                                                }else{
                                                    data.setValue(-1);
                                                }
                                            }
                                        });
                            }
                        }else{
                            data.setValue(-1);
                        }
                    }
                });
        return data;
    }
    public LiveData<Integer> updateWaterPoint(WaterPointModel waterPointModel){
        MutableLiveData<Integer> data = new MutableLiveData<>();
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
                                        .update("senderMail", waterPointModel.getSenderMail(),
                                                "estimatedQte", waterPointModel.getEstimatedQte(),
                                                "longitude", waterPointModel.getLongitude(),
                                                "latitude", waterPointModel.getLatitude(),
                                                "sourceType", waterPointModel.getSourceType()
                                        )
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull @NotNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    data.setValue(1);
                                                }else{
                                                    data.setValue(-1);
                                                }
                                            }
                                        });
                            }
                        }else{
                            data.setValue(-1);
                        }
                    }
                });
        return data;
    }

}
