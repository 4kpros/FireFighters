package com.example.firefighters.tools;

import android.app.Activity;
import android.widget.Toast;

import com.example.firefighters.models.EmergencyModel;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class FirebaseManager {

    private static FirebaseManager instance;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore fireStoreDb;
    private FirebaseStorage firebaseStorage;

    public static FirebaseManager getInstance() {
        if (instance == null) {
            instance = new FirebaseManager();
            instance.firebaseAuth = FirebaseAuth.getInstance();
            instance.fireStoreDb = FirebaseFirestore.getInstance();
            instance.firebaseStorage = FirebaseStorage.getInstance();
        }
        return instance;
    }

    public FirebaseAuth getFirebaseAuthInstance() {
        return firebaseAuth;
    }

    public FirebaseFirestore getFirebaseFirestoreInstance() {
        return fireStoreDb;
    }

    public FirebaseStorage getFirebaseStorageInstance() {
        return firebaseStorage;
    }

    public FirebaseUser getCurrentAuthUser() {
        return firebaseAuth.getCurrentUser();
    }
}
