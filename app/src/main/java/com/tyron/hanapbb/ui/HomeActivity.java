package com.tyron.hanapbb.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.tyron.hanapbb.BackgroundUpdateService;
import com.tyron.hanapbb.R;
import com.tyron.hanapbb.messenger.AndroidUtilities;
import com.tyron.hanapbb.messenger.NotificationCenter;
import com.tyron.hanapbb.messenger.UserConfig;
import com.tyron.hanapbb.ui.actionbar.ActionBarLayout;
import com.tyron.hanapbb.ui.actionbar.BaseFragment;
import com.tyron.hanapbb.ui.components.PickerBottomLayout;
import com.tyron.hanapbb.ui.fragments.ChatFragment;
import com.tyron.hanapbb.ui.fragments.ConversationsList;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity implements ActionBarLayout.ActionBarLayoutDelegate, NotificationCenter.NotificationCenterDelegate {
    private ActionBarLayout actionBarLayout;

    private final ArrayList<BaseFragment> mainFragmentStack = new ArrayList<>();

    @Override
    public void onAttachedToWindow() {
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.closeChats);
        super.onAttachedToWindow();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

//        startService(new Intent(this, BackgroundUpdateService.class ));

        FrameLayout container = findViewById(R.id.container);
        actionBarLayout = new ActionBarLayout(this);

        //testActivity = new TestActivity();

         container.addView(actionBarLayout);
         actionBarLayout.init(mainFragmentStack);
         actionBarLayout.setDelegate(this);

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
        updateStatusBar();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        showContent();
    }

    private void showContent() {

       // albumPickerActivity.setDelegate(mPhotoAlbumPickerActivityDelegate);
          actionBarLayout.presentFragment(new ConversationsList());
      //  actionBarLayout.presentFragment(new SettingsActivity());
        //actionBarLayout.presentFragment(pickerBottomLayout);
       // actionBarLayout.presentFragment(albumPickerActivity,false,true,true);
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

    @Override
    public void didReceivedNotification(int id, Object... args) {
        if(id == NotificationCenter.closeChats){
            updateStatusBar();
        }
    }

    private void updateStatusBar() {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        window.setStatusBarColor(Color.TRANSPARENT);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    @Override
    protected void onDestroy() {
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.closeChats);
        super.onDestroy();
    }
}