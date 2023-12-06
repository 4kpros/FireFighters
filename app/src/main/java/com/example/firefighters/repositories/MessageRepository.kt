package com.example.firefighters.repositories

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.firefighters.models.EmergencyModel
import com.example.firefighters.models.MessageModel
import com.example.firefighters.utils.ConstantsValues
import com.example.firefighters.utils.FirebaseUtils
import com.google.firebase.firestore.Query
import java.io.File
import java.util.UUID

class MessageRepository {
    //Sign in with mail and password
    fun loadMessageModel(messageId: Int): LiveData<MessageModel?> {
        val data = MutableLiveData<MessageModel?>()
        FirebaseUtils.instance?.firebaseFirestoreInstance
            ?.collection(ConstantsValues.MESSAGE_COLLECTION)
            ?.whereEqualTo("id", messageId)
            ?.get()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if ((task.result?.size() ?: 0) > 0) {
                        val message = task.result?.documents?.get(0)?.toObject(
                            MessageModel::class.java
                        )
                        data.setValue(message)
                    } else {
                        data.setValue(null)
                    }
                } else {
                    data.setValue(null)
                }
            }
        return data
    }

    fun saveDataToStorage(parent: String, path: String?): LiveData<String?> {
        val data = MutableLiveData<String?>()
        //Save the data
        if (!path.isNullOrEmpty()) {
            val randomKey = UUID.randomUUID().toString()
            val storageReference =
                FirebaseUtils.instance?.firebaseStorageInstance
                    ?.reference?.child(parent + randomKey)
            val imageUri = Uri.fromFile(File(path))
            storageReference?.putFile(imageUri)
                ?.addOnSuccessListener { taskSnapshot ->
                    taskSnapshot.storage
                        .downloadUrl
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val dataUrl = task.result.toString()
                                data.setValue(dataUrl)
                            } else {
                                data.setValue(null)
                            }
                        }
                }
                ?.addOnFailureListener { data.value = null }
        } else {
            data.setValue("")
        }
        return data
    }

    fun saveMessage(messageModel: MessageModel): LiveData<Int> {
        val data = MutableLiveData<Int>()
        FirebaseUtils.instance?.firebaseFirestoreInstance
            ?.collection(ConstantsValues.MESSAGE_COLLECTION)
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
                    messageModel.id = lastId + 1
                    val finalLastId = lastId + 1
                    FirebaseUtils.instance?.firebaseFirestoreInstance
                        ?.collection(ConstantsValues.MESSAGE_COLLECTION)
                        ?.add(messageModel)
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

    fun deleteMessage(messageId: Int): LiveData<Int> {
        val data = MutableLiveData<Int>()
        FirebaseUtils.instance?.firebaseFirestoreInstance
            ?.collection(ConstantsValues.MESSAGE_COLLECTION)
            ?.whereEqualTo("id", messageId)
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

    fun updateMessage(messageModel: MessageModel): LiveData<Int> {
        val data = MutableLiveData<Int>()
        FirebaseUtils.instance?.firebaseFirestoreInstance
            ?.collection(ConstantsValues.MESSAGE_COLLECTION)
            ?.whereEqualTo("id", messageModel.id)
            ?.get()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result ?: listOf()) {
                        document.reference
                            .update(
                                "message", messageModel.message,
                                "imagesSrc", messageModel.imagesSrc,
                                "videoSrc", messageModel.videoSrc,
                                "audioSrc", messageModel.audioSrc
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
