package com.tyron.hanapbb.ui.components;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.transition.Slide;

import com.tyron.hanapbb.messenger.AndroidUtilities;

public class EmojiPopupWindow extends PopupWindow {

    private Context context;
    private ViewGroup rootView;
    private EditText editText;
    private View anchorView;
    private View triggerView;
    private LinearLayout layout;

    private int keyboardHeight;

    public EmojiPopupWindow(Context context, ViewGroup rootView, EditText editText, View anchorView, View triggerView,int keyboardHeight){

        super(context);
        this.context = context;
        this.rootView = rootView;
        this.editText = editText;
        this.anchorView = anchorView;
        this.triggerView = triggerView;
        this.keyboardHeight = keyboardHeight;

        layout = new LinearLayout(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layout.setLayoutParams(params);
        layout.setGravity(Gravity.CENTER);

        TextView text = new TextView(context);
        text.setText("Emoji Popup");

        layout.addView(text);
        layout.setBackgroundColor(Color.parseColor("#ffffff"));


        setContentView(layout);

        initConfig();

        setSize(ViewGroup.LayoutParams.MATCH_PARENT, keyboardHeight);
    }
    @SuppressLint("ClickableViewAccessibility")
    private void initConfig() {
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setInputMethodMode(PopupWindow.INPUT_METHOD_NOT_NEEDED);
        setOutsideTouchable(true);
        setFocusable(true);
        setTouchInterceptor((View v, MotionEvent event) -> {
            Log.d("TAG", "action " + event.getAction());
            if(event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    dismiss();
                    return true;
                }
            }
            return false;
        });
    }
    private void setSize(int width, int height){
        setWidth(width);
        setHeight(height);
    }

    public void show(){
        showOverKeyboard();
        animate();
    }

    private void showOverKeyboard() {
        showAtLocation(rootView,Gravity.BOTTOM, 0,0);
    }
    private void animate(){
        ValueAnimator valueAnimator = ValueAnimator.ofInt(0,keyboardHeight);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setHeight((int) animation.getAnimatedValue());
                Log.d("value", "val" + animation.getAnimatedValue());
                //requestLayout();
            }
        });
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.setDuration(260);
        valueAnimator.start();
    }
}
