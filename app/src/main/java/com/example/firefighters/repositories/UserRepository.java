package com.example.firefighters.repositories;

import com.example.firefighters.models.FireTruckModel;
import com.example.firefighters.models.UserAdminModel;
import com.example.firefighters.models.UserFireFighterModel;
import com.example.firefighters.models.UserModel;
import com.example.firefighters.tools.FirebaseManager;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

public class UserRepository {

    private static UserRepository instance;

    private boolean isLoadingUser;

    private FirebaseUser currentAuthUser;
    private final UserModel currentUserModel = new UserModel();
    private final UserFireFighterModel currentUserFireFighterModel = new UserFireFighterModel();
    private final UserAdminModel currentUserAdminModel = new UserAdminModel();

    public static UserRepository getInstance(){
        if (instance == null)
            instance = new UserRepository();
        return instance;
    }

    public MutableLiveData<Boolean> bindIsLoadingUser(){
        MutableLiveData<Boolean> mutableLiveData = new MutableLiveData<>();
        mutableLiveData.setValue(isLoadingUser);
        return mutableLiveData;
    }
    public MutableLiveData<FirebaseUser> bindCurrentAuthUser(){
        MutableLiveData<FirebaseUser> mutableLiveData = new MutableLiveData<>();
        mutableLiveData.setValue(currentAuthUser);
        return mutableLiveData;
    }
    public MutableLiveData<UserModel> bindUser(){
        MutableLiveData<UserModel> mutableLiveData = new MutableLiveData<>();
        mutableLiveData.setValue(currentUserModel);
        return mutableLiveData;
    }
    public MutableLiveData<UserFireFighterModel> bindFireFighter(){
        MutableLiveData<UserFireFighterModel> mutableLiveData = new MutableLiveData<>();
        mutableLiveData.setValue(currentUserFireFighterModel);
        return mutableLiveData;
    }
    public MutableLiveData<UserAdminModel> bindAdmin(){
        MutableLiveData<UserAdminModel> mutableLiveData = new MutableLiveData<>();
        mutableLiveData.setValue(currentUserAdminModel);
        return mutableLiveData;
    }

    //Sign in with mail and password
    public void signInUser(String userMail, String userPassword){
        if (userMail == null || userPassword == null)
            return;
        if (userMail.length() <= 0 || userPassword.length() <= 0)
            return;
        FirebaseManager.getInstance().getFirebaseAuthInstance().signInWithEmailAndPassword(userMail, userPassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                        isLoadingUser = false;
                        if (task.isSuccessful()){
                            //
                        }else {
                            //
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        isLoadingUser = false;
                    }
                });
    }
    //Create new user
    public void createNewUser(String userName, String userMail, String userPassword){
        isLoadingUser = true;
        FirebaseManager.getInstance().getFirebaseAuthInstance().createUserWithEmailAndPassword(userMail, userPassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            currentUserModel.setUserName(userName);
                            currentUserModel.setMail(userMail);
                            uploadUserProfile(currentUserModel);
                        }else {
                            isLoadingUser = false;
                        }
                    }
                });
    }
    //Reset password
    public void resetPasswordWithMail(String userMail){
        isLoadingUser = true;
        FirebaseManager.getInstance().getFirebaseAuthInstance().sendPasswordResetEmail(userMail)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        isLoadingUser = false;
                    }
                });
    }
    //Create user profile with username and password
    public void uploadUserProfile(UserModel userModel) {
        FirebaseManager.getInstance().getFirebaseFirestoreInstance().collection("users").add(userModel)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DocumentReference> task) {
                        isLoadingUser = false;
                        if (task.isSuccessful()){
                            //Say to the user that is successful
                        }else {
                            //Say to the user that is failed
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        isLoadingUser = false;
                        //Say to the user that is failed
                    }
                });
    }
}
