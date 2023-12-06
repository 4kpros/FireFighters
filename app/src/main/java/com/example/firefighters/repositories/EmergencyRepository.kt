package com.example.firefighters.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.firefighters.models.EmergencyModel
import com.example.firefighters.utils.ConstantsValues
import com.example.firefighters.utils.FirebaseUtils
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot

class EmergencyRepository {
    fun getEmergenciesQuery(
        lastDocument: DocumentSnapshot?,
        lastFilter: String?,
        lastOrder: Query.Direction?,
        limitCount: Int
    ): LiveData<QuerySnapshot> {
        val tempLastFilter: String = lastFilter ?: ConstantsValues.FILTER_NAME
        val tempLastOrder: Query.Direction = lastOrder ?: Query.Direction.DESCENDING
        val data = MutableLiveData<QuerySnapshot>()
        if (lastDocument != null) {
            FirebaseUtils.instance?.firebaseFirestoreInstance
                ?.collection(ConstantsValues.EMERGENCIES_COLLECTION)
                ?.orderBy(tempLastFilter, tempLastOrder)
                ?.startAfter(lastDocument)
                ?.limit(limitCount.toLong())
                ?.get()
                ?.addOnCompleteListener { task -> data.value = task.result }
        } else {
            FirebaseUtils.instance?.firebaseFirestoreInstance
                ?.collection(ConstantsValues.EMERGENCIES_COLLECTION)
                ?.orderBy(tempLastFilter, tempLastOrder)
                ?.limit(limitCount.toLong())
                ?.get()
                ?.addOnCompleteListener { task -> data.value = task.result }
        }
        return data
    }

    fun getEmergenciesQuerySnapshot(
        lastFilter: String?,
        lastOrder: Query.Direction?
    ): LiveData<QuerySnapshot?> {
        val tempLastFilter: String = lastFilter ?: ConstantsValues.FILTER_NAME
        val tempLastOrder: Query.Direction = lastOrder ?: Query.Direction.DESCENDING
        val data = MutableLiveData<QuerySnapshot?>()
        FirebaseUtils.instance?.firebaseFirestoreInstance
            ?.collection(ConstantsValues.EMERGENCIES_COLLECTION)
            ?.orderBy(tempLastFilter, tempLastOrder)
            ?.addSnapshotListener { value, _ -> data.value = value }
        return data
    }

    fun getLastQuerySnapshot(
        lastFilter: String?,
        lastOrder: Query.Direction?
    ): LiveData<QuerySnapshot?> {
        val tempLastFilter: String = lastFilter ?: ConstantsValues.FILTER_NAME
        val tempLastOrder: Query.Direction = lastOrder ?: Query.Direction.DESCENDING
        val data = MutableLiveData<QuerySnapshot?>()
        FirebaseUtils.instance?.firebaseFirestoreInstance
            ?.collection(ConstantsValues.EMERGENCIES_COLLECTION)
            ?.orderBy(tempLastFilter, tempLastOrder)
            ?.limit(1)
            ?.addSnapshotListener { value, _ -> data.value = value }
        return data
    }

    fun getEmergencyWorkingOnModel(currentUnit: String?): LiveData<EmergencyModel?> {
        val data = MutableLiveData<EmergencyModel?>()
        if (currentUnit.isNullOrEmpty()) {
            data.setValue(null)
        } else {
            FirebaseUtils.instance?.firebaseFirestoreInstance
                ?.collection(ConstantsValues.EMERGENCIES_COLLECTION)
                ?.whereEqualTo("status", ConstantsValues.WORKING)
                ?.whereEqualTo("currentUnit", currentUnit)
                ?.get()
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        if ((task.result?.documents?.size ?: 0) > 0) {
                            val emergencyModel = task.result?.documents?.get(0)?.toObject(
                                EmergencyModel::class.java
                            )
                            data.setValue(emergencyModel)
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

    fun getEmergencyWorkingOn(currentUnit: String?): LiveData<QuerySnapshot?> {
        val data = MutableLiveData<QuerySnapshot?>()
        if (currentUnit.isNullOrEmpty()) {
            data.setValue(null)
        } else {
            FirebaseUtils.instance?.firebaseFirestoreInstance
                ?.collection(ConstantsValues.EMERGENCIES_COLLECTION)
                ?.whereEqualTo("currentUnit", currentUnit)
                ?.whereEqualTo("status", ConstantsValues.WORKING)
                ?.addSnapshotListener { value, _ -> data.value = value }
        }
        return data
    }

    fun saveEmergency(emergencyModel: EmergencyModel): LiveData<Int> {
        val data = MutableLiveData<Int>()
        FirebaseUtils.instance?.firebaseFirestoreInstance
            ?.collection(ConstantsValues.EMERGENCIES_COLLECTION)
            ?.orderBy("id", Query.Direction.DESCENDING)
            ?.limit(1)
            ?.get()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    var lastId: Long = 0
                    if ((task.result?.size() ?: 0) > 0) {
                        val em = task.result?.documents?.get(0)?.toObject(
                            EmergencyModel::class.java
                        )
                        if (em != null) lastId = em.id
                    }
                    emergencyModel.id = lastId + 1
                    val finalLastId = lastId + 1
                    FirebaseUtils.instance?.firebaseFirestoreInstance
                        ?.collection(ConstantsValues.EMERGENCIES_COLLECTION)
                        ?.add(emergencyModel)
                        ?.addOnCompleteListener {
                            if (it.isSuccessful) {
                                data.setValue(finalLastId.toInt())
                            } else {
                                data.setValue(-1)
                            }
                        }
                } else {
                    data.setValue(-1)
                }
            }
        return data
    }

    fun deleteEmergency(emergencyModel: EmergencyModel): LiveData<Int> {
        val data = MutableLiveData<Int>()
        FirebaseUtils.instance?.firebaseFirestoreInstance
            ?.collection(ConstantsValues.EMERGENCIES_COLLECTION)
            ?.whereEqualTo("id", emergencyModel.id)
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

    fun updateEmergency(emergencyModel: EmergencyModel): LiveData<Int> {
        val data = MutableLiveData<Int>()
        FirebaseUtils.instance?.firebaseFirestoreInstance
            ?.collection(ConstantsValues.EMERGENCIES_COLLECTION)
            ?.whereEqualTo("id", emergencyModel.id)
            ?.get()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result ?: listOf()) {
                        document.reference
                            .update(
                                "senderMail", emergencyModel.senderMail,
                                "messageId", emergencyModel.messageId,
                                "longitude", emergencyModel.longitude,
                                "latitude", emergencyModel.latitude,
                                "gravity", emergencyModel.gravity,
                                "status", emergencyModel.status,
                                "currentUnit", emergencyModel.currentUnit
                            )
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
