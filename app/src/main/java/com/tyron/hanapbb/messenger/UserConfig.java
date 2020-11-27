package com.tyron.hanapbb.messenger;

import android.os.Message;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuth.AuthStateListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tyron.hanapbb.ui.models.MessagesModel;
import com.tyron.hanapbb.ui.models.UserModel;

public class UserConfig {
    private static FirebaseAuth auth = FirebaseAuth.getInstance();
    private static FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static DatabaseReference myRef = database.getReference("users");

    public static UserModel config;

    public static boolean isLoggedIn(){
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    };

    public static void signOut(){
        auth.signOut();
    }

    static{
        if(auth.getUid() != null) {
            myRef.child(getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    UserModel model = snapshot.getValue(UserModel.class);
                    config = model;
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    public static String getUid(){
        return auth.getUid();
    }

}
