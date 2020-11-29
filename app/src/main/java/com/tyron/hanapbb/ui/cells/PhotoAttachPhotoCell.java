package com.tyron.hanapbb.ui.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.tyron.hanapbb.R;
import com.tyron.hanapbb.messenger.AndroidUtilities;
import com.tyron.hanapbb.messenger.MediaController;
import com.tyron.hanapbb.ui.PhotoViewer;
import com.tyron.hanapbb.ui.actionbar.Theme;
import com.tyron.hanapbb.ui.components.BackupImageView;
import com.tyron.hanapbb.ui.components.LayoutHelper;

public class PhotoAttachPhotoCell extends FrameLayout {

    private FrameLayout container;
    private FrameLayout videoInfoContainer;
    private FrameLayout checkFrame;
    private BackupImageView imageView;

    private TextView videoTextView;

    private int itemSize;

    private boolean isVertical;
    private boolean itemSizeChanged;
    private boolean isLast;
    private boolean pressed;

    private MediaController.PhotoEntry photoEntry;

    public interface PhotoAttachPhotoCellDelegate{
        void onCheckClick(PhotoAttachPhotoCell v);
    }
    public PhotoAttachPhotoCell(@NonNull Context context) {
        super(context);
        setWillNotDraw(false);

        container = new FrameLayout(context);
        addView(container, LayoutHelper.createFrame(80,80));

        imageView = new BackupImageView(context);
        container.addView(imageView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        videoInfoContainer = new FrameLayout(context){

            private RectF rect = new RectF();

            @Override
            protected void onDraw(Canvas canvas) {
                rect.set(0,0,getMeasuredWidth(),getMeasuredHeight());
                canvas.drawRoundRect(rect, AndroidUtilities.dp(4),AndroidUtilities.dp(4), Theme.chat_statusPaint);
            }
        };
        videoInfoContainer.setWillNotDraw(false);
        videoInfoContainer.setPadding(AndroidUtilities.dp(5),0,AndroidUtilities.dp(5),0);

        container.addView(videoInfoContainer, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, 17, Gravity.BOTTOM | Gravity.LEFT, 4, 0, 0, 4));

        ImageView imageView1 = new ImageView(context);
        imageView1.setImageResource(R.drawable.ic_photo_camera_black_36dp);
        videoInfoContainer.addView(imageView1, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.LEFT | Gravity.CENTER_VERTICAL));

        videoTextView = new TextView(context);
        videoTextView.setTextColor(0xffffffff);
        videoTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        videoInfoContainer.addView(videoTextView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.LEFT | Gravity.CENTER_VERTICAL, 13, -0.7f, 0, 0));

        checkFrame = new FrameLayout(context);
        addView(checkFrame, LayoutHelper.createFrame(42, 42, Gravity.LEFT | Gravity.TOP, 38, 0, 0, 0));

        itemSize = AndroidUtilities.dp(80);


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (itemSizeChanged) {
            super.onMeasure(MeasureSpec.makeMeasureSpec(itemSize, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(itemSize + AndroidUtilities.dp(5), MeasureSpec.EXACTLY));
        } else {
            if (isVertical) {
                super.onMeasure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(80), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(80 + (isLast ? 0 : 6)), MeasureSpec.EXACTLY));
            } else {
                super.onMeasure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(80 + (isLast ? 0 : 6)), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(80), MeasureSpec.EXACTLY));
            }
        }
    }
}
