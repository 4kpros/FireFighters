package com.example.firefighters.viewmodels;

import android.app.Activity;

import com.example.firefighters.models.UserModel;
import com.example.firefighters.repositories.UserRepository;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class UserViewModel extends ViewModel {

    private UserRepository repository;

    public void init() {
        if (repository != null)
            return;
        repository = UserRepository.getInstance();
    }

    public LiveData<QuerySnapshot> loadAllFireFightersWorkingUnitQuery(String unit){
        return repository.loadAllFireFightersWorkingUnitQuery(unit);
    }
    public LiveData<QuerySnapshot> loadFireFighters(DocumentSnapshot lastDocument, int limitCount){
        return repository.loadFireFightersQuery(lastDocument, limitCount);
    }
    public LiveData<QuerySnapshot> loadFireFightersSnapshot(){
        return repository.loadFireFightersQuerySnapshot();
    }
    public LiveData<Integer> saveFireFighter(UserModel firefighterModel) {
        return repository.saveFireFighter(firefighterModel);
    }
    public LiveData<Integer> updateFireFighter(UserModel firefighterModel) {
        return repository.updateFireFighter(firefighterModel);
    }
    public LiveData<Integer> deleteFireFighter(UserModel firefighterModel) {
        return repository.deleteFireFighter(firefighterModel);
    }

    //Sign in with mail and password
    public LiveData<Integer> signInUser(String userMail, String userPassword) {
        return repository.signInUser(userMail, userPassword);
    }
    //Create new user
    public LiveData<Integer> createNewUser(String userName, String userMail, String userPassword, boolean isFirefighter) {
        return repository.createNewUser(userName, userMail, userPassword, isFirefighter);
    }
    //Load to my page
    public LiveData<UserModel> loadUserModel(String userMail) {
        return repository.loadUserModel(userMail);
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
