package com.tyron.hanapbb.ui.cells;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.icu.util.Measure;
import android.os.Build;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.tyron.hanapbb.R;
import com.tyron.hanapbb.messenger.AndroidUtilities;
import com.tyron.hanapbb.messenger.FirebaseUtilities;
import com.tyron.hanapbb.messenger.NotificationCenter;
import com.tyron.hanapbb.messenger.UserConfig;
import com.tyron.hanapbb.ui.ConversationsListActivity;
import com.tyron.hanapbb.ui.actionbar.ActionBar;
import com.tyron.hanapbb.ui.actionbar.SimpleTextView;
import com.tyron.hanapbb.ui.actionbar.Theme;
import com.tyron.hanapbb.ui.components.SendingFileDrawable;
import com.tyron.hanapbb.ui.components.StatusDrawable;
import com.tyron.hanapbb.ui.components.TypingDotsDrawable;
import com.tyron.hanapbb.ui.fragments.ChatFragment;

import java.text.SimpleDateFormat;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAvatarCell extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {

    private ChatFragment parentFragment;
    private boolean occupyStatusBar = false;

    private SimpleTextView titleTextView;
    private SimpleTextView subtitleTextView;

    private StatusDrawable[] statusDrawables = new StatusDrawable[5];

    private CircleImageView avatarImageView;

    private int leftPadding = AndroidUtilities.dp(8);

    private AnimatorSet titleAnimation;

    private String chat_id;

    public ChatAvatarCell(@NonNull Context context, ChatFragment activity, String chat_id) {
        super(context);
        this.chat_id = chat_id;
        parentFragment = activity;
        avatarImageView = new CircleImageView(context);
        avatarImageView.setImageResource(R.mipmap.ic_launcher);
        addView(avatarImageView);

        //TODO :set on click to profile

        titleTextView = new SimpleTextView(context);
        titleTextView.setTextColor(Color.parseColor("#ffffff"));
        titleTextView.setTextSize(18);
        titleTextView.setGravity(Gravity.START);
        titleTextView.setLeftDrawableTopPadding(-AndroidUtilities.dp(1.3f));

        addView(titleTextView);

        subtitleTextView = new SimpleTextView(context);
        subtitleTextView.setTextSize(14);
        subtitleTextView.setGravity(Gravity.START);
        subtitleTextView.setTextColor(Color.parseColor("#e8e8e8"));

        //default colors
        //#e8e8e8
        //f05454
        //30475e
        //222831

        addView(subtitleTextView);

        statusDrawables[0] = new TypingDotsDrawable(false);
        statusDrawables[1] = new SendingFileDrawable(false);

        statusDrawables[0].setIsChat(true);
        statusDrawables[1].setIsChat(true);

        listenForStatus();
    }

    private void listenForStatus() {
        FirebaseUtilities.userRef.child(chat_id.replace(UserConfig.getUid(), "")).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("status").exists()){
                    String status = String.valueOf(snapshot.child("status").getValue());
                    if(status.equals("online")){
                        setSubTitle("Online");
                    }else{
                        String date = (String) DateUtils.getRelativeTimeSpanString(getContext(),Long.parseLong(status));
                        setSubTitle("Last seen "+  date);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
        int actionBarHeight = ActionBar.getCurrentActionBarHeight() + AndroidUtilities.dp(6);
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

    public void setPicture(String url){
        Glide.with(getContext()).load(url).into(avatarImageView);
    }
    public void setTypingAnimation(boolean start){
        titleAnimation = new AnimatorSet();
        if(start){
            try{
                subtitleTextView.setLeftDrawable(statusDrawables[0]);
                statusDrawables[0].start();
            }catch(Exception e){
                Log.e("HanapBb", e.toString());
            }
        }else{
            subtitleTextView.setLeftDrawable(null);
            statusDrawables[0].stop();
        }
    }

    public String getTitleText(){
        return titleTextView.getText().toString();
    }

    public void setFileSendingAnimation(boolean start){
        if(start){
            subtitleTextView.setLeftDrawable(statusDrawables[1]);
            statusDrawables[1].start();
        }else{
            subtitleTextView.setLeftDrawable(null);
            statusDrawables[1].stop();
        }
    }
}

