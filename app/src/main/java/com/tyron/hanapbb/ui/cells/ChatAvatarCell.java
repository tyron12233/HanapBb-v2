package com.tyron.hanapbb.ui.cells;

import android.content.Context;
import android.graphics.Color;
import android.icu.util.Measure;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.tyron.hanapbb.R;
import com.tyron.hanapbb.messenger.AndroidUtilities;
import com.tyron.hanapbb.messenger.NotificationCenter;
import com.tyron.hanapbb.ui.ConversationsListActivity;
import com.tyron.hanapbb.ui.actionbar.ActionBar;
import com.tyron.hanapbb.ui.actionbar.SimpleTextView;
import com.tyron.hanapbb.ui.actionbar.Theme;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAvatarCell extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {

    private ConversationsListActivity parentFragment;
    private boolean occupyStatusBar = false;

    private SimpleTextView titleTextView;
    private SimpleTextView subtitleTextView;

    private CircleImageView avatarImageView;

    private int leftPadding = AndroidUtilities.dp(8);

    public ChatAvatarCell(@NonNull Context context, ConversationsListActivity activity) {
        super(context);
        parentFragment = activity;
        avatarImageView = new CircleImageView(context);
        avatarImageView.setImageResource(R.mipmap.ic_launcher);
        addView(avatarImageView);

        //TODO :set on click to profile

        titleTextView = new SimpleTextView(context);
        titleTextView.setTextColor(Color.parseColor("#000000"));
        titleTextView.setTextSize(18);
        titleTextView.setGravity(Gravity.START);
        titleTextView.setLeftDrawableTopPadding(-AndroidUtilities.dp(1.3f));

        addView(titleTextView);

        subtitleTextView = new SimpleTextView(context);
        subtitleTextView.setTextSize(14);
        subtitleTextView.setGravity(Gravity.START);
        subtitleTextView.setTextColor(Color.parseColor("#000000"));

        addView(subtitleTextView);

    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        if(id == NotificationCenter.didUpdateConnectionState){
            AndroidUtilities.showToast("Connection state updated");
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if(parentFragment != null){
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.didUpdateConnectionState);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int width = MeasureSpec.getSize(widthMeasureSpec);

        int availableWidth = width - AndroidUtilities.dp((avatarImageView.getVisibility() == View.VISIBLE? 54 : 0) + 16);
        avatarImageView.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(42),MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(42), MeasureSpec.EXACTLY));
        titleTextView.measure(MeasureSpec.makeMeasureSpec(availableWidth,MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(24), MeasureSpec.AT_MOST));
        subtitleTextView.measure(MeasureSpec.makeMeasureSpec(availableWidth, MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(20), MeasureSpec.AT_MOST));

        setMeasuredDimension(width - AndroidUtilities.dp(56), MeasureSpec.getSize(heightMeasureSpec));

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int actionBarHeight = ActionBar.getCurrentActionBarHeight();
        int viewTop = (actionBarHeight - AndroidUtilities.dp(42)) / 2 + (Build.VERSION.SDK_INT >= 21  && occupyStatusBar? AndroidUtilities.statusBarHeight : 0);
        avatarImageView.layout(leftPadding, viewTop, leftPadding + AndroidUtilities.dp(42), viewTop + AndroidUtilities.dp(42));
        int l = leftPadding + (avatarImageView.getVisibility() == VISIBLE ? AndroidUtilities.dp( 54) : 0);
        if (subtitleTextView.getVisibility() != GONE) {
            titleTextView.layout(l, viewTop + AndroidUtilities.dp(1.3f), l + titleTextView.getMeasuredWidth(), viewTop + titleTextView.getTextHeight() + AndroidUtilities.dp(1.3f));
        } else {
            titleTextView.layout(l, viewTop + AndroidUtilities.dp(11), l + titleTextView.getMeasuredWidth(), viewTop + titleTextView.getTextHeight() + AndroidUtilities.dp(11));
        }
        subtitleTextView.layout(l, viewTop + AndroidUtilities.dp(24), l + subtitleTextView.getMeasuredWidth(), viewTop + subtitleTextView.getTextHeight() + AndroidUtilities.dp(24));
    }

    public void setTitle(String title){
        titleTextView.setText(title);
    }
    public void setSubTitle(String subtitle){
        subtitleTextView.setText(subtitle);
    }
}
