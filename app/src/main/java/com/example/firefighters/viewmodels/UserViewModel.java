package com.example.firefighters.viewmodels;

import android.app.Activity;

import com.example.firefighters.repositories.UserRepository;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class UserViewModel extends ViewModel {

    private MutableLiveData<QuerySnapshot> queryMutableLiveData;
    private MutableLiveData<QuerySnapshot> querySnapshotMutableLiveData;
    private MutableLiveData<Boolean> isLoadingSignInMutableLiveData;
    private MutableLiveData<Boolean> isLoadingSignUpMutableLiveData;
    private MutableLiveData<String> typeUserMutableLiveData;

    private UserRepository repository;

    public void init() {
        if (repository != null)
            return;
        repository = UserRepository.getInstance();
        typeUserMutableLiveData = repository.bindTypeUser();
    }

    public LiveData<String> getTypeUser() {
        return typeUserMutableLiveData;
    }

    //Sign in with mail and password
    public LiveData<Integer> signInUser(Activity activity, String userMail, String userPassword) {
        return repository.signInUser(activity, userMail, userPassword);
    }

    public LiveData<String> loadTypeUser() {
        return repository.loadTypeUser();
    }

    //Create new user
    public LiveData<Integer> createNewUser(Activity activity, String userName, String userMail, String userPassword) {
        return repository.createNewUser(activity, userName, userMail, userPassword);
    }

    //Reset password
    public LiveData<Integer> resetPasswordWithMail(Activity activity, String userMail) {
        return repository.resetPasswordWithMail(activity, userMail);
    }
    //Disconnect user
    public LiveData<Integer> logOut() {
        return repository.logOut();
    }

}
