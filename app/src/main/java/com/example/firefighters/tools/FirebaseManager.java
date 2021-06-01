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

    public static FirebaseManager getInstance(){
        if (instance == null){
            instance = new FirebaseManager();
            instance.firebaseAuth = FirebaseAuth.getInstance();
            instance.fireStoreDb = FirebaseFirestore.getInstance();
            instance.firebaseStorage = FirebaseStorage.getInstance();
        }
        return instance;
    }

    public FirebaseAuth getFirebaseAuthInstance(){
        return firebaseAuth;
    }
    public FirebaseFirestore getFirebaseFirestoreInstance(){
        return fireStoreDb;
    }
    public FirebaseStorage getFirebaseStorageInstance(){
        return firebaseStorage;
    }

    public FirebaseUser getCurrentAuthUser(){
        return firebaseAuth.getCurrentUser();
    }
    public void signInUser(String userMail, String userPassword){
        firebaseAuth.signInWithEmailAndPassword(userMail, userPassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                        //
                    }
                })
                .addOnCanceledListener(new OnCanceledListener() {
                    @Override
                    public void onCanceled() {
                        //
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        //
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        //
                    }
                });
    }
    public void createNewUser(String userMail, String userPassword){
        firebaseAuth.createUserWithEmailAndPassword(userMail, userPassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                        //
                    }
                })
                .addOnCanceledListener(new OnCanceledListener() {
                    @Override
                    public void onCanceled() {
                        //
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        //
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        //
                    }
                });
    }
    public void resetPasswordWithMail(String userMail){
        firebaseAuth.sendPasswordResetEmail(userMail)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        //
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        //
                    }
                })
                .addOnCanceledListener(new OnCanceledListener() {
                    @Override
                    public void onCanceled() {
                        //
                    }
                });
    }

    private void loadEmergencies(EventListener<QuerySnapshot> eventListener, int maxLoad){
        fireStoreDb.collection("emergency").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                if (value != null && value.isEmpty()) return;
                //
            }
        });
    }
    private void uploadEmergency(EmergencyModel emergencyModel, Activity activity) {
        fireStoreDb.collection("emergency").add(emergencyModel)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DocumentReference> task) {
                        //
                    }
                })
                .addOnCanceledListener(new OnCanceledListener() {
                    @Override
                    public void onCanceled() {
                        //
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(activity, "Sent", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(activity, "Error !", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void updateEmergency(int emergencyId, EmergencyModel emergencyModel){
        //
    }
//
//    void loadMoreEmergencies(List<DocumentChange> documentChanges, int end){
//        fireStoreDb.collection("Emergency").addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//                if (error != null) {
//                    return;
//                }
//                documentChanges.addAll(value.getDocumentChanges().subList(documentChanges.size(), end));
//                for (DocumentSnapshot doc : value) {
//                    EmergencyModel e,emergencyModel = doc.toObject(EmergencyModel.class);
//                    emergencies.add(emergencyModel);
//                }
//            }
//        });
//    }

//    private void UploadImageToFirebaseStorage(){
//        final String randomKey = UUID.randomUUID().toString();
//        StorageReference profileRef = storageReference.child("images/" + randomKey);
//        Uri imageUri = Uri.fromFile(new File("path to the file"));
//        StorageTask<UploadTask.TaskSnapshot> uploadTask = profileRef.putFile(imageUri)
//                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                        profileRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Uri> task) {
//                                String profileImageUrl = Objects.requireNonNull(task.getResult()).toString();
//                                // Set now the profile image
//                            }
//                        });
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        //Show failure message
//                    }
//                })
//                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
//                        double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
//                        //Set the progress
//                    }
//                });
//
//    }

}
