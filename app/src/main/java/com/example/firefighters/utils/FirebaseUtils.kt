package com.example.firefighters.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class FirebaseUtils {
    var firebaseAuthInstance: FirebaseAuth? = null
        private set
    var firebaseFirestoreInstance: FirebaseFirestore? = null
        private set
    var firebaseStorageInstance: FirebaseStorage? = null
        private set
    val currentAuthUser: FirebaseUser?
        get() = firebaseAuthInstance!!.currentUser

    companion object {
        @JvmStatic
        var instance: FirebaseUtils? = null
            get() {
                if (field == null) {
                    field = FirebaseUtils()
                    field!!.firebaseAuthInstance = FirebaseAuth.getInstance()
                    field!!.firebaseFirestoreInstance = FirebaseFirestore.getInstance()
                    field!!.firebaseStorageInstance = FirebaseStorage.getInstance()
                }
                return field
            }
            private set
    }
}
