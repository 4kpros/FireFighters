package com.example.firefighters.repositories;

import android.net.Uri;
import android.widget.Toast;

import com.example.firefighters.models.EmergencyModel;
import com.example.firefighters.models.MessageModel;
import com.example.firefighters.models.MessageModel;
import com.example.firefighters.tools.ConstantsValues;
import com.example.firefighters.tools.FirebaseManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Objects;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class MessageRepository {

    private static MessageRepository instance;

    public static MessageRepository getInstance() {
        if (instance == null)
            instance = new MessageRepository();
        return instance;
    }

    //Sign in with mail and password
    public LiveData<MessageModel> loadMessageModel(int messageId) {
        MutableLiveData<MessageModel> data = new MutableLiveData<>();
        FirebaseManager.getInstance().getFirebaseFirestoreInstance()
                .collection(ConstantsValues.MESSAGE_COLLECTION)
                .whereEqualTo("id", messageId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().size() > 0) {
                                MessageModel message = task.getResult().getDocuments().get(0).toObject(MessageModel.class);
                                data.setValue(message);
                            } else {
                                data.setValue(null);
                            }
                        } else {
                            data.setValue(null);
                        }
                    }
                });
        return data;
    }

    public LiveData<String> saveDataToStorage(String parent, String path){
        MutableLiveData<String> data = new MutableLiveData<>();
        //Save the data
        if (path != null && !path.isEmpty()) {
            final String randomKey = UUID.randomUUID().toString();
            StorageReference storageReference = FirebaseManager.getInstance().getFirebaseStorageInstance().getReference().child(parent + randomKey);
            Uri imageUri = Uri.fromFile(new File(path));
            storageReference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            taskSnapshot.getStorage()
                                    .getDownloadUrl()
                                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull @NotNull Task<Uri> task) {
                                            if (task.isSuccessful()) {
                                                String dataUrl = task.getResult().toString();
                                                data.setValue(dataUrl);
                                            } else {
                                                data.setValue(null);
                                            }
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull @NotNull Exception e) {
                            data.setValue(null);
                        }
                    });
        }else {
            data.setValue("");
        }
        return data;
    }

    public LiveData<Integer> saveMessage(MessageModel messageModel) {
        MutableLiveData<Integer> data = new MutableLiveData<>();
        FirebaseManager.getInstance().getFirebaseFirestoreInstance()
                .collection(ConstantsValues.MESSAGE_COLLECTION)
                .orderBy("id", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            long lastId = 0;
                            if (task.getResult().size() > 0) {
                                EmergencyModel em = task.getResult().getDocuments().get(0).toObject(EmergencyModel.class);
                                if (em != null)
                                    lastId = em.getId();
                            }
                            messageModel.setId(lastId+1);
                            long finalLastId = lastId+1;
                            FirebaseManager.getInstance().getFirebaseFirestoreInstance()
                                    .collection(ConstantsValues.MESSAGE_COLLECTION)
                                    .add(messageModel)
                                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                        @Override
                                        public void onComplete(@NonNull @NotNull Task<DocumentReference> task) {
                                            if (task.isSuccessful()){
                                                data.setValue((int)finalLastId);
                                            }else{
                                                data.setValue(-1);
                                            }
                                        }
                                    });
                        }else {
                            data.setValue(-1);
                        }
                    }
                });
        return data;
    }

    public LiveData<Integer> deleteMessage(int messageId) {
        MutableLiveData<Integer> data = new MutableLiveData<>();
        FirebaseManager.getInstance().getFirebaseFirestoreInstance()
                .collection(ConstantsValues.MESSAGE_COLLECTION)
                .whereEqualTo("id", messageId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (DocumentSnapshot document: task.getResult()) {
                                document.getReference()
                                        .delete()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull @NotNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    data.setValue(1);
                                                }else{
                                                    data.setValue(-1);
                                                }
                                            }
                                        });
                            }
                        }else{
                            data.setValue(-1);
                        }
                    }
                });
        return data;
    }
    public LiveData<Integer> updateMessage(MessageModel messageModel){
        MutableLiveData<Integer> data = new MutableLiveData<>();
        FirebaseManager.getInstance().getFirebaseFirestoreInstance()
                .collection(ConstantsValues.MESSAGE_COLLECTION)
                .whereEqualTo("id", messageModel.getId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (DocumentSnapshot document: task.getResult()) {
                                document.getReference()
                                        .update("message", messageModel.getMessage(),
                                                "imagesSrc", messageModel.getImagesSrc(),
                                                "videoSrc", messageModel.getVideoSrc(),
                                                "audioSrc", messageModel.getAudioSrc()
                                        )
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull @NotNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    data.setValue(1);
                                                }else{
                                                    data.setValue(-1);
                                                }
                                            }
                                        });
                            }
                        }else{
                            data.setValue(-1);
                        }
                    }
                });
        return data;
    }

}
