package com.example.firefighters.viewmodels;

import com.example.firefighters.models.UserAdminModel;
import com.example.firefighters.models.UserFireFighterModel;
import com.example.firefighters.models.UserModel;
import com.example.firefighters.repositories.UserRepository;
import com.google.firebase.auth.FirebaseUser;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class UserViewModel extends ViewModel {

    private MutableLiveData<Boolean> isLoadingUserMutableLiveData;

    private MutableLiveData<FirebaseUser> currentAuthUserMutableLiveData;
    private MutableLiveData<UserModel> currentUserMutableLiveData;
    private MutableLiveData<UserFireFighterModel> currentUserFireFighterMutableLiveData;
    private MutableLiveData<UserAdminModel> currentUserAdminMutableLiveData;

    private UserRepository repository;

    public void init() {
        if (repository != null)
            return;
        repository = UserRepository.getInstance();

        isLoadingUserMutableLiveData = repository.bindIsLoadingUser();

        currentAuthUserMutableLiveData = repository.bindCurrentAuthUser();
        currentUserMutableLiveData = repository.bindUser();
        currentUserFireFighterMutableLiveData = repository.bindFireFighter();
        currentUserAdminMutableLiveData = repository.bindAdmin();
    }

    public LiveData<Boolean> getIsLoadingUser() {
        return isLoadingUserMutableLiveData;
    }

    public LiveData<FirebaseUser> getCurrentAuthUser() {
        return currentAuthUserMutableLiveData;
    }

    public LiveData<UserModel> getUser() {
        return currentUserMutableLiveData;
    }

    public LiveData<UserFireFighterModel> getFireFighter() {
        return currentUserFireFighterMutableLiveData;
    }

    public LiveData<UserAdminModel> getAdmin() {
        return currentUserAdminMutableLiveData;
    }

    //Sign in with mail and password
    public void signInUser(String userMail, String userPassword) {
        repository.signInUser(userMail, userPassword);
    }

    //Create new user
    public void createNewUser(String userName, String userMail, String userPassword) {
        repository.createNewUser(userName, userMail, userPassword);
    }

    //Reset password
    public void resetPasswordWithMail(String userMail) {
        repository.resetPasswordWithMail(userMail);
    }

}
