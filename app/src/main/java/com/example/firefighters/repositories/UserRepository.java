package com.example.firefighters.repositories;

import android.app.Activity;

import com.example.firefighters.models.EmergencyModel;
import com.example.firefighters.models.UserModel;
import com.example.firefighters.tools.ConstantsValues;
import com.example.firefighters.tools.FirebaseManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class UserRepository {

    private static UserRepository instance;

    public static UserRepository getInstance() {
        if (instance == null)
            instance = new UserRepository();
        return instance;
    }

    //Sign in with mail and password
    public LiveData<Integer> signInUser(String userMail, String userPassword) {
        MutableLiveData<Integer> data = new MutableLiveData<>();
        if (userMail == null || userPassword == null || userMail.isEmpty() || userPassword.isEmpty()) {
            data.setValue(-1);
        } else {
            FirebaseManager.getInstance().getFirebaseAuthInstance().signInWithEmailAndPassword(userMail, userPassword)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                data.setValue(1);
                            } else {
                                data.setValue(-1);
                            }
                        }
                    });
        }
        return data;
    }

    //Create new user
    public LiveData<Integer> createNewUser(String userName, String userMail, String userPassword, boolean isFirefighter) {
        MutableLiveData<Integer> data = new MutableLiveData<>();
        if (userMail == null || userPassword == null || userMail.isEmpty() || userPassword.isEmpty()) {
            data.setValue(-1);
        } else {
            FirebaseManager.getInstance().getFirebaseAuthInstance().createUserWithEmailAndPassword(userMail, userPassword)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                if (userMail.equals(ConstantsValues.ADMIN_EMAIL)) {
                                    data.setValue(2);
                                } else {
                                    FirebaseManager.getInstance().getFirebaseFirestoreInstance()
                                            .collection(ConstantsValues.USERS_COLLECTION)
                                            .whereEqualTo("mail", userMail)
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        if (task.getResult().size() > 0) {
                                                            UserModel user = task.getResult().getDocuments().get(0).toObject(UserModel.class);
                                                            if(user != null && user.isFireFighter()){
                                                                data.setValue(1);
                                                            }else if (user != null){
                                                                data.setValue(-1);
                                                            }
                                                        } else {
                                                            UserModel currentUserModel = new UserModel();
                                                            currentUserModel.setUserName(userName);
                                                            currentUserModel.setFireFighter(isFirefighter);
                                                            currentUserModel.setMail(userMail);
                                                            FirebaseManager.getInstance().getFirebaseFirestoreInstance()
                                                                    .collection(ConstantsValues.USERS_COLLECTION)
                                                                    .add(currentUserModel)
                                                                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull @NotNull Task<DocumentReference> task) {
                                                                            if (task.isSuccessful()) {
                                                                                data.setValue(1);
                                                                            } else {
                                                                                data.setValue(-1);
                                                                            }
                                                                        }
                                                                    });
                                                        }
                                                    } else {
                                                        data.setValue(-1);
                                                    }
                                                }
                                            });
                                }
                            } else {
                                data.setValue(-1);
                            }
                        }
                    });
        }
        return data;
    }

    public LiveData<UserModel> loadUserModel(String userMail) {
        MutableLiveData<UserModel> data = new MutableLiveData<>();
        UserModel user = null;
        if (userMail == null || userMail.isEmpty()) {
            data.setValue(null);
        } else {
            FirebaseManager.getInstance().getFirebaseFirestoreInstance()
                    .collection(ConstantsValues.USERS_COLLECTION)
                    .whereEqualTo("mail", userMail)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if (task.getResult().size() > 0) {
                                    UserModel user = task.getResult().getDocuments().get(0).toObject(UserModel.class);
                                    data.setValue(user);
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


    public LiveData<QuerySnapshot> loadAllFireFightersWorkingUnitQuery(String unit) {
        MutableLiveData<QuerySnapshot> data = new MutableLiveData<>();
        FirebaseManager.getInstance().getFirebaseFirestoreInstance()
                .collection(ConstantsValues.USERS_COLLECTION)
                .whereEqualTo("unit", unit)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                        data.setValue(task.getResult());
                    }
                });
        return data;
    }

    //Reset password
    public LiveData<Integer> resetPasswordWithMail(Activity activity, String userMail) {
        MutableLiveData<Integer> data = new MutableLiveData<>();
        FirebaseManager.getInstance().getFirebaseAuthInstance().sendPasswordResetEmail(userMail)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            data.setValue(1);
                        } else {
                            data.setValue(-1);
                        }
                    }
                });
        return data;
    }

    public LiveData<Integer> logOut() {
        MutableLiveData<Integer> data = new MutableLiveData<>();
        FirebaseManager.getInstance().getFirebaseAuthInstance().signOut();
        data.setValue(1);
        return data;
    }

    public LiveData<QuerySnapshot> loadFireFightersQuery(DocumentSnapshot lastDocument, int limitCount) {
        MutableLiveData<QuerySnapshot> data = new MutableLiveData<>();
        if (lastDocument != null){
            FirebaseManager.getInstance().getFirebaseFirestoreInstance()
                    .collection(ConstantsValues.USERS_COLLECTION)
                    .whereEqualTo("fireFighter", true)
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
                    .collection(ConstantsValues.USERS_COLLECTION)
                    .whereEqualTo("fireFighter", true)
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

    public LiveData<QuerySnapshot> loadFireFightersQuerySnapshot() {
        MutableLiveData<QuerySnapshot> data = new MutableLiveData<>();
        FirebaseManager.getInstance().getFirebaseFirestoreInstance()
                .collection(ConstantsValues.USERS_COLLECTION)
                .whereEqualTo("fireFighter", true)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                        data.setValue(value);
                    }
                });
        return data;
    }

    public LiveData<Integer> saveFireFighter(UserModel firefighterModel) {
        MutableLiveData<Integer> data = new MutableLiveData<>();
        FirebaseManager.getInstance().getFirebaseFirestoreInstance()
                .collection(ConstantsValues.USERS_COLLECTION)
                .add(firefighterModel)
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
        return data;
    }

    public LiveData<Integer> updateFireFighter(UserModel firefighterModel) {
        MutableLiveData<Integer> data = new MutableLiveData<>();
        FirebaseManager.getInstance().getFirebaseFirestoreInstance()
                .collection(ConstantsValues.EMERGENCIES_COLLECTION)
                .whereEqualTo("mail", firefighterModel.getMail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            data.setValue(1);
                        }else{
                            data.setValue(-1);
                        }
                    }
                });
        return data;
    }

    public LiveData<Integer> deleteFireFighter(UserModel firefighterModel) {
        MutableLiveData<Integer> data = new MutableLiveData<>();
        FirebaseManager.getInstance().getFirebaseFirestoreInstance()
                .collection(ConstantsValues.USERS_COLLECTION)
                .whereEqualTo("fireFighter", true)
                .whereEqualTo("mail", firefighterModel.getMail())
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
}
