package com.example.firefighters.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.firefighters.models.UnitModel
import com.example.firefighters.utils.ConstantsValues
import com.example.firefighters.utils.FirebaseUtils
import com.google.firebase.firestore.QuerySnapshot

class UnitRepository {
    fun getUnitModel(unitName: String?): LiveData<UnitModel?> {
        val data = MutableLiveData<UnitModel?>()
        if (unitName.isNullOrEmpty()) {
            data.setValue(null)
        } else {
            FirebaseUtils.instance?.firebaseFirestoreInstance
                ?.collection(ConstantsValues.UNIT_COLLECTION)
                ?.whereEqualTo("unitName", unitName)
                ?.get()
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        if ((task.result?.size() ?: 0) > 0) {
                            val unitModel = task.result?.documents?.get(0)?.toObject(
                                UnitModel::class.java
                            )
                            data.setValue(unitModel)
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

    val unitsQuery: LiveData<QuerySnapshot>
        get() {
            val data = MutableLiveData<QuerySnapshot>()
            FirebaseUtils.instance?.firebaseFirestoreInstance
                ?.collection(ConstantsValues.UNIT_COLLECTION)
                ?.get()
                ?.addOnCompleteListener { task -> data.value = task.result }
            return data
        }

    fun saveUnit(unitModel: UnitModel): LiveData<Int> {
        val data = MutableLiveData<Int>()
        FirebaseUtils.instance?.firebaseFirestoreInstance
            ?.collection(ConstantsValues.UNIT_COLLECTION)
            ?.whereEqualTo("unitName", unitModel.unitName)
            ?.get()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if ((task.result?.size() ?: 0) > 0) {
                        data.setValue(-1)
                    } else {
                        FirebaseUtils.instance?.firebaseFirestoreInstance
                            ?.collection(ConstantsValues.UNIT_COLLECTION)
                            ?.add(unitModel)
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
        return data
    }
}
