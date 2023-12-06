package com.example.firefighters.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.firefighters.models.WaterPointModel
import com.example.firefighters.utils.ConstantsValues
import com.example.firefighters.utils.FirebaseUtils
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot

class WaterPointRepository {
    val allWaterPointsQuery: LiveData<QuerySnapshot>
        get() {
            val data = MutableLiveData<QuerySnapshot>()
            FirebaseUtils.instance?.firebaseFirestoreInstance
                ?.collection(ConstantsValues.WATER_POINTS_COLLECTION)
                ?.get()
                ?.addOnCompleteListener { task -> data.value = task.result }
            return data
        }

    fun getWaterPointsQuery(
        lastDocument: DocumentSnapshot?,
        limitCount: Int
    ): LiveData<QuerySnapshot> {
        val data = MutableLiveData<QuerySnapshot>()
        if (lastDocument != null) {
            FirebaseUtils.instance?.firebaseFirestoreInstance
                ?.collection(ConstantsValues.WATER_POINTS_COLLECTION)
                ?.startAfter(lastDocument)
                ?.limit(limitCount.toLong())
                ?.get()
                ?.addOnCompleteListener { task -> data.value = task.result }
        } else {
            FirebaseUtils.instance?.firebaseFirestoreInstance
                ?.collection(ConstantsValues.WATER_POINTS_COLLECTION)
                ?.limit(limitCount.toLong())
                ?.get()
                ?.addOnCompleteListener { task -> data.value = task.result }
        }
        return data
    }

    val waterPointsQuerySnapshot: LiveData<QuerySnapshot?>
        get() {
            val data = MutableLiveData<QuerySnapshot?>()
            FirebaseUtils.instance?.firebaseFirestoreInstance
                ?.collection(ConstantsValues.WATER_POINTS_COLLECTION)
                ?.addSnapshotListener { value, _ -> data.value = value }
            return data
        }

    fun saveWaterPoint(waterPointModel: WaterPointModel): LiveData<Int> {
        val data = MutableLiveData<Int>()
        FirebaseUtils.instance?.firebaseFirestoreInstance
            ?.collection(ConstantsValues.WATER_POINTS_COLLECTION)
            ?.orderBy("id", Query.Direction.DESCENDING)
            ?.limit(1)
            ?.get()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    var lastId: Long = 0
                    if ((task.result?.size() ?: 0) > 0) {
                        val wp = task.result?.documents?.get(0)?.toObject(
                            WaterPointModel::class.java
                        )
                        if (wp != null) lastId = wp.id
                    }
                    waterPointModel.id = lastId + 1
                    FirebaseUtils.instance?.firebaseFirestoreInstance
                        ?.collection(ConstantsValues.WATER_POINTS_COLLECTION)
                        ?.add(waterPointModel)
                        ?.addOnCompleteListener {
                            if (it.isSuccessful) {
                                data.setValue(1)
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

    fun deleteWaterPoint(waterPointModel: WaterPointModel): LiveData<Int> {
        val data = MutableLiveData<Int>()
        FirebaseUtils.instance?.firebaseFirestoreInstance
            ?.collection(ConstantsValues.WATER_POINTS_COLLECTION)
            ?.whereEqualTo("id", waterPointModel.id)
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

    fun updateWaterPoint(waterPointModel: WaterPointModel): LiveData<Int> {
        val data = MutableLiveData<Int>()
        FirebaseUtils.instance?.firebaseFirestoreInstance
            ?.collection(ConstantsValues.WATER_POINTS_COLLECTION)
            ?.whereEqualTo("id", waterPointModel.id)
            ?.get()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result ?: listOf()) {
                        document.reference
                            .update(
                                "senderMail", waterPointModel.senderMail,
                                "estimatedQte", waterPointModel.estimatedQte,
                                "longitude", waterPointModel.longitude,
                                "latitude", waterPointModel.latitude,
                                "sourceType", waterPointModel.sourceType
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

    fun getWaterPointModel(id: Int): LiveData<WaterPointModel?> {
        val data = MutableLiveData<WaterPointModel?>()
        FirebaseUtils.instance?.firebaseFirestoreInstance
            ?.collection(ConstantsValues.WATER_POINTS_COLLECTION)
            ?.whereEqualTo("id", id)
            ?.get()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if ((task.result?.size() ?: 0) > 0) {
                        val waterPointModel = task.result?.documents?.get(0)?.toObject(
                            WaterPointModel::class.java
                        )
                        data.setValue(waterPointModel)
                    } else {
                        data.setValue(null)
                    }
                } else {
                    data.setValue(null)
                }
            }
        return data
    }
}
