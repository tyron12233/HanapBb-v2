package com.tyron.hanapbb.messenger;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseUtilities {
    static FirebaseDatabase database = FirebaseDatabase.getInstance();

    public static void removeFromPath(String path){
        DatabaseReference ref = database.getReference(path);
        ref.removeValue();
    }
}
