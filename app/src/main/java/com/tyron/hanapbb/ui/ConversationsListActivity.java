package com.tyron.hanapbb.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import android.recyclerview.widget.RecyclerView;

import com.tyron.hanapbb.R;
import com.tyron.hanapbb.messenger.NotificationCenter;
import com.tyron.hanapbb.ui.actionbar.ActionBar;
import com.tyron.hanapbb.ui.actionbar.ActionBarMenu;
import com.tyron.hanapbb.ui.actionbar.ActionBarMenuItem;
import com.tyron.hanapbb.ui.actionbar.BaseFragment;

public class ConversationsListActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {

    private RecyclerView recyclerView;

    @Override
    public View createView(Context context) {

        fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) fragmentView;
        actionBar.setBackgroundColor(0xffffff);
       // fragmentView.setBackgroundColor(Color.parseColor("#432635"));

        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.chat_empty_state, (ViewGroup) fragmentView, false);

        ((FrameLayout) fragmentView).addView(view );

        fragmentView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        actionBar.createActionMode();
        actionBar.setFitsSystemWindows(true);
        actionBar.setOccupyStatusBar(true);

        actionBar.createActionMode();
        ActionBarMenu menu = actionBar.createMenu();
        menu.addItem(44, R.drawable.ic_search_24);

        actionBar.setBackButtonImage(R.drawable.ic_arrow_back);
        ActionBarMenuItem items = new ActionBarMenuItem(context,menu, 0xff000000);

        items.addSubItem(0,"Chat info", R.drawable.ic_baseline_add_24);

        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                }
                if(id == 44){
                   items.toggleSubMenu();
                }
            }
        });

        return fragmentView;
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {

    }

    @Override
    public boolean onBackPressed() {
        return super.onBackPressed();
    }
}
