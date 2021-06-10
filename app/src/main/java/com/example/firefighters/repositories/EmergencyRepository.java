package com.example.firefighters.repositories;

import android.app.Activity;
import android.os.Handler;
import android.widget.Toast;

import com.example.firefighters.models.EmergencyModel;
import com.example.firefighters.models.EmergencyModel;
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

    private String lastFilter = ConstantsValues.FILTER_STATUS;
    private Query.Direction lastOrder = Query.Direction.DESCENDING;
    private DocumentSnapshot lastDocument;

    public static EmergencyRepository getInstance() {
        if (instance == null)
            instance = new EmergencyRepository();
        return instance;
    }

    public LiveData<QuerySnapshot> getEmergenciesQuery(Activity activity) {
        MutableLiveData<QuerySnapshot> data = new MutableLiveData<>();
        if (lastDocument != null){
            FirebaseManager.getInstance().getFirebaseFirestoreInstance()
                    .collection(ConstantsValues.EMERGENCIES_COLLECTION)
                    .orderBy(lastFilter, lastOrder)
                    .startAfter(lastDocument)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                            if(task.getResult().getDocuments().size() > 0){
                                lastDocument = task.getResult().getDocuments().get(task.getResult().size()-1);
                            }
                            data.setValue(task.getResult());
                        }
                    });
        }else{
            FirebaseManager.getInstance().getFirebaseFirestoreInstance()
                    .collection(ConstantsValues.EMERGENCIES_COLLECTION)
                    .orderBy(lastFilter, lastOrder)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                            lastDocument = task.getResult().getDocuments().get(task.getResult().size()-1);
                            data.setValue(task.getResult());
                        }
                    });
        }
        return data;
    }
    public LiveData<QuerySnapshot> getEmergenciesQuerySnapshot(Activity activity) {
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
    public LiveData<Integer> saveEmergency(EmergencyModel emergencyModel, Activity activity) {
        MutableLiveData<Integer> data = new MutableLiveData<>();
        FirebaseManager.getInstance().getFirebaseFirestoreInstance()
                .collection(ConstantsValues.EMERGENCIES_COLLECTION)
                .add(emergencyModel)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DocumentReference> task) {
                        if (task.isSuccessful()){
                            task.getResult()
                                    .update("id", task.getResult().getId()
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
                        }else{
                            data.setValue(-1);
                        }
                    }
                });
        return data;
    }
    public LiveData<Integer> deleteEmergency(EmergencyModel emergencyModel, Activity activity) {
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
                                        .update("gravity", emergencyModel.getGravity(),
                                                "status", emergencyModel.getStatus(),
                                                "messageId", emergencyModel.getMessageId()
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
    public void setFilter(String filter) {
        lastFilter = filter;
        lastDocument = null;
    }
    public void setOrder(Query.Direction order) {
        lastOrder = order;
        lastDocument = null;
    }
}
