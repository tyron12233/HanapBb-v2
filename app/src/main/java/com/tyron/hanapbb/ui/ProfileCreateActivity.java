package com.tyron.hanapbb.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tyron.hanapbb.R;
import com.tyron.hanapbb.messenger.UserConfig;
import com.tyron.hanapbb.ui.models.UserModel;
import com.tyron.hanapbb.ui.fragments.ProfileCreateFragment;

public class ProfileCreateActivity extends AppCompatActivity implements ProfileCreateFragment.onGenderSelectedListener{

    private FirebaseStorage profile = FirebaseStorage.getInstance();
    private StorageReference up = profile.getReference("users");
    private FirebaseDatabase _firebase = FirebaseDatabase.getInstance();
    private DatabaseReference ref = _firebase.getReference("users");


    private int gender = -1;
    private int preferred_gender = -1;

    private Button next;
    public ViewPager2 vp;

    private ProfileCreateFragment fragment;

    private boolean page1  = false, page2 = false;
    private Uri photoUri;

    private Context context;

    @Override
    public void onBackPressed(){
        if(vp.getCurrentItem() > 0){
            vp.setCurrentItem(vp.getCurrentItem()-1,true);
        }else{
            super.onBackPressed();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activiy_profile_create);
        context = this;

        next = findViewById(R.id.button_next);

        vp = findViewById(R.id.viewpager);
        ProfileCreateAdapter adapter = new ProfileCreateAdapter(this);

        vp.setAdapter(adapter);

        next.setOnClickListener((v) -> {
            fragment = (ProfileCreateFragment) getSupportFragmentManager().findFragmentByTag("f" + vp.getCurrentItem());
            switch(vp.getCurrentItem()){
                case 1:
                    if(fragment.isDoneTyping()){
                        vp.setCurrentItem(2,true);
                        page2 = true;
                    }else{
                        Toast.makeText(this, "Complete all the fields", Toast.LENGTH_LONG).show();
                    }
                    break;
                case 2:
                    if(fragment.pictureSelected() && page1 && page2){
                        upload();
                    }
                    break;

                default:
                    if (gender != -1 && preferred_gender != -1) {
                        vp.setCurrentItem(1,true);
                        page1 = true;
                    }
            }
        });
    }

    private void upload() {
            fragment = (ProfileCreateFragment) getSupportFragmentManager().findFragmentByTag("f" + 1);
            up.child(UserConfig.getUid()).putFile(photoUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {

                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    return up.child(UserConfig.getUid()).getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String photoUrl = downloadUri.toString();

                        UserModel model = fragment.getModel();
                        model.setPhotoUrl(photoUrl);
                        model.setGender(gender == 0? "female" : "male");
                        model.setPreferredGender(preferred_gender == 0? "female" : "male");
                        model.setUid(UserConfig.getUid());

                        ref.child(UserConfig.getUid()).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(context, "Success!", Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(context, LaunchActivity.class));
                                }
                            }
                        });
                    } else {
                        Toast.makeText(context, "An error has occured on image upload", Toast.LENGTH_LONG).show();
                    }
                }
            });
    }
    @Override
    public void onPictureSelected(Uri uri){
        photoUri = uri;
    }
    @Override
    public void onGenderSelect(int gender) {
        this.gender = gender;
    }

    @Override
    public void onGenderPreferredSelect(int gender){
        this.preferred_gender = gender;
    }

    public class ProfileCreateAdapter extends FragmentStateAdapter {

        public ProfileCreateAdapter(FragmentActivity fm){
            super(fm);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return ProfileCreateFragment.newInstance(position);
        }

        @Override
        public int getItemCount() {
            return 3;
        }
    }
}