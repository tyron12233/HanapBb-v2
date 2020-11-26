package com.tyron.hanapbb.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.tyron.hanapbb.R;
import com.tyron.hanapbb.messenger.UserConfig;
import com.tyron.hanapbb.ui.actionbar.ActionBarLayout;
import com.tyron.hanapbb.ui.actionbar.BaseFragment;
import com.tyron.hanapbb.ui.components.PickerBottomLayout;
import com.tyron.hanapbb.ui.fragments.ChatFragment;
import com.tyron.hanapbb.ui.fragments.ConversationsList;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity implements ActionBarLayout.ActionBarLayoutDelegate {

    public static final String PHOTOS = "PHOTOS";
    public static final String VIDEO = "VIDEOS";

    private static final String GALLERY_CONFIG = "GALLERY_CONFIG";

    private ActionBarLayout actionBarLayout;
    private TestActivity testActivity;
    private PhotoAlbumPickerActivity albumPickerActivity;

    private PickerBottomLayout pickerBottomLayout;

    private ArrayList<BaseFragment> mainFragmentStack = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        FrameLayout container = findViewById(R.id.container);
        actionBarLayout = new ActionBarLayout(this);

        //testActivity = new TestActivity();

         container.addView(actionBarLayout);
         actionBarLayout.init(mainFragmentStack);
         actionBarLayout.setDelegate(this);

       getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        //getSupportFragmentManager().beginTransaction().add(R.id.container, ConversationsList.newInstance()).commit();
        String READ_EXTERNAL_STORAGE = "android.permission.READ_EXTERNAL_STORAGE";

        if (checkCallingOrSelfPermission(
                READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= 23) {
                requestPermissions(new String[] {
                        READ_EXTERNAL_STORAGE
                }, 1);
                return;
            }
        }
        showContent();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        showContent();
    }

    private void showContent() {
        Intent intent = getIntent();
        albumPickerActivity = new PhotoAlbumPickerActivity(
                new String[]{"image/jpeg"},
                3,
                false,
                "Select a photo",
                false
        );


       // albumPickerActivity.setDelegate(mPhotoAlbumPickerActivityDelegate);
          actionBarLayout.presentFragment(new ConversationsList());
        //actionBarLayout.presentFragment(pickerBottomLayout);
       // actionBarLayout.presentFragment(albumPickerActivity,false,true,true);
    }

    private PhotoAlbumPickerActivity.PhotoAlbumPickerActivityDelegate mPhotoAlbumPickerActivityDelegate = new PhotoAlbumPickerActivity.PhotoAlbumPickerActivityDelegate() {
        @Override
        public void didSelectPhotos(ArrayList<String> photos, ArrayList<String> captions) {
            Intent intent = new Intent();
            intent.putExtra(PHOTOS, photos);
            setResult(Activity.RESULT_OK, intent);
        }

        @Override
        public boolean didSelectVideo(String path) {
            Intent intent = new Intent();
            intent.putExtra(VIDEO, path);
            setResult(Activity.RESULT_OK, intent);
            return true;
        }

        @Override
        public void startPhotoSelectActivity() {
        }
    };

    public void replaceFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_left).replace(R.id.container, fragment).addToBackStack(null).commit();
    }

    @Override
    public boolean onPreIme() {
        return false;
    }

    @Override
    public boolean needPresentFragment(BaseFragment fragment, boolean removeLast, boolean forceWithoutAnimation, ActionBarLayout layout) {
        return true;
    }

    @Override
    public boolean needAddFragmentToStack(BaseFragment fragment, ActionBarLayout layout) {
        return true;
    }

    @Override
    public boolean needCloseLastFragment(ActionBarLayout layout) {
        if(layout.fragmentsStack.size() <= 1){
            finish();
            return false;
        }
        return true;
    }

    @Override
    public void onRebuildAllFragments(ActionBarLayout layout) {

    }

    @Override
    public void onBackPressed() {
        actionBarLayout.onBackPressed();
    }
}