package com.tyron.hanapbb.ui;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

import com.tyron.hanapbb.R;
import com.tyron.hanapbb.ui.actionbar.BaseFragment;

public class SettingsActivity extends BaseFragment {

    @Override
    public boolean onFragmentCreate() {
        return super.onFragmentCreate();
    }

    @Override
    public View createView(Context context) {
        fragmentView = new FrameLayout(context);
        actionBar.setBackgroundColor(Color.parseColor("#ffffff"));
        actionBar.setCastShadows(false);

        actionBar.setTitle("Settings");
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.settings_activity, (ViewGroup) fragmentView,false);
        ((ViewGroup) fragmentView).addView(view);
            ((AppCompatActivity)getParentActivity()).getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        return fragmentView;
    }


    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }
    }
}