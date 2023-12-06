package com.example.firefighters.viewmodels.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.firefighters.models.UserModel
import com.example.firefighters.repositories.UserRepository
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot

class UserViewModel(private val mRepository: UserRepository) : ViewModel() {

    fun loadAllFireFightersWorkingUnitQuery(unit: String?): LiveData<QuerySnapshot> {
        return mRepository.loadAllFireFightersWorkingUnitQuery(unit)
    }

    fun loadFireFighters(
        lastDocument: DocumentSnapshot?,
        limitCount: Int
    ): LiveData<QuerySnapshot> {
        return mRepository.loadFireFightersQuery(lastDocument, limitCount)
    }

    fun loadFireFightersSnapshot(): LiveData<QuerySnapshot?> {
        return mRepository.loadFireFightersQuerySnapshot()
    }

    fun saveFireFighter(firefighterModel: UserModel?): LiveData<Int> {
        return mRepository.saveFireFighter(firefighterModel)
    }

    fun updateFireFighter(firefighterModel: UserModel?): LiveData<Int> {
        return mRepository.updateFireFighter(firefighterModel!!)
    }

    fun deleteFireFighter(firefighterModel: UserModel?): LiveData<Int> {
        return mRepository.deleteFireFighter(firefighterModel!!)
    }

    //Sign in with mail and password
    fun signInUser(userMail: String?, userPassword: String?): LiveData<Int> {
        return mRepository.signInUser(userMail, userPassword)
    }

    //Create new user
    fun createNewUser(
        userName: String?,
        userMail: String?,
        userPassword: String?,
        isFirefighter: Boolean
    ): LiveData<Int> {
        return mRepository.createNewUser(userName, userMail, userPassword, isFirefighter)
    }

    //Load to my page
    fun loadUserModel(userMail: String?): LiveData<UserModel?> {
        return mRepository.loadUserModel(userMail)
    }

    //Reset password
    fun resetPasswordWithMail(userMail: String?): LiveData<Int> {
        return mRepository.resetPasswordWithMail(userMail)
    }

    //Disconnect user
    fun logOut(): LiveData<Int> {
        return mRepository.logOut()
    }

    class Factory(
        private val repository: UserRepository
    ) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return UserViewModel(repository) as T
        }
    }
}
