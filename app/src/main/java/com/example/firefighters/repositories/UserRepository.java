package com.example.firefighters.repositories;

import android.app.Activity;
import android.widget.Toast;

import com.example.firefighters.models.EmergencyModel;
import com.example.firefighters.models.UserAdminModel;
import com.example.firefighters.models.UserFireFighterModel;
import com.example.firefighters.models.UserModel;
import com.example.firefighters.tools.ConstantsValues;
import com.example.firefighters.tools.FirebaseManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class UserRepository {

    private static UserRepository instance;
    private String typeUser;

    public static UserRepository getInstance() {
        if (instance == null)
            instance = new UserRepository();
        return instance;
    }

    public MutableLiveData<String> bindTypeUser() {
        MutableLiveData<String> mutableLiveData = new MutableLiveData<>();
        mutableLiveData.setValue(typeUser);
        return mutableLiveData;
    }

    //Sign in with mail and password
    public LiveData<Integer> signInUser(Activity activity, String userMail, String userPassword) {
        MutableLiveData<Integer> data = new MutableLiveData<>();
        if (userMail == null || userPassword == null) {
            data.setValue(-1);
        } else {
            FirebaseManager.getInstance().getFirebaseAuthInstance().signInWithEmailAndPassword(userMail, userPassword)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                FirebaseManager.getInstance().getFirebaseFirestoreInstance()
                                        .collection(ConstantsValues.ADMINS_COLLECTION)
                                        .whereEqualTo("mail", userMail)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    if (task.getResult().size() > 0) {
                                                        typeUser = ConstantsValues.ADMIN_USER;
                                                        data.setValue(1);
                                                    } else {
                                                        FirebaseManager.getInstance().getFirebaseFirestoreInstance()
                                                                .collection(ConstantsValues.FIRE_FIGHTERS_COLLECTION)
                                                                .whereEqualTo("mail", userMail)
                                                                .get()
                                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                                                                        if (task.isSuccessful()) {
                                                                            if (task.getResult().size() > 0) {
                                                                                typeUser = ConstantsValues.FIRE_FIGHTER_USER;
                                                                                data.setValue(1);
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
                                                                                                        typeUser = ConstantsValues.NORMAL_USER;
                                                                                                        data.setValue(1);
                                                                                                    } else {
                                                                                                        data.setValue(-1);
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
                                                } else {
                                                    data.setValue(-1);
                                                }
                                            }
                                        });
                            } else {
                                data.setValue(-1);
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull @NotNull Exception e) {
                            data.setValue(-1);
                        }
                    });
        }
        return data;
    }

    //Create new user
    public LiveData<Integer> createNewUser(Activity activity, String userName, String userMail, String userPassword) {
        MutableLiveData<Integer> data = new MutableLiveData<>();
        FirebaseManager.getInstance().getFirebaseAuthInstance().createUserWithEmailAndPassword(userMail, userPassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            UserModel currentUserModel = new UserModel();
                            currentUserModel.setUserName(userName);
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
                        } else {
                            data.setValue(-1);
                        }
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

    //Create Fire Fighter profile with mail
    public LiveData<Integer> createFireFighterProfile(Activity activity, String mail) {
        MutableLiveData<Integer> data = new MutableLiveData<>();
        UserFireFighterModel fireFighterModel = new UserFireFighterModel();
        fireFighterModel.setMail(mail);
        FirebaseManager.getInstance().getFirebaseFirestoreInstance()
                .collection(ConstantsValues.FIRE_FIGHTERS_COLLECTION)
                .add(fireFighterModel)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            data.setValue(1);
                        } else {
                            data.setValue(-1);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        data.setValue(-1);
                    }
                });
        return data;
    }

    public LiveData<Integer> logOut() {
        MutableLiveData<Integer> data = new MutableLiveData<>();
        UserFireFighterModel fireFighterModel = new UserFireFighterModel();
        FirebaseManager.getInstance().getFirebaseAuthInstance().signOut();
        data.setValue(1);
        return data;
    }

    public LiveData<String> loadTypeUser() {
        String userMail = FirebaseManager.getInstance().getCurrentAuthUser().getEmail();
        MutableLiveData<String> data = new MutableLiveData<>();
        FirebaseManager.getInstance().getFirebaseFirestoreInstance()
                .collection(ConstantsValues.ADMINS_COLLECTION)
                .whereEqualTo("mail", userMail)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().size() > 0) {
                                typeUser = ConstantsValues.ADMIN_USER;
                                data.setValue(ConstantsValues.ADMIN_USER);
                            } else {
                                FirebaseManager.getInstance().getFirebaseFirestoreInstance()
                                        .collection(ConstantsValues.FIRE_FIGHTERS_COLLECTION)
                                        .whereEqualTo("mail", userMail)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    if (task.getResult().size() > 0) {
                                                        typeUser = ConstantsValues.FIRE_FIGHTER_USER;
                                                        data.setValue(ConstantsValues.FIRE_FIGHTER_USER);
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
                                                                                typeUser = ConstantsValues.NORMAL_USER;
                                                                                data.setValue(ConstantsValues.NORMAL_USER);
                                                                            } else {
                                                                                data.setValue("null");
                                                                            }
                                                                        } else {
                                                                            data.setValue("null");
                                                                        }
                                                                    }
                                                                });
                                                    }
                                                } else {
                                                    data.setValue("null");
                                                }
                                            }
                                        });
                            }
                        } else {
                            data.setValue("null");
                        }
                    }
                });
        return data;
    }
}
