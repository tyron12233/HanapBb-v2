package com.tyron.hanapbb;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tyron.hanapbb.messenger.AndroidUtilities;
import com.tyron.hanapbb.messenger.UserConfig;
import com.tyron.hanapbb.ui.HomeActivity;
import com.tyron.hanapbb.ui.models.MessagesModel;

import java.util.ArrayList;
import java.util.List;

public class BackgroundUpdateService extends Service {

    private List<String> keys = new ArrayList<>();
    private FirebaseDatabase firebase = FirebaseDatabase.getInstance();
    private DatabaseReference convRef = firebase.getReference("conversations");
    private DatabaseReference chatRef = firebase.getReference("chats");
    private ValueEventListener[] listeners;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        AndroidUtilities.showToast("Service is running");

        convRef.child(UserConfig.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listeners = new ValueEventListener[(int) snapshot.getChildrenCount()];
                for(DataSnapshot ds: snapshot.getChildren()){
                    String chat_id = ds.child("chat_id").getValue(String.class);
                    keys.add(chat_id);
                }
                retrieveConversations();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void retrieveConversations() {
        for(int i = 0; i < listeners.length; i++){
            listeners[i] = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot ds : snapshot.getChildren()){
                        MessagesModel model = ds.getValue(MessagesModel.class);

                        showNotification(model);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };
        }
        for(int i = 0; i <listeners.length; i++){
            chatRef.child(keys.get(i)).child("messages").orderByKey().limitToLast(1).addValueEventListener(listeners[i]);
        }
    }

    private void showNotification(MessagesModel model) {

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("HanapBb",
                    "Messages",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Notifications for messages");
            mNotificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder b = new NotificationCompat.Builder(getApplicationContext(),"HanapBB" )
                .setContentText(model.getMessage())
                .setSmallIcon(R.drawable.ic_baseline_add_24)
                .setContentTitle("New message")
                .setAutoCancel(true);

        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        b.setContentIntent(pi);

        mNotificationManager.notify(0,b.build());
    }
}
