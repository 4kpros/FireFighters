package com.example.firefighters.tools;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

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
