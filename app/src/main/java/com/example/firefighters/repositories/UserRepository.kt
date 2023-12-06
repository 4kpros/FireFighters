package com.example.firefighters.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.firefighters.models.UserModel
import com.example.firefighters.utils.ConstantsValues
import com.example.firefighters.utils.FirebaseUtils
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot

class UserRepository {
    //Sign in with mail and password
    fun signInUser(userMail: String?, userPassword: String?): LiveData<Int> {
        val data = MutableLiveData<Int>()
        if (userMail == null || userPassword == null || userMail.isEmpty() || userPassword.isEmpty()) {
            data.setValue(-1)
        } else {
            FirebaseUtils.instance?.firebaseAuthInstance
                ?.signInWithEmailAndPassword(
                userMail,
                userPassword
            )
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        data.setValue(1)
                    } else {
                        data.setValue(-1)
                    }
                }
        }
        return data
    }

    //Create new user
    fun createNewUser(
        userName: String?,
        userMail: String?,
        userPassword: String?,
        isFirefighter: Boolean
    ): LiveData<Int> {
        val data = MutableLiveData<Int>()
        if (userMail == null || userPassword == null || userMail.isEmpty() || userPassword.isEmpty()) {
            data.setValue(-1)
        } else {
            FirebaseUtils.instance?.firebaseAuthInstance
                ?.createUserWithEmailAndPassword(
                userMail,
                userPassword
            )
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        if (userMail == ConstantsValues.ADMIN_EMAIL) {
                            data.setValue(2)
                        } else {
                            FirebaseUtils.instance?.firebaseFirestoreInstance
                                ?.collection(ConstantsValues.USERS_COLLECTION)
                                ?.whereEqualTo("mail", userMail)
                                ?.get()
                                ?.addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        if ((it.result?.size() ?: 0) > 0) {
                                            val user = it.result?.documents?.get(0)?.toObject(
                                                UserModel::class.java
                                            )
                                            if (user != null && user.isFireFighter) {
                                                data.setValue(1)
                                            } else if (user != null) {
                                                data.value = -1
                                            }
                                        } else {
                                            val currentUserModel = UserModel()
                                            currentUserModel.userName = userName
                                            currentUserModel.isFireFighter = isFirefighter
                                            currentUserModel.mail = userMail
                                            FirebaseUtils.instance?.firebaseFirestoreInstance
                                                ?.collection(ConstantsValues.USERS_COLLECTION)
                                                ?.add(currentUserModel)
                                                ?.addOnCompleteListener {
                                                    if (it.isSuccessful) {
                                                        data.setValue(1)
                                                    } else {
                                                        data.setValue(-1)
                                                    }
                                                }
                                        }
                                    } else {
                                        data.setValue(-1)
                                    }
                                }
                        }
                    } else {
                        data.setValue(-1)
                    }
                }
        }
        return data
    }

    fun loadUserModel(userMail: String?): LiveData<UserModel?> {
        val data = MutableLiveData<UserModel?>()
        if (userMail.isNullOrEmpty()) {
            data.setValue(null)
        } else {
            FirebaseUtils.instance?.firebaseFirestoreInstance
                ?.collection(ConstantsValues.USERS_COLLECTION)
                ?.whereEqualTo("mail", userMail)
                ?.get()
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        if ((task.result?.size() ?: 0) > 0) {
                            data.setValue(
                                task.result?.documents?.get(0)?.toObject(
                                    UserModel::class.java
                                )
                            )
                        } else {
                            data.setValue(null)
                        }
                    } else {
                        data.setValue(null)
                    }
                }
        }
        return data
    }

    fun loadAllFireFightersWorkingUnitQuery(unit: String?): LiveData<QuerySnapshot> {
        val data = MutableLiveData<QuerySnapshot>()
        FirebaseUtils.instance?.firebaseFirestoreInstance
            ?.collection(ConstantsValues.USERS_COLLECTION)
            ?.whereEqualTo("unit", unit)
            ?.get()
            ?.addOnCompleteListener { task -> data.value = task.result }
        return data
    }

    //Reset password
    fun resetPasswordWithMail(userMail: String?): LiveData<Int> {
        val data = MutableLiveData<Int>()
        FirebaseUtils.instance?.firebaseAuthInstance
            ?.sendPasswordResetEmail(userMail!!)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    data.setValue(1)
                } else {
                    data.setValue(-1)
                }
            }
        return data
    }

    fun logOut(): LiveData<Int> {
        val data = MutableLiveData<Int>()
        FirebaseUtils.instance?.firebaseAuthInstance
            ?.signOut()
        data.value = 1
        return data
    }

    fun loadFireFightersQuery(
        lastDocument: DocumentSnapshot?,
        limitCount: Int
    ): LiveData<QuerySnapshot> {
        val data = MutableLiveData<QuerySnapshot>()
        if (lastDocument != null) {
            FirebaseUtils.instance?.firebaseFirestoreInstance
                ?.collection(ConstantsValues.USERS_COLLECTION)
                ?.whereEqualTo("fireFighter", true)
                ?.startAfter(lastDocument)
                ?.limit(limitCount.toLong())
                ?.get()
                ?.addOnCompleteListener { task -> data.value = task.result }
        } else {
            FirebaseUtils.instance?.firebaseFirestoreInstance
                ?.collection(ConstantsValues.USERS_COLLECTION)
                ?.whereEqualTo("fireFighter", true)
                ?.limit(limitCount.toLong())
                ?.get()
                ?.addOnCompleteListener { task -> data.value = task.result }
        }
        return data
    }

    fun loadFireFightersQuerySnapshot(): LiveData<QuerySnapshot?> {
        val data = MutableLiveData<QuerySnapshot?>()
        FirebaseUtils.instance?.firebaseFirestoreInstance
            ?.collection(ConstantsValues.USERS_COLLECTION)
            ?.whereEqualTo("fireFighter", true)
            ?.addSnapshotListener { value, _ -> data.value = value }
        return data
    }

    fun saveFireFighter(firefighterModel: UserModel?): LiveData<Int> {
        val data = MutableLiveData<Int>()
        FirebaseUtils.instance?.firebaseFirestoreInstance
            ?.collection(ConstantsValues.USERS_COLLECTION)
            ?.add(firefighterModel!!)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    data.setValue(1)
                } else {
                    data.setValue(-1)
                }
            }
        return data
    }

    fun updateFireFighter(firefighterModel: UserModel): LiveData<Int> {
        val data = MutableLiveData<Int>()
        FirebaseUtils.instance?.firebaseFirestoreInstance
            ?.collection(ConstantsValues.EMERGENCIES_COLLECTION)
            ?.whereEqualTo("mail", firefighterModel.mail)
            ?.get()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    data.setValue(1)
                } else {
                    data.setValue(-1)
                }
            }
        return data
    }

    fun deleteFireFighter(firefighterModel: UserModel): LiveData<Int> {
        val data = MutableLiveData<Int>()
        FirebaseUtils.instance?.firebaseFirestoreInstance
            ?.collection(ConstantsValues.USERS_COLLECTION)
            ?.whereEqualTo("fireFighter", true)
            ?.whereEqualTo("mail", firefighterModel.mail)
            ?.get()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result ?: listOf()) {
                        document.reference
                            .delete()
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
                                    data.setValue(1)
                                } else {
                                    data.setValue(-1)
                                }
                            }
                    }
                } else {
                    data.setValue(-1)
                }
            }
        return data
    }
}
