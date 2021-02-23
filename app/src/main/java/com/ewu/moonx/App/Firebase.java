package com.ewu.moonx.App;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class Firebase {

    public static DatabaseReference RealTimeRef(String parent) {
        return FirebaseDatabase.getInstance().getReference(parent);
    }

    public static CollectionReference FireCloudRef(String parent) {
        return FirebaseFirestore.getInstance().collection(parent);
    }
}
