package com.tyron.hanapbb.ui;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.FrameLayout;

import com.tyron.hanapbb.R;
import com.tyron.hanapbb.ui.actionbar.BaseFragment;

public class TestActivity extends BaseFragment {

    @Override
    public boolean onFragmentCreate() {
        return super.onFragmentCreate();
    }

    @Override
    public View createView(Context context) {
        actionBar.setBackgroundColor(Color.parseColor("#ffffff"));
        actionBar.setBackButtonImage(R.drawable.ic_arrow_back);


        FrameLayout frameLayout = new FrameLayout(context);

        fragmentView = new FrameLayout(context);
        presentFragment(this);
        return fragmentView;
    }

    @Override
    public boolean onBackPressed() {
        return super.onBackPressed();
    }
}
