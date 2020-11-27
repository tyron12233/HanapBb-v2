package com.tyron.hanapbb;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

import com.google.firebase.database.FirebaseDatabase;
import com.tyron.hanapbb.emoji.EmojiManager;
import com.tyron.hanapbb.emoji.ios.IosEmojiProvider;
import com.tyron.hanapbb.messenger.AndroidUtilities;
import com.tyron.hanapbb.messenger.UserConfig;

public class MyApplication extends Application {

    public static Context applicationContext;
    public static volatile Handler applicationHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        EmojiManager.install(new IosEmojiProvider());
        applicationContext = this;
        AndroidUtilities.fillStatusBarHeight(applicationContext);
        applicationHandler = new Handler(applicationContext.getMainLooper());

        if(UserConfig.getUid() != null) UserConfig.updateStatus();
    }

}
