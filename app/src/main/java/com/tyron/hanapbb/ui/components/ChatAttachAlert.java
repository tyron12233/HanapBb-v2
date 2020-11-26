package com.tyron.hanapbb.ui.components;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.core.graphics.ColorUtils;

import com.tyron.hanapbb.messenger.AndroidUtilities;
import com.tyron.hanapbb.ui.actionbar.BaseFragment;
import com.tyron.hanapbb.ui.actionbar.BottomSheet;
import com.tyron.hanapbb.ui.actionbar.Theme;

import java.util.ArrayList;

public class ChatAttachAlert extends BottomSheet implements BottomSheet.BottomSheetDelegateInterface {

    private int selectedId;
    private int attachItemSize = AndroidUtilities.dp(85);

    private Paint attachButtonPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    protected SizeNotifierFrameLayoutPhoto sizeNotifierFrameLayout;

    @Override
    public void onOpenAnimationStart() {

    }

    @Override
    public void onOpenAnimationEnd() {

    }

    @Override
    public boolean canDismiss() {
        return false;
    }

    public static class AttachAlertLayout extends FrameLayout {

        protected ChatAttachAlert parentAlert;

        public AttachAlertLayout(ChatAttachAlert alert, @NonNull Context context) {
            super(context);
            parentAlert = alert;
        }

        boolean onSheetKeyDown(int keyCode, Context context){
            return false;
        }
        boolean onDismiss(){
            return false;
        }
        boolean onCustomMeasure(View view, int width, int height){
            return false;
        }
        boolean onCustomLayout(View view, int left, int top, int right, int bottom) {
            return false;
        }

        boolean onContainerViewTouchEvent(MotionEvent event) {
            return false;
        }

        void onPreMeasure(int availableWidth, int availableHeight) {

        }

        void onMenuItemClick(int id) {

        }

        void onButtonsTranslationYUpdated() {

        }

        boolean canScheduleMessages(){
            return true;
        }

        void onPause() {

        }

        void onResume() {

        }

        boolean canDismissWithTouchOutside() {
            return true;
        }

        void onDismissWithButtonClick(int item) {

        }

        void onContainerTranslationUpdated(float currentPanTranslationY) {

        }

        void onHideShowProgress(float progress) {

        }

        void onOpenAnimationEnd() {

        }

        void onInit(boolean mediaEnabled) {

        }

        int getSelectedItemsCount() {
            return 0;
        }

        void onSelectedItemsCountChanged(int count) {

        }

        void applyCaption(String text) {

        }

        void onDestroy() {

        }

        void onHide() {

        }

        void onHidden() {

        }

        int getCurrentItemTop() {
            return 0;
        }

        int getFirstOffset() {
            return 0;
        }

        int getButtonsHideOffset() {
            return AndroidUtilities.dp(needsActionBar() != 0 ? 12 : 17);
        }

        int getListTopPadding() {
            return 0;
        }

        int needsActionBar() {
            return 0;
        }

        void sendSelectedItems(boolean notify, int scheduleDate) {

        }

        void onShow() {

        }

        void onShown() {

        }

        void scrollToTop() {

        }

        boolean onBackPressed() {
            return false;
        }

    }

    private class AttachButton extends FrameLayout{

        private TextView textView;
        private ImageView imageView;

        private boolean checked;
        private int currentId;
        private float checkedState;

        private Animator checkAnimator;

        public AttachButton(@NonNull Context context) {
            super(context);
            setWillNotDraw(false);

            imageView = new androidx.appcompat.widget.AppCompatImageView(context){
                @Override
                public void setScaleX(float scaleX) {
                    super.setScaleX(scaleX);
                    AttachButton.this.invalidate();
                }
            };

            imageView.setScaleType(ImageView.ScaleType.CENTER);
            addView(imageView, LayoutHelper.createFrame(32, 32, Gravity.CENTER_HORIZONTAL | Gravity.TOP, 0, 18, 0, 0));

            textView = new TextView(context);
            textView.setMaxLines(2);
            textView.setGravity(Gravity.CENTER_HORIZONTAL);
            textView.setEllipsize(TextUtils.TruncateAt.END);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
            textView.setLineSpacing(-AndroidUtilities.dp(2),1.0f);
            addView(textView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.LEFT | Gravity.TOP, 0, 62, 0, 0));
        }

        void updateCheckedState(boolean animate){
            if(checked == (currentId == selectedId) ){
                return;
            }
            checked = currentId == selectedId;
            if(checkAnimator != null){
                checkAnimator.cancel();
            }
            if(animate){
                if(checked){
                    //play animaton
                }else{
                    checkAnimator = ObjectAnimator.ofFloat(this, "checkedState", checked ? 1f : 0f);
                    checkAnimator.setDuration(200);
                    checkAnimator.start();
                }
            }else{
                setCheckedState(checked ? 1f : 0f);
            }
        }

        void setCheckedState(float state){
            checkedState = state;
            imageView.setScaleX(1.0f - 0.06f * state);
            imageView.setScaleY(1.0f - 0.06f * state);
            textView.setTextColor(ColorUtils.blendARGB(0x0000000, 0x000000,checkedState));
            invalidate();
        }
        @Keep
        public float getCheckedState() {
            return checkedState;
        }

        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            updateCheckedState(false);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(MeasureSpec.makeMeasureSpec(attachItemSize, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(84), MeasureSpec.EXACTLY));
        }

        @Override
        protected void onDraw(Canvas canvas) {
            float scale = imageView.getScaleX() + 0.06f * checkedState;
            float radius = AndroidUtilities.dp(23) * scale;

            float cx = imageView.getLeft() + imageView.getMeasuredWidth() / 2;
            float cy = imageView.getTop() + imageView.getMeasuredWidth() / 2;

            attachButtonPaint.setColor(0xeeeeee);
            attachButtonPaint.setStyle(Paint.Style.STROKE);
            attachButtonPaint.setStrokeWidth(AndroidUtilities.dp(3) * scale);
            attachButtonPaint.setAlpha(Math.round(255f * checkedState));
            canvas.drawCircle(cx, cy, radius - 0.5f * attachButtonPaint.getStrokeWidth(), attachButtonPaint);

            attachButtonPaint.setAlpha(255);
            attachButtonPaint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(cx, cy, radius - AndroidUtilities.dp(5) * checkedState, attachButtonPaint);
        }
        @Override
        public boolean hasOverlappingRendering() {
            return false;
        }

    }

    private ArrayList<android.graphics.Rect> exclusionRects = new ArrayList<>();
    private android.graphics.Rect exclustionRect = new Rect();

    public ChatAttachAlert(Context context, final BaseFragment parentFragment) {
        super(context, false);
        drawNavigationBar = true;
        openInterpolator = new OvershootInterpolator();
        setDelegate(this);
        exclusionRects.add(exclustionRect);

        sizeNotifierFrameLayout = new SizeNotifierFrameLayoutPhoto(context){

        };
    }

}
