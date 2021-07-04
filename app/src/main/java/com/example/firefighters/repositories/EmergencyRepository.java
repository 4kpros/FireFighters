package com.example.firefighters.repositories;

import android.app.Activity;
import android.os.Handler;
import android.widget.Toast;

import com.example.firefighters.models.EmergencyModel;
import com.example.firefighters.models.EmergencyModel;
import com.example.firefighters.models.UnitModel;
import com.example.firefighters.tools.ConstantsValues;
import com.example.firefighters.tools.FirebaseManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class EmergencyRepository {

    private static EmergencyRepository instance;

    public static EmergencyRepository getInstance() {
        if (instance == null)
            instance = new EmergencyRepository();
        return instance;
    }

    public LiveData<QuerySnapshot> getEmergenciesQuery(DocumentSnapshot lastDocument, String lastFilter, Query.Direction lastOrder, int limitCount) {
        if (lastFilter == null || lastFilter.isEmpty())
            lastFilter = ConstantsValues.FILTER_NAME;
        if (lastOrder == null)
            lastOrder = Query.Direction.DESCENDING;
        MutableLiveData<QuerySnapshot> data = new MutableLiveData<>();
        if (lastDocument != null){
            FirebaseManager.getInstance().getFirebaseFirestoreInstance()
                    .collection(ConstantsValues.EMERGENCIES_COLLECTION)
                    .orderBy(lastFilter, lastOrder)
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
                    .collection(ConstantsValues.EMERGENCIES_COLLECTION)
                    .orderBy(lastFilter, lastOrder)
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
    public LiveData<QuerySnapshot> getEmergenciesQuerySnapshot(String lastFilter, Query.Direction lastOrder) {
        if (lastFilter == null || lastFilter.isEmpty())
            lastFilter = ConstantsValues.FILTER_NAME;
        if (lastOrder == null)
            lastOrder = Query.Direction.DESCENDING;
        MutableLiveData<QuerySnapshot> data = new MutableLiveData<>();
        FirebaseManager.getInstance().getFirebaseFirestoreInstance()
                .collection(ConstantsValues.EMERGENCIES_COLLECTION)
                .orderBy(lastFilter, lastOrder)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                        data.setValue(value);
                    }
                });
        return data;
    }
    public LiveData<QuerySnapshot> getMyWorkingEmergency(String unitWork) {
        MutableLiveData<QuerySnapshot> data = new MutableLiveData<>();
        FirebaseManager.getInstance().getFirebaseFirestoreInstance()
                .collection(ConstantsValues.EMERGENCIES_COLLECTION)
                .whereEqualTo("", unitWork)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                        data.setValue(value);
                    }
                });
        return data;
    }

    public LiveData<EmergencyModel> getEmergencyWorkingOn(String currentUnit) {
        MutableLiveData<EmergencyModel> data = new MutableLiveData<>();
        if (currentUnit == null || currentUnit.isEmpty()) {
            data.setValue(null);
        } else {
            FirebaseManager.getInstance().getFirebaseFirestoreInstance()
                    .collection(ConstantsValues.EMERGENCIES_COLLECTION)
                    .whereEqualTo("currentUnit", currentUnit)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if (task.getResult().size() > 0) {
                                    EmergencyModel emergencyModel = task.getResult().getDocuments().get(0).toObject(EmergencyModel.class);
                                    data.setValue(emergencyModel);
                                } else {
                                    data.setValue(null);
                                }
                            } else {
                                data.setValue(null);
                            }
                        }
                    });
        }
        return data;
    }

    public LiveData<Integer> saveEmergency(EmergencyModel emergencyModel) {
        MutableLiveData<Integer> data = new MutableLiveData<>();
        FirebaseManager.getInstance().getFirebaseFirestoreInstance()
                .collection(ConstantsValues.EMERGENCIES_COLLECTION)
                .orderBy("id", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            long lastId = 0;
                            if (task.getResult().size() > 0) {
                                EmergencyModel em = task.getResult().getDocuments().get(0).toObject(EmergencyModel.class);
                                if (em != null)
                                    lastId = em.getId();
                            }
                            emergencyModel.setId(lastId+1);
                            long finalLastId = lastId+1;
                            FirebaseManager.getInstance().getFirebaseFirestoreInstance()
                                    .collection(ConstantsValues.EMERGENCIES_COLLECTION)
                                    .add(emergencyModel)
                                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                        @Override
                                        public void onComplete(@NonNull @NotNull Task<DocumentReference> task) {
                                            if (task.isSuccessful()){
                                                data.setValue((int) finalLastId);
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
    public LiveData<Integer> deleteEmergency(EmergencyModel emergencyModel) {
        MutableLiveData<Integer> data = new MutableLiveData<>();
        FirebaseManager.getInstance().getFirebaseFirestoreInstance()
                .collection(ConstantsValues.EMERGENCIES_COLLECTION)
                .whereEqualTo("id", emergencyModel.getId())
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
    public LiveData<Integer> updateEmergency(EmergencyModel emergencyModel){
        MutableLiveData<Integer> data = new MutableLiveData<>();
        FirebaseManager.getInstance().getFirebaseFirestoreInstance()
                .collection(ConstantsValues.EMERGENCIES_COLLECTION)
                .whereEqualTo("id", emergencyModel.getId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (DocumentSnapshot document: task.getResult()) {
                                document.getReference()
                                        .update("senderMail", emergencyModel.getSenderMail(),
                                                "messageId", emergencyModel.getMessageId(),
                                                "longitude", emergencyModel.getLongitude(),
                                                "latitude", emergencyModel.getLatitude(),
                                                "gravity", emergencyModel.getGravity(),
                                                "status", emergencyModel.getStatus()
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
