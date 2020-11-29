package com.tyron.hanapbb.ui.components;

import android.content.Context;
import android.os.Bundle;

import com.tyron.hanapbb.messenger.AndroidUtilities;
import com.tyron.hanapbb.messenger.MediaController;
import com.tyron.hanapbb.messenger.NotificationCenter;
import com.tyron.hanapbb.ui.actionbar.BaseFragment;
import com.tyron.hanapbb.ui.actionbar.BottomSheet;

import java.util.ArrayList;
import java.util.HashMap;

public class PickerBottomSheet extends BottomSheet implements NotificationCenter.NotificationCenterDelegate {

    private HashMap<Integer, MediaController.PhotoEntry> selectedPhotos;
    private ArrayList<MediaController.SearchImage> recentImages;

    private ArrayList<MediaController.SearchImage> searchResult = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public PickerBottomSheet(Context context, boolean needFocus) {
        super(context, needFocus);

        NotificationCenter.getInstance().addObserver(this, NotificationCenter.recentImagesDidLoaded);
        NotificationCenter.getInstance().addObserver(this,NotificationCenter.albumsDidLoaded);
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        if(id == NotificationCenter.albumsDidLoaded){
            AndroidUtilities.showToast("Albums loaded" + args[0]);
        }
    }

    @Override
    public void show() {
        super.show();

        MediaController.loadGalleryPhotosAlbums(BaseFragment.lastClassGuid,new String[] { "image/jpeg"});
        AndroidUtilities.showToast("Show");
    }
}
