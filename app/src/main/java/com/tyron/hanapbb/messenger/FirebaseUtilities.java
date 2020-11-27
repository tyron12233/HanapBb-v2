package com.tyron.hanapbb.messenger;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseUtilities {
    static FirebaseDatabase database = FirebaseDatabase.getInstance();
    public static DatabaseReference userRef = database.getReference("users");

    public static ValueEventListener listener;

    public static void removeFromPath(String path){
        DatabaseReference ref = database.getReference(path);
        ref.removeValue();
    }
}
