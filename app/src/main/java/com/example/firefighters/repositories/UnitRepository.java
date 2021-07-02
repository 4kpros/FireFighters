package com.example.firefighters.repositories;

import com.example.firefighters.models.EmergencyModel;
import com.example.firefighters.models.UnitModel;
import com.example.firefighters.models.UserModel;
import com.example.firefighters.tools.ConstantsValues;
import com.example.firefighters.tools.FirebaseManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class UnitRepository {

    private static UnitRepository instance;

    public static UnitRepository getInstance() {
        if (instance == null)
            instance = new UnitRepository();
        return instance;
    }

    public LiveData<UnitModel> getUnitModel(String unitName) {
        MutableLiveData<UnitModel> data = new MutableLiveData<>();
        if (unitName == null || unitName.isEmpty()) {
            data.setValue(null);
        } else {
            FirebaseManager.getInstance().getFirebaseFirestoreInstance()
                    .collection(ConstantsValues.UNIT_COLLECTION)
                    .whereEqualTo("unitName", unitName)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if (task.getResult().size() > 0) {
                                    UnitModel unitModel = task.getResult().getDocuments().get(0).toObject(UnitModel.class);
                                    data.setValue(unitModel);
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

    public LiveData<QuerySnapshot> getUnitsQuery() {
        MutableLiveData<QuerySnapshot> data = new MutableLiveData<>();
        FirebaseManager.getInstance().getFirebaseFirestoreInstance()
                .collection(ConstantsValues.UNIT_COLLECTION)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                        data.setValue(task.getResult());
                    }
                });
        return data;
    }

    public LiveData<Integer> saveUnit(UnitModel unitModel) {
        MutableLiveData<Integer> data = new MutableLiveData<>();
        FirebaseManager.getInstance().getFirebaseFirestoreInstance()
                .collection(ConstantsValues.UNIT_COLLECTION)
                .whereEqualTo("unitName", unitModel.getUnitName())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            if (task.getResult().size() > 0) {
                                data.setValue(-1);
                            }else{
                                FirebaseManager.getInstance().getFirebaseFirestoreInstance()
                                    .collection(ConstantsValues.UNIT_COLLECTION)
                                    .add(unitModel)
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
                            }
                        }else {
                            data.setValue(-1);
                        }
                    }
                });

        return data;
    }
}
