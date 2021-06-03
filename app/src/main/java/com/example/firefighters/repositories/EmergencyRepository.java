package com.example.firefighters.repositories;

import android.app.Activity;
import android.os.Handler;
import android.widget.Toast;

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
import androidx.lifecycle.MutableLiveData;

public class EmergencyRepository {

    private static EmergencyRepository instance;
    private final ArrayList<EmergencyModel> emergencies = new ArrayList<>();
    DocumentSnapshot lastVisible;
    private boolean isLoading;
    private boolean canRunThread;
    private QuerySnapshot queryUpdates;
    private Query queryEmergenciesLimit;
    private String lastFilter = ConstantsValues.FILTER_STATUS;
    private Query.Direction lastOrder = Query.Direction.DESCENDING;
    private Integer lastVisiblePosition;

    /**
     * Constructor for design pattern single
     *
     * @return instance
     */
    public static EmergencyRepository getInstance() {
        if (instance == null) {
            instance = new EmergencyRepository();
            //getEmergenciesFromFireBase();
        }
        return instance;
    }

    //These functions are used to bind live data and mutable live data with their views
    public MutableLiveData<QuerySnapshot> bindQueryUpdates() {
        MutableLiveData<QuerySnapshot> data = new MutableLiveData<>();
        data.setValue(queryUpdates);
        return data;
    }

    public MutableLiveData<ArrayList<EmergencyModel>> bindEmergencies() {
        MutableLiveData<ArrayList<EmergencyModel>> data = new MutableLiveData<>();
        data.setValue(emergencies);
        return data;
    }

    public MutableLiveData<Boolean> bindIsLoading() {
        MutableLiveData<Boolean> data = new MutableLiveData<>();
        data.setValue(isLoading);
        return data;
    }

    public MutableLiveData<String> bindLastFilter() {
        MutableLiveData<String> data = new MutableLiveData<>();
        data.setValue(lastFilter);
        return data;
    }

    public MutableLiveData<Query.Direction> bindLastOrder() {
        MutableLiveData<Query.Direction> data = new MutableLiveData<>();
        data.setValue(lastOrder);
        return data;
    }

    public MutableLiveData<Integer> bindLastVisiblePosition() {
        MutableLiveData<Integer> data = new MutableLiveData<>();
        data.setValue(lastVisiblePosition);
        return data;
    }

    //Initialize the observer for firebase
    private void getEmergenciesFromFireBase() {
        FirebaseManager.getInstance().getFirebaseFirestoreInstance().collection("emergencies").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                if (value != null) {
                    queryUpdates = value;
                }
            }
        });
    }

    //These functions are used to do modify (or work) with values of live data and mutable live data
    public void setFilter(String filter) {
        lastFilter = filter;
    }

    public void setOrder(Query.Direction order) {
        lastOrder = order;
    }

    public void firstLoad(Activity activity, int qte) {
        clearEmergencies();
        isLoading = true;
        canRunThread = true;
        queryEmergenciesLimit = (FirebaseManager.getInstance().getFirebaseFirestoreInstance()
                .collection("emergencies")
                .orderBy(lastFilter)
                .limit(qte)
        );
        queryEmergenciesLimit.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult() != null && task.getResult().size() > 0) {
                        lastVisible = task.getResult().getDocuments().get(task.getResult().size() - 1);
                        lastVisiblePosition = task.getResult().size() - 1;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            EmergencyModel emergencyModel = document.toObject(EmergencyModel.class);
                            emergencies.add(emergencyModel);
                        }
                        canRunThread = false;
                        isLoading = false;
                        Toast.makeText(activity, task.getResult().size() + " initial", Toast.LENGTH_SHORT).show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                while (canRunThread) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        EmergencyModel emergencyModel = document.toObject(EmergencyModel.class);
                                        emergencies.add(emergencyModel);
                                    }
                                    canRunThread = false;
                                    isLoading = false;
                                }
                            }
                        }, 1000);
                    }

                } else {
                    isLoading = false;
                    Toast.makeText(activity, "Failed to load data", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void clearEmergencies() {
        canRunThread = false;
        isLoading = false;
        emergencies.clear();
    }

    public void loadEmergencies(Activity activity, int qte) {
        if (emergencies.size() < qte - 1)
            return;
        isLoading = true;
        canRunThread = true;
        Toast.makeText(activity, " loaded", Toast.LENGTH_SHORT).show();
        queryEmergenciesLimit = (FirebaseManager.getInstance().getFirebaseFirestoreInstance()
                .collection("emergencies")
                .orderBy(lastFilter)
                .startAfter(lastVisible)
                .limit(qte)
        );
        queryEmergenciesLimit.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult() != null && task.getResult().size() > 0) {
                        lastVisible = task.getResult().getDocuments().get(task.getResult().size() - 1);
                        lastVisiblePosition = task.getResult().size() - 1;
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                while (canRunThread) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        EmergencyModel emergencyModel = document.toObject(EmergencyModel.class);
                                        emergencies.add(emergencyModel);
                                    }
                                    canRunThread = false;
                                    isLoading = false;
                                }
                            }
                        }, 1000);
                    }
                } else {
                    isLoading = false;
                    Toast.makeText(activity, "Failed to load data", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void uploadEmergency(EmergencyModel emergencyModel, Activity activity) {
        FirebaseManager.getInstance().getFirebaseFirestoreInstance()
                .collection("emergencies")
                .add(emergencyModel)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(activity, "Sos sent", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(activity, "Not sent", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void updateEmergency(EmergencyModel emergencyModel, Activity activity) {
        Toast.makeText(activity, "update not implemented", Toast.LENGTH_SHORT).show();
    }

    public void deleteEmergency(EmergencyModel emergencyModel, Activity activity) {
        Toast.makeText(activity, "Delete not implemented", Toast.LENGTH_SHORT).show();
    }
}
