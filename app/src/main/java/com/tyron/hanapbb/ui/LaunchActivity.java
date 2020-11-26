package com.tyron.hanapbb.ui;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tyron.hanapbb.R;
import com.tyron.hanapbb.messenger.AndroidUtilities;
import com.tyron.hanapbb.messenger.UserConfig;
import com.tyron.hanapbb.messenger.VersionModel;
import com.tyron.hanapbb.ui.models.UserModel;

import java.util.Objects;

public class LaunchActivity extends AppCompatActivity {

    private FirebaseDatabase _firebase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = _firebase.getReference("updates/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //AndroidUtilities.fillStatusBarHeight(this);

            if(AndroidUtilities.isNetworkAvailable(this)) {
                checkLatestVersion();
            }else {
                //TODO: offline code
            }
    }
    @SuppressWarnings("ConstantConditions")
    private void checkLatestVersion(){

        databaseReference.orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                VersionModel versionModel = snapshot.child("latest_update").getValue(VersionModel.class);

                double latest_version = versionModel.getVersion();
                String message = versionModel.getMessage();
                String link = versionModel.getUpdate_link();

                PackageInfo packageInfo = null;
                try {
                    packageInfo = LaunchActivity.this.getPackageManager().getPackageInfo(getPackageName(), 0);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

                double app_version = Double.parseDouble(Objects.requireNonNull(packageInfo).versionName);

                if (app_version >= latest_version) {
                    if(UserConfig.isLoggedIn()){
                        checkProfile();
                    }else{
                        startActivity(new Intent(LaunchActivity.this,LoginActivity.class));
                        finish();
                    }
                } else {
                    if (versionModel.isRequired()) {
                        boolean forceUpdate = !(app_version >= versionModel.getMin_version());
                        showUpdateDialog(forceUpdate,latest_version,message, link);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @SuppressWarnings("ConstantConditions")
    private void showUpdateDialog(boolean forceUpdate, double version, String message, String link) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.UpdateDialog);
        View bottomSheetView;
        bottomSheetView = getLayoutInflater().inflate(R.layout.update_dialog,null);
        bottomSheetDialog.setContentView(bottomSheetView);

       // bottomSheetDialog.getWindow().findViewById(R.id.design_bottom_sheet).setBackgroundResource(android.R.color.transparent);

        Button dismiss = bottomSheetDialog.findViewById(R.id.button_dismiss);
        Button button = bottomSheetDialog.findViewById(R.id.button_install);
        TextView textview_version = bottomSheetDialog.findViewById(R.id.textview_version);
        TextView textview_message = bottomSheetDialog.findViewById(R.id.textview_content);

        textview_version.setText(String.valueOf(version));
        textview_message.setText(message);

        dismiss.setVisibility(forceUpdate? View.GONE : View.VISIBLE);

        bottomSheetDialog.setOnDismissListener((dialog -> {
            if(!forceUpdate) checkProfile();
        }));

        dismiss.setOnClickListener((view)->{
            checkProfile();
        });
        button.setOnClickListener((view) -> {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));
        });
        bottomSheetDialog.setCancelable(!forceUpdate);
        bottomSheetDialog.show();
    }

    private void checkProfile() {
        DatabaseReference users = _firebase.getReference("users/" + UserConfig.getUid());
        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    UserModel model = snapshot.getValue(UserModel.class);
                    UserConfig.config = model;
                    startActivity(new Intent(LaunchActivity.this, HomeActivity.class));
                }else{
                    startActivity(new Intent(LaunchActivity.this, ProfileCreateActivity.class));
                }
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
