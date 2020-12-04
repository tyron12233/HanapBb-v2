package com.tyron.hanapbb.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.recyclerview.widget.DefaultItemAnimator;
import android.recyclerview.widget.LinearLayoutManager;
import android.recyclerview.widget.RecyclerView;
import android.util.Property;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.graphics.ColorUtils;
import androidx.core.view.NestedScrollingParent3;
import androidx.core.view.NestedScrollingParentHelper;
import androidx.core.view.ViewCompat;
import androidx.preference.PreferenceManager;

import com.bumptech.glide.Glide;
import com.tyron.hanapbb.R;
import com.tyron.hanapbb.messenger.AndroidUtilities;
import com.tyron.hanapbb.messenger.FileLog;
import com.tyron.hanapbb.messenger.NotificationCenter;
import com.tyron.hanapbb.messenger.UserConfig;
import com.tyron.hanapbb.tl.FileLocation;
import com.tyron.hanapbb.tl.ImageLoader;
import com.tyron.hanapbb.tl.TLObject;
import com.tyron.hanapbb.ui.actionbar.ActionBar;
import com.tyron.hanapbb.ui.actionbar.ActionBarMenu;
import com.tyron.hanapbb.ui.actionbar.ActionBarMenuItem;
import com.tyron.hanapbb.ui.actionbar.BackDrawable;
import com.tyron.hanapbb.ui.actionbar.BaseFragment;
import com.tyron.hanapbb.ui.actionbar.SimpleTextView;
import com.tyron.hanapbb.ui.actionbar.Theme;
import com.tyron.hanapbb.ui.cells.CheckBoxCell;
import com.tyron.hanapbb.ui.cells.HeaderCell;
import com.tyron.hanapbb.ui.cells.RadioButtonCell;
import com.tyron.hanapbb.ui.cells.TextCheckBoxCell;
import com.tyron.hanapbb.ui.cells.TextDetailCell;
import com.tyron.hanapbb.ui.components.AnimatedFileDrawable;
import com.tyron.hanapbb.ui.components.AnimationProperties;
import com.tyron.hanapbb.ui.components.BackupImageView;
import com.tyron.hanapbb.ui.components.CombinedDrawable;
import com.tyron.hanapbb.ui.components.CubicBezierInterpolator;
import com.tyron.hanapbb.ui.components.EmptyTextProgressView;
import com.tyron.hanapbb.ui.components.ImageReceiver;
import com.tyron.hanapbb.ui.components.LayoutHelper;
import com.tyron.hanapbb.ui.components.RecyclerListView;
import com.tyron.hanapbb.ui.models.UserModel;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import io.grpc.okhttp.internal.framed.Header;

public class ProfileActivity extends BaseFragment{

    private ListAdapter listAdapter;
    private ValueAnimator expandAnimator;
    private float[] expandAnimatorValues = new float[]{0f, 1f};
    private ActionBarMenuItem animatingItem;
    private float currentExpanAnimatorFracture;
    private float animationProgress;
    protected float headerShadowAlpha = -1.0f;
    private float listViewVelocityY;
    private SimpleTextView[] nameTextView = new SimpleTextView[2];
    private SimpleTextView[] onlineTextView = new SimpleTextView[3];

    private AvatarImageView avatarImage;

    private int rowCount;

    private ImageView writeButton;
    private AnimatorSet writeButtonAnimation;

    private HashMap<Integer, Integer> positionToOffset = new HashMap<>();

    private TopView topView;

    private boolean isPulledDown;
    private boolean allowProfileAnimation = true;

    private int listContentHeight;

    private FrameLayout avatarContainer;

    private float nameX;
    private float nameY;
    private float onlineX;
    private float onlineY;

    private LinearLayoutManager layoutManager;

    private UserModel userModel;

    private float mediaHeaderAnimationProgress;
    private Property<ActionBar, Float> ACTIONBAR_HEADER_PROGRESS = new AnimationProperties.FloatProperty<ActionBar>("animationProgress") {
        @Override
        public void setValue(ActionBar object, float value) {
            mediaHeaderAnimationProgress = value;
            topView.invalidate();

            int color1 = Theme.getColor(Theme.key_profile_title);
            int color2 = Theme.getColor(Theme.key_player_actionBarTitle);
            int c = AndroidUtilities.getOffsetColor(color1, color2, value, 1.0f);
            nameTextView[1].setTextColor(c);


            //color1 = Theme.getColor(Theme.key_actionBarDefaultIcon);
            //color2 = Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2);
            //actionBar.setItemsColor(AndroidUtilities.getOffsetColor(color1, color2, value, 1.0f), false);

            color1 = Theme.getColor(Theme.key_avatar_actionBarSelectorBlue);
            color2 = Theme.getColor(Theme.key_actionBarActionModeDefaultSelector);
            actionBar.setItemsBackgroundColor(AndroidUtilities.getOffsetColor(color1, color2, value, 1.0f));

            topView.invalidate();
           // otherItem.setIconColor(Theme.getColor(Theme.key_actionBarDefaultIcon));
            //callItem.setIconColor(Theme.getColor(Theme.key_actionBarDefaultIcon));
            //videoCallItem.setIconColor(Theme.getColor(Theme.key_actionBarDefaultIcon));
            //editItem.setIconColor(Theme.getColor(Theme.key_actionBarDefaultIcon));

        }

        @Override
        public Float get(ActionBar object) {
            return mediaHeaderAnimationProgress;
        }
    };
    private float extraHeight;

    private SharedPreferences settingsPref;

    private AnimatorSet headerAnimatorSet;
    private boolean mediaHeaderVisible;
    private int playProfileAnimation = 1;
    private float initialAnimationExtraHeight;

    private RecyclerListView listView;
    private boolean openAnimationInProgress;
    private float avatarY;
    private float avatarX;
    private float expandProgress;
    private float avatarScale;
    private boolean allowPullingDown;
    private boolean openingAvatar = false;
    private int avatarColor;
    private boolean expandPhoto;
    private boolean fragmentOpened;
    private int lastMeasuredContentWidth;
    private int lastMeasuredContentHeight;
    private boolean transitionAnimationInProress;
    private EmptyTextProgressView emptyView;
    private boolean videoCallItemVisible = false;
    private int setUsernameRow;
    private int bioRow;
    private int numberRow;
    private int numberSectionRow;
    private int usernameRow;
    private int debugHeaderRow;
    private int enableDebugRow;

    private boolean isDebugMode;

    @Override
    public View createView(Context context) {
        settingsPref = PreferenceManager.getDefaultSharedPreferences(context);
        isDebugMode = settingsPref.getBoolean("debug",false);
        Theme.createCommonResources(context);
        Theme.applyCommonTheme();
        extraHeight = AndroidUtilities.dp(88f);
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick(){
            @Override
            public void onItemClick(int id) {
                if(getParentActivity() == null){
                    return;
                }
                if(id == -1){
                    finishFragment();
                }
            }
        });
        fragmentView = new NestedFrameLayout(context){
            private boolean ignoreLayout;
            private boolean firstLayout = true;
            private Paint whitePaint = new Paint();
            private Paint grayPaint = new Paint();

            @Override
            public boolean hasOverlappingRendering() {
                return false;
            }
            @Override
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                final int actionBarHeight = ActionBar.getCurrentActionBarHeight() + (actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0);
                if (listView != null) {
                    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) listView.getLayoutParams();
                    if (layoutParams.topMargin != actionBarHeight) {
                        layoutParams.topMargin = actionBarHeight;
                    }
                }

                int height = MeasureSpec.getSize(heightMeasureSpec);
                super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));

                boolean changed = false;
                if (lastMeasuredContentWidth != getMeasuredWidth() || lastMeasuredContentHeight != getMeasuredHeight()) {
                    changed = lastMeasuredContentWidth != 0 && lastMeasuredContentWidth != getMeasuredWidth();
                    listContentHeight = 0;
                    int count = listAdapter.getItemCount();
                    lastMeasuredContentWidth = getMeasuredWidth();
                    lastMeasuredContentHeight = getMeasuredHeight();
                    int ws = MeasureSpec.makeMeasureSpec(getMeasuredWidth(), MeasureSpec.EXACTLY);
                    int hs = MeasureSpec.makeMeasureSpec(listView.getMeasuredHeight(), MeasureSpec.UNSPECIFIED);
                    positionToOffset.clear();
                    for (int i = 0; i < count; i++) {
                        int type = listAdapter.getItemViewType(i);
                        positionToOffset.put(i, listContentHeight);
                        if (type == 13) {
                            listContentHeight += listView.getMeasuredHeight();
                        } else {
                            RecyclerView.ViewHolder holder = listAdapter.createViewHolder(null, type);
                            listAdapter.onBindViewHolder(holder, i);
                            holder.itemView.measure(ws, hs);
                            listContentHeight += holder.itemView.getMeasuredHeight();
                        }
                    }
                }
                if (firstLayout && (expandPhoto || openAnimationInProgress && playProfileAnimation == 2)) {
                    ignoreLayout = true;

                    if (expandPhoto) {
//                        if (searchItem != null) {
//                            searchItem.setAlpha(0.0f);
//                            searchItem.setEnabled(false);
//                        }
                        nameTextView[1].setTextColor(Color.WHITE);
                        onlineTextView[1].setTextColor(Color.argb(179, 255, 255, 255));
                        actionBar.setItemsBackgroundColor(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR);
                        //actionBar.setItemsColor(Color.WHITE, false);
                        //overlaysView.setOverlaysVisible();
                        //overlaysView.setAlphaValue(1.0f, false);
                       // avatarImage.setForegroundAlpha(1.0f);
                        avatarContainer.setVisibility(View.GONE);
                        //avatarsViewPager.resetCurrentItem();
                        //avatarsViewPager.setVisibility(View.VISIBLE);
                        expandPhoto = false;
                    }

                    allowPullingDown = true;
                    isPulledDown = true;
//                    if (otherItem != null) {
//                        otherItem.showSubItem(gallery_menu_save);
//                        if (imageUpdater != null) {
//                            otherItem.showSubItem(edit_avatar);
//                            otherItem.showSubItem(delete_avatar);
//                            otherItem.hideSubItem(logout);
//                            otherItem.hideSubItem(edit_name);
//                        }
//                    }
                    currentExpanAnimatorFracture = 1.0f;

                    int paddingTop;
                    int paddingBottom;
//                    if (isInLandscapeMode) {
//                        paddingTop = AndroidUtilities.dp(88f);
//                        paddingBottom = 0;
//                    } else {
                        paddingTop = listView.getMeasuredWidth();
                        paddingBottom = Math.max(0, getMeasuredHeight() - (listContentHeight + AndroidUtilities.dp(88) + actionBarHeight));
//                    }
                        listView.setBottomGlowOffset(0);
                    initialAnimationExtraHeight = paddingTop - actionBarHeight;
                    layoutManager.scrollToPositionWithOffset(0, -actionBarHeight);
                    listView.setPadding(0, paddingTop, 0, paddingBottom);
                    measureChildWithMargins(listView, widthMeasureSpec, 0, heightMeasureSpec, 0);
                    listView.layout(0, actionBarHeight, listView.getMeasuredWidth(), actionBarHeight + listView.getMeasuredHeight());
                    ignoreLayout = false;
                } else if (fragmentOpened && !openAnimationInProgress && !firstLayout) {
                    ignoreLayout = true;

                    int paddingTop;
                    int paddingBottom;
//                    if (isInLandscapeMode) {
//                        paddingTop = AndroidUtilities.dp(88f);
//                        paddingBottom = 0;
//                    } else {
                        paddingTop = listView.getMeasuredWidth();
                        paddingBottom = Math.max(0, getMeasuredHeight() - (listContentHeight + AndroidUtilities.dp(88) + actionBarHeight));
//                    }
                        listView.setBottomGlowOffset(0);
                    int currentPaddingTop = listView.getPaddingTop();
                    View view = listView.getChildAt(0);
                    int pos = RecyclerView.NO_POSITION;
                    int top = 0;
                    if (view != null) {
                        RecyclerView.ViewHolder holder = listView.findContainingViewHolder(view);
                        pos = holder.getAdapterPosition();
                        if (pos == RecyclerView.NO_POSITION) {
                            pos = holder.getPosition();
                        }
                        top = view.getTop();
                    }
                    boolean layout = false;
                    if (actionBar.isSearchFieldVisible()) {
                        layoutManager.scrollToPositionWithOffset(0, -paddingTop);
                        layout = true;
                    } else if ((!changed || !allowPullingDown) && pos != RecyclerView.NO_POSITION) {
                        layoutManager.scrollToPositionWithOffset(pos, top - paddingTop);
                        layout = true;
                    }
                    if (currentPaddingTop != paddingTop || listView.getPaddingBottom() != paddingBottom) {
                        listView.setPadding(0, paddingTop, 0, paddingBottom);
                        layout = true;
                    }
                    if (layout) {
                        measureChildWithMargins(listView, widthMeasureSpec, 0, heightMeasureSpec, 0);
                        try {
                            listView.layout(0, actionBarHeight, listView.getMeasuredWidth(), actionBarHeight + listView.getMeasuredHeight());
                        } catch (Exception e) {
                            FileLog.e(e);
                        }
                    }
                    ignoreLayout = false;
                }
            }
            @Override
            protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                super.onLayout(changed, left, top, right, bottom);
                firstLayout = false;
                checkListViewScroll();
            }
            @Override
            public void requestLayout() {
                if (ignoreLayout) {
                    return;
                }
                super.requestLayout();
            }

            @Override
            public void onDraw(Canvas c) {
                whitePaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                if (listView.getVisibility() == VISIBLE) {
                    grayPaint.setColor(Theme.getColor(Theme.key_windowBackgroundGray));
                    if (transitionAnimationInProress) {
                        whitePaint.setAlpha((int) (255 * listView.getAlpha()));
                    }
                    if (transitionAnimationInProress) {
                        grayPaint.setAlpha((int) (255 * listView.getAlpha()));
                    }

                    int top = listView.getTop();
                    int count = listView.getChildCount();
                    Paint paint;
                    for (int a = 0; a <= count; a++) {
                        if (a < count) {
                            View child = listView.getChildAt(a);
                            int bottom = listView.getTop() + child.getBottom();
                            c.drawRect(listView.getX(), top, listView.getX() + listView.getMeasuredWidth(), bottom, child.getBackground() != null ? grayPaint : whitePaint);
                            top = bottom;
                        } else {
                            if (top < listView.getBottom()) {
                                c.drawRect(listView.getX(), top, listView.getX() + listView.getMeasuredWidth(), listView.getBottom(), grayPaint);
                            }
                        }
                    }
                } else {
                    int top = 0;//searchListView.getTop();
                    c.drawRect(0, top + extraHeight + 0, getMeasuredWidth(), top + getMeasuredHeight(), whitePaint);
                }
            }

        };
        fragmentView.setWillNotDraw(false);
        FrameLayout frameLayout = (FrameLayout) fragmentView;

        listAdapter = new ListAdapter(context);

        listView = new RecyclerListView(context){

            private VelocityTracker velocityTracker;

            @Override
            protected boolean allowSelectChildAtPosition(View child) {
                return true; //child != sharedMediaLayout;
            }

            @Override
            public boolean hasOverlappingRendering() {
                return false;
            }

            @Override
            protected void requestChildOnScreen(View child, View focused) {

            }

            @Override
            public void invalidate() {
                super.invalidate();
                if (fragmentView != null) {
                    fragmentView.invalidate();
                }
            }

            @Override
            public boolean onTouchEvent(MotionEvent e) {
                final int action = e.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    if (velocityTracker == null) {
                        velocityTracker = VelocityTracker.obtain();
                    } else {
                        velocityTracker.clear();
                    }
                    velocityTracker.addMovement(e);
                } else if (action == MotionEvent.ACTION_MOVE) {
                    if (velocityTracker != null) {
                        velocityTracker.addMovement(e);
                        velocityTracker.computeCurrentVelocity(1000);
                        listViewVelocityY = velocityTracker.getYVelocity(e.getPointerId(e.getActionIndex()));
                    }
                } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
                    if (velocityTracker != null) {
                        velocityTracker.recycle();
                        velocityTracker = null;
                    }
                }
                final boolean result = super.onTouchEvent(e);
                if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
                    if (allowPullingDown) {
                        final View view = layoutManager.findViewByPosition(0);
                        if (view != null) {
                            if (isPulledDown) {
                                final int actionBarHeight = ActionBar.getCurrentActionBarHeight() + (actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0);
                                listView.smoothScrollBy(0, view.getTop() - listView.getMeasuredWidth() + actionBarHeight, CubicBezierInterpolator.EASE_OUT_QUINT);
                            } else {
                                listView.smoothScrollBy(0, view.getTop() - AndroidUtilities.dp(88), CubicBezierInterpolator.EASE_OUT_QUINT);
                            }
                        }
                    }
                }
                return result;
            }
        };
        listView.setVerticalScrollBarEnabled(false);

        frameLayout.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.TOP | Gravity.LEFT));

        emptyView = new EmptyTextProgressView(context);
        emptyView.showTextView();
        emptyView.setTextSize(18);
        emptyView.setVisibility(View.GONE);
        emptyView.setShowAtCenter(true);
        emptyView.setPadding(0, AndroidUtilities.dp(50), 0, 0);
        emptyView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        frameLayout.addView(emptyView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));



        listView.setVerticalScrollBarEnabled(false);
//        if (imageUpdater == null) {
//            listView.setItemAnimator(null);
//            listView.setLayoutAnimation(null);
//        } else {
            DefaultItemAnimator itemAnimator = (DefaultItemAnimator) listView.getItemAnimator();
            itemAnimator.setSupportsChangeAnimations(false);
            itemAnimator.setDelayAnimations(false);
        //}
        listView.setClipToPadding(false);
        listView.setHideIfEmpty(false);

        layoutManager = new LinearLayoutManager(context) {

            @Override
            public boolean supportsPredictiveItemAnimations() {
                return false;//imageUpdater != null;
            }

            @Override
            public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
                final View view = layoutManager.findViewByPosition(0);
                if (view != null && !openingAvatar) {
                    final int canScroll = view.getTop() - AndroidUtilities.dp(88);
                    if (!allowPullingDown && canScroll > dy) {
                        dy = canScroll;
//                        if (avatarsViewPager.hasImages() && avatarImage.getImageReceiver().hasNotThumb() && !isInLandscapeMode && !AndroidUtilities.isTablet()) {
//                            allowPullingDown = avatarBig == null;
//                        }
                        allowPullingDown = true;
                    } else if (allowPullingDown) {
                        if (dy >= canScroll) {
                            dy = canScroll;
                            allowPullingDown = false;
                        } else if (listView.getScrollState() == RecyclerListView.SCROLL_STATE_DRAGGING) {
                            if (!isPulledDown) {
                                dy /= 2;
                            }
                        }
                    }
                }
                return super.scrollVerticallyBy(dy, recycler, state);
            }
        };

//        FrameLayout frameLayout1 = new FrameLayout(context) {
//            @Override
//            protected void onDraw(Canvas canvas) {
//                Theme.chat_composeShadowDrawable = context.getDrawable(R.drawable.compose_panel_shadow);
//                int bottom = Theme.chat_composeShadowDrawable.getIntrinsicHeight();
//                Theme.chat_composeShadowDrawable.setBounds(0, 0, getMeasuredWidth(), bottom);
//                Theme.chat_composeShadowDrawable.draw(canvas);
//                canvas.drawRect(0, bottom, getMeasuredWidth(), getMeasuredHeight(), Theme.chat_composeBackgroundPaint);
//            }
//        };
//        frameLayout1.setWillNotDraw(false);
//        frameLayout.addView(frameLayout1, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 51, Gravity.LEFT | Gravity.BOTTOM));


//        TextView textView = new TextView(context);
//        textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText));
//        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
//        textView.setGravity(Gravity.CENTER);
//        textView.setText("Ban from group");
//
//        frameLayout1.addView(textView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER, 0, 1, 0, 0));

        topView = new TopView(context);
        topView.setBackgroundColor(Color.parseColor("#f06262"));
        frameLayout.addView(topView);

        avatarContainer = new FrameLayout(context);
        avatarContainer.setPivotX(0);
        avatarContainer.setPivotY(0);


        avatarImage = new AvatarImageView(context);
        //avatarImage.setRoundRadius(AndroidUtilities.dp(21));
        avatarImage.setPivotX(0);
        avatarImage.setPivotY(0);
        avatarContainer.addView(avatarImage, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        frameLayout.addView(avatarContainer, LayoutHelper.createFrame(42, 42, Gravity.TOP | Gravity.LEFT, 64, 0, 0, 0));

        frameLayout.addView(actionBar);

        for (int a = 0; a < nameTextView.length; a++) {
            if (playProfileAnimation == 0 && a == 0) {
                continue;
            }
            nameTextView[a] = new SimpleTextView(context);
            if (a == 1) {
                nameTextView[a].setTextColor(Theme.getColor(Theme.key_profile_title));
            } else {
                nameTextView[a].setTextColor(Theme.getColor(Theme.key_actionBarDefaultTitle));
            }
            nameTextView[a].setTextSize(18);
            nameTextView[a].setGravity(Gravity.LEFT);
            // nameTextView[a].setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            nameTextView[a].setLeftDrawableTopPadding(-AndroidUtilities.dp(1.3f));
            nameTextView[a].setPivotX(0);
            nameTextView[a].setPivotY(0);
            nameTextView[a].setAlpha(a == 0 ? 0.0f : 1.0f);
            if (a == 1) {
                // nameTextView[a].setScrollNonFitText(true);
                nameTextView[a].setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
            }
            frameLayout.addView(nameTextView[a], LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.LEFT | Gravity.TOP, 118, 0, a == 0 ? 48 : 0, 0));
        }

        for (int a = 0; a < onlineTextView.length; a++) {
            onlineTextView[a] = new SimpleTextView(context);
            if (a == 2) {
                onlineTextView[a].setTextColor(Theme.getColor(Theme.key_player_actionBarSubtitle));
            } else {
                onlineTextView[a].setTextColor(Theme.getColor(Theme.key_avatar_subtitleInProfileBlue));
            }
            onlineTextView[a].setTextSize(14);
            onlineTextView[a].setGravity(Gravity.LEFT);
            onlineTextView[a].setAlpha(a == 0 || a == 2 ? 0.0f : 1.0f);
            onlineTextView[a].setText("Last seen Dec 1");
            if (a > 0) {
                onlineTextView[a].setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
            }
            frameLayout.addView(onlineTextView[a], LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.LEFT | Gravity.TOP, 118, 0, a == 0 ? 48 : 8, 0));
        }


        listView.setPadding(0, AndroidUtilities.dp(88), 0, AndroidUtilities.dp(48));
        listView.setBottomGlowOffset(AndroidUtilities.dp(48));

        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        listView.setLayoutManager(layoutManager);
        listView.setGlowColor(0);
        listView.setPadding(0, AndroidUtilities.dp(88), 0, 0);
        listView.setAdapter(listAdapter);

        topView.setBackgroundColor(Color.parseColor("#f05252"));


        writeButton = new ImageView(context);
        Drawable drawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56), Theme.getColor(Theme.key_profile_actionBackground), Theme.getColor(Theme.key_profile_actionPressedBackground));
        if (Build.VERSION.SDK_INT < 21) {
            Drawable shadowDrawable = context.getResources().getDrawable(R.drawable.floating_shadow_profile).mutate();
            shadowDrawable.setColorFilter(new PorterDuffColorFilter(0xff000000, PorterDuff.Mode.MULTIPLY));
            CombinedDrawable combinedDrawable = new CombinedDrawable(shadowDrawable, drawable, 0, 0);
            combinedDrawable.setIconSize(AndroidUtilities.dp(56), AndroidUtilities.dp(56));
            drawable = combinedDrawable;
        }
        writeButton.setBackgroundDrawable(drawable);

        //TODO: change write button depending on userid

        writeButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_profile_actionIcon), PorterDuff.Mode.MULTIPLY));
        writeButton.setScaleType(ImageView.ScaleType.CENTER);
        if (Build.VERSION.SDK_INT >= 21) {
            StateListAnimator animator = new StateListAnimator();
            animator.addState(new int[]{android.R.attr.state_pressed}, ObjectAnimator.ofFloat(writeButton, View.TRANSLATION_Z, AndroidUtilities.dp(2), AndroidUtilities.dp(4)).setDuration(200));
            animator.addState(new int[]{}, ObjectAnimator.ofFloat(writeButton, View.TRANSLATION_Z, AndroidUtilities.dp(4), AndroidUtilities.dp(2)).setDuration(200));
            writeButton.setStateListAnimator(animator);
            writeButton.setOutlineProvider(new ViewOutlineProvider() {
                @SuppressLint("NewApi")
                @Override
                public void getOutline(View view, Outline outline) {
                    outline.setOval(0, 0, AndroidUtilities.dp(56), AndroidUtilities.dp(56));
                }
            });
        }
        frameLayout.addView(writeButton, LayoutHelper.createFrame(Build.VERSION.SDK_INT >= 21 ? 56 : 60, Build.VERSION.SDK_INT >= 21 ? 56 : 60, Gravity.RIGHT | Gravity.TOP, 0, 0, 16, 0));
        needLayout(false);

        listView.setOnItemClickListener((view,position,x,y) ->{


            if (getParentActivity() == null) {
                return;
            }

            if(position== enableDebugRow){
                TextCheckBoxCell textCheckBoxCell = (TextCheckBoxCell)view;
                settingsPref.edit().putBoolean("debug",!isDebugMode).commit();
                isDebugMode = !isDebugMode;
                textCheckBoxCell.setChecked(isDebugMode);
                listAdapter.notifyItemChanged(enableDebugRow);
            }
        });
        listView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener() {
            @Override
            public boolean onItemClick(View view, int position) {
                return true;
            }
        });
        listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if(newState == RecyclerView.SCROLL_STATE_DRAGGING){
                    AndroidUtilities.hideKeyboard(getParentActivity().getCurrentFocus());
                }
                if (openingAvatar && newState != RecyclerView.SCROLL_STATE_SETTLING) {
                    openingAvatar = false;
                }

            }
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                checkListViewScroll();
//                if (participantsMap != null && !usersEndReached && layoutManager.findLastVisibleItemPosition() > membersEndRow - 8) {
//                    getChannelParticipants(false);
//                }
            }
        });

        expandAnimator = ValueAnimator.ofFloat(0f, 1f);
        expandAnimator.addUpdateListener(anim -> {
            final int newTop = ActionBar.getCurrentActionBarHeight() + (actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0);
            final float value = AndroidUtilities.lerp(expandAnimatorValues, currentExpanAnimatorFracture = anim.getAnimatedFraction());

            avatarContainer.setScaleX(avatarScale);
            avatarContainer.setScaleY(avatarScale);
            avatarContainer.setTranslationX(AndroidUtilities.lerp(avatarX, 0f, value));
            avatarContainer.setTranslationY(AndroidUtilities.lerp((float) Math.ceil(avatarY), 0f, value));
            //avatarImage.setRoundRadius((int) AndroidUtilities.lerp(AndroidUtilities.dpf2(21f), 0f, value));

            if (extraHeight > AndroidUtilities.dp(88f) && expandProgress < 0.33f) {
                refreshNameAndOnlineXY();
            }

            final float k = AndroidUtilities.dpf2(8f);

            final float nameTextViewXEnd = AndroidUtilities.dpf2(16f) - nameTextView[1].getLeft();
            final float nameTextViewYEnd = newTop + extraHeight - AndroidUtilities.dpf2(38f) - nameTextView[1].getBottom();
            final float nameTextViewCx = k + nameX + (nameTextViewXEnd - nameX) / 2f;
            final float nameTextViewCy = k + nameY + (nameTextViewYEnd - nameY) / 2f;
            final float nameTextViewX = (1 - value) * (1 - value) * nameX + 2 * (1 - value) * value * nameTextViewCx + value * value * nameTextViewXEnd;
            final float nameTextViewY = (1 - value) * (1 - value) * nameY + 2 * (1 - value) * value * nameTextViewCy + value * value * nameTextViewYEnd;

            final float onlineTextViewXEnd = AndroidUtilities.dpf2(16f) - onlineTextView[1].getLeft();
            final float onlineTextViewYEnd = newTop + extraHeight - AndroidUtilities.dpf2(18f) - onlineTextView[1].getBottom();
            final float onlineTextViewCx = k + onlineX + (onlineTextViewXEnd - onlineX) / 2f;
            final float onlineTextViewCy = k + onlineY + (onlineTextViewYEnd - onlineY) / 2f;
            final float onlineTextViewX = (1 - value) * (1 - value) * onlineX + 2 * (1 - value) * value * onlineTextViewCx + value * value * onlineTextViewXEnd;
            final float onlineTextViewY = (1 - value) * (1 - value) * onlineY + 2 * (1 - value) * value * onlineTextViewCy + value * value * onlineTextViewYEnd;

            nameTextView[1].setTranslationX(nameTextViewX);
            nameTextView[1].setTranslationY(nameTextViewY);
            onlineTextView[1].setTranslationX(onlineTextViewX);
            onlineTextView[1].setTranslationY(onlineTextViewY);
            onlineTextView[2].setTranslationX(onlineTextViewX);
            onlineTextView[2].setTranslationY(onlineTextViewY);

            final Object onlineTextViewTag = onlineTextView[1].getTag();
            int statusColor;
            if (onlineTextViewTag instanceof String) {
                statusColor = Theme.getColor((String) onlineTextViewTag);
            } else {
                statusColor = Theme.getColor(Theme.key_avatar_subtitleInProfileBlue);
            }
            onlineTextView[1].setTextColor(ColorUtils.blendARGB(statusColor, Color.argb(179, 255, 255, 255), value));
            if (extraHeight > AndroidUtilities.dp(88f)) {
                nameTextView[1].setPivotY(AndroidUtilities.lerp(0, nameTextView[1].getMeasuredHeight(), value));
                nameTextView[1].setScaleX(AndroidUtilities.lerp(1.12f, 1.67f, value));
                nameTextView[1].setScaleY(AndroidUtilities.lerp(1.12f, 1.67f, value));
            }
            needLayoutText(Math.min(1f, extraHeight / AndroidUtilities.dp(88f)));


            nameTextView[1].setTextColor(ColorUtils.blendARGB(Theme.getColor(Theme.key_profile_title), Color.WHITE, value));
           // actionBar.setItemsColor(ColorUtils.blendARGB(Theme.getColor(Theme.key_actionBarDefaultIcon), Color.WHITE, value), false);

            final FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) avatarContainer.getLayoutParams();
            params.width = (int) AndroidUtilities.lerp(AndroidUtilities.dpf2(42f), listView.getMeasuredWidth() / avatarScale, value);
            params.height = (int) AndroidUtilities.lerp(AndroidUtilities.dpf2(42f), (extraHeight + newTop) / avatarScale, value);
            params.leftMargin = (int) AndroidUtilities.lerp(AndroidUtilities.dpf2(64f), 0f, value);
            avatarContainer.requestLayout();

        });

        expandAnimator.setInterpolator(CubicBezierInterpolator.EASE_BOTH);
        expandAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                actionBar.setItemsBackgroundColor(isPulledDown ? Theme.ACTION_BAR_WHITE_SELECTOR_COLOR : Theme.getColor(Theme.key_avatar_actionBarSelectorBlue));
//              avatarImage.clearForeground();
                avatarImage.invalidate();
//                doNotSetForeground = false;
            }
        });

        updateUserProfile();
        return fragmentView;
    }

    private void updateUserProfile() {
        nameTextView[1].setText(userModel.getName());
        Glide.with(avatarImage).load(userModel.getPhotoUrl()).into(avatarImage);
    }

    private void needLayoutText(float diff) {
        FrameLayout.LayoutParams layoutParams;
        float scale = nameTextView[1].getScaleX();
        float maxScale = extraHeight > AndroidUtilities.dp(88f) ? 1.67f : 1.12f;

        if (extraHeight > AndroidUtilities.dp(88f) && scale != maxScale) {
            return;
        }

        int viewWidth = AndroidUtilities.isTablet() ? AndroidUtilities.dp(490) : AndroidUtilities.displaySize.x;

        ActionBarMenuItem item = null; //avatarsViewPagerIndicatorView.getSecondaryMenuItem();
        int buttonsWidth = AndroidUtilities.dp(118 + 8 + (40 + (item != null ? 48 * (1.0f - mediaHeaderAnimationProgress) : 0) + (videoCallItemVisible ? 48 * (1.0f - mediaHeaderAnimationProgress) : 0)));
        int minWidth = viewWidth - buttonsWidth;

        int width = (int) (viewWidth - buttonsWidth * Math.max(0.0f, 1.0f - (diff != 1.0f ? diff * 0.15f / (1.0f - diff) : 1.0f)) - nameTextView[1].getTranslationX());
        float width2 = nameTextView[1].getPaint().measureText(nameTextView[1].getText().toString()) * scale + nameTextView[1].getSideDrawablesSize();
        layoutParams = (FrameLayout.LayoutParams) nameTextView[1].getLayoutParams();
        int prevWidth = layoutParams.width;
        if (width < width2) {
            layoutParams.width = Math.max(minWidth, (int) Math.ceil((width - AndroidUtilities.dp(24)) / (scale + ((maxScale - scale) * 7.0f))));
        } else {
            layoutParams.width = (int) Math.ceil(width2);
        }
        layoutParams.width = (int) Math.min((viewWidth - nameTextView[1].getX()) / scale - AndroidUtilities.dp(8), layoutParams.width);
        if (layoutParams.width != prevWidth) {
            nameTextView[1].requestLayout();
        }

        width2 = onlineTextView[1].getPaint().measureText(onlineTextView[1].getText().toString());
        layoutParams = (FrameLayout.LayoutParams) onlineTextView[1].getLayoutParams();
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) onlineTextView[2].getLayoutParams();
        prevWidth = layoutParams.width;

        layoutParams2.rightMargin = layoutParams.rightMargin = (int) Math.ceil(onlineTextView[1].getTranslationX() + AndroidUtilities.dp(8) + AndroidUtilities.dp(40) * (1.0f - diff));
        if (width < width2) {
            layoutParams2.width = layoutParams.width = (int) Math.ceil(width);
        } else {
            layoutParams2.width = layoutParams.width = LayoutHelper.WRAP_CONTENT;
        }
        if (prevWidth != layoutParams.width) {
            onlineTextView[1].requestLayout();
            onlineTextView[2].requestLayout();
        }
    }

    private void checkListViewScroll() {
        if (listView.getVisibility() != View.VISIBLE) {
            return;
        }
        if (listView.getChildCount() <= 0 || openAnimationInProgress) {
            return;
        }

        View child = listView.getChildAt(0);
        RecyclerListView.Holder holder = (RecyclerListView.Holder) listView.findContainingViewHolder(child);
        int top = child.getTop();
        int newOffset = 0;
        int adapterPosition = holder != null ? holder.getAdapterPosition() : RecyclerView.NO_POSITION;
        if (top >= 0 && adapterPosition == 0) {
            newOffset = top;
        }

        if (extraHeight != newOffset) {
            extraHeight = newOffset;
            topView.invalidate();
            if (playProfileAnimation != 0) {
                allowProfileAnimation = extraHeight != 0;
            }
            needLayout(true);
        }
    }

    @Override
    protected ActionBar createActionBar(Context context) {
        ActionBar actionBar = new ActionBar(context);
        actionBar.setBackgroundColor(Color.TRANSPARENT);
        actionBar.setBackButtonDrawable(new BackDrawable(false));
        actionBar.setCastShadows(false);
        actionBar.setAddToContainer(false);
        actionBar.setOccupyStatusBar(Build.VERSION.SDK_INT >= 21 && !AndroidUtilities.isTablet());
        return actionBar;
    }

    @Override
    protected AnimatorSet onCustomTransitionAnimation(final boolean isOpen, final Runnable callback) {
        if (playProfileAnimation != 0 && allowProfileAnimation && !isPulledDown) {
            final AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.setDuration(playProfileAnimation == 2 ? 250 : 180);
            listView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            ActionBarMenu menu = actionBar.createMenu();
            if (menu.getItem(10) == null) {
                if (animatingItem == null) {
                    animatingItem = menu.addItem(10, R.drawable.ic_ab_other);
                }
            }
            if (isOpen) {
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) onlineTextView[1].getLayoutParams();
                layoutParams.rightMargin = (int) (-21 * AndroidUtilities.density + AndroidUtilities.dp(8));
                onlineTextView[1].setLayoutParams(layoutParams);

                if (playProfileAnimation != 2) {
                    int width = (int) Math.ceil(AndroidUtilities.displaySize.x - AndroidUtilities.dp(118 + 8) + 21 * AndroidUtilities.density);
                    float width2 = nameTextView[1].getPaint().measureText(nameTextView[1].getText().toString()) * 1.12f + nameTextView[1].getSideDrawablesSize();
                    layoutParams = (FrameLayout.LayoutParams) nameTextView[1].getLayoutParams();
                    if (width < width2) {
                        layoutParams.width = (int) Math.ceil(width / 1.12f);
                    } else {
                        layoutParams.width = LayoutHelper.WRAP_CONTENT;
                    }
                    nameTextView[1].setLayoutParams(layoutParams);

                    initialAnimationExtraHeight = AndroidUtilities.dp(88f);
                } else {
                    layoutParams = (FrameLayout.LayoutParams) nameTextView[1].getLayoutParams();
                    layoutParams.width = (int) ((AndroidUtilities.displaySize.x - AndroidUtilities.dp(32)) / 1.67f);
                    nameTextView[1].setLayoutParams(layoutParams);
                }
                fragmentView.setBackgroundColor(0);
                setAnimationProgress(0);
                ArrayList<Animator> animators = new ArrayList<>();
                animators.add(ObjectAnimator.ofFloat(this, "animationProgress", 0.0f, 1.0f));
                if (writeButton != null && writeButton.getTag() == null) {
                    writeButton.setScaleX(0.2f);
                    writeButton.setScaleY(0.2f);
                    writeButton.setAlpha(0.0f);
                    animators.add(ObjectAnimator.ofFloat(writeButton, View.SCALE_X, 1.0f));
                    animators.add(ObjectAnimator.ofFloat(writeButton, View.SCALE_Y, 1.0f));
                    animators.add(ObjectAnimator.ofFloat(writeButton, View.ALPHA, 1.0f));
                }
                if (playProfileAnimation == 2) {
                    //avatarColor = AndroidUtilities.calcBitmapColor(avatarImage.getImageReceiver().getBitmap());
                    nameTextView[1].setTextColor(Color.WHITE);
                    onlineTextView[1].setTextColor(Color.argb(179, 255, 255, 255));
                    actionBar.setItemsBackgroundColor(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR);
                   // overlaysView.setOverlaysVisible();
                }
                for (int a = 0; a < 2; a++) {
                    onlineTextView[a].setAlpha(a == 0 ? 1.0f : 0.0f);
                    nameTextView[a].setAlpha(a == 0 ? 1.0f : 0.0f);
                    animators.add(ObjectAnimator.ofFloat(onlineTextView[a], View.ALPHA, a == 0 ? 0.0f : 1.0f));
                    animators.add(ObjectAnimator.ofFloat(nameTextView[a], View.ALPHA, a == 0 ? 0.0f : 1.0f));
                }
                if (animatingItem != null) {
                    animatingItem.setAlpha(1.0f);
                    animators.add(ObjectAnimator.ofFloat(animatingItem, View.ALPHA, 0.0f));
                }
//                if (callItemVisible) {
//                    callItem.setAlpha(0.0f);
//                    animators.add(ObjectAnimator.ofFloat(callItem, View.ALPHA, 1.0f));
//                }
//                if (videoCallItemVisible) {
//                    videoCallItem.setAlpha(0.0f);
//                    animators.add(ObjectAnimator.ofFloat(videoCallItem, View.ALPHA, 1.0f));
//                }
//                if (editItemVisible) {
//                    editItem.setAlpha(0.0f);
//                    animators.add(ObjectAnimator.ofFloat(editItem, View.ALPHA, 1.0f));
//                }
                animatorSet.playTogether(animators);
            } else {
                initialAnimationExtraHeight = extraHeight;
                ArrayList<Animator> animators = new ArrayList<>();
                animators.add(ObjectAnimator.ofFloat(this, "animationProgress", 1.0f, 0.0f));
                if (writeButton != null) {
                    animators.add(ObjectAnimator.ofFloat(writeButton, View.SCALE_X, 0.2f));
                    animators.add(ObjectAnimator.ofFloat(writeButton, View.SCALE_Y, 0.2f));
                    animators.add(ObjectAnimator.ofFloat(writeButton, View.ALPHA, 0.0f));
                }
                for (int a = 0; a < 2; a++) {
                    animators.add(ObjectAnimator.ofFloat(onlineTextView[a], View.ALPHA, a == 0 ? 1.0f : 0.0f));
                    animators.add(ObjectAnimator.ofFloat(nameTextView[a], View.ALPHA, a == 0 ? 1.0f : 0.0f));
                }
                if (animatingItem != null) {
                    animatingItem.setAlpha(0.0f);
                    animators.add(ObjectAnimator.ofFloat(animatingItem, View.ALPHA, 1.0f));
                }
//                if (callItemVisible) {
//                    callItem.setAlpha(1.0f);
//                    animators.add(ObjectAnimator.ofFloat(callItem, View.ALPHA, 0.0f));
//                }
//                if (videoCallItemVisible) {
//                    videoCallItem.setAlpha(1.0f);
//                    animators.add(ObjectAnimator.ofFloat(videoCallItem, View.ALPHA, 0.0f));
//                }
//                if (editItemVisible) {
//                    editItem.setAlpha(1.0f);
//                    animators.add(ObjectAnimator.ofFloat(editItem, View.ALPHA, 0.0f));
//                }
                animatorSet.playTogether(animators);
            }
            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    listView.setLayerType(View.LAYER_TYPE_NONE, null);
                    if (animatingItem != null) {
                        ActionBarMenu menu = actionBar.createMenu();
                        menu.clearItems();
                        animatingItem = null;
                    }
                    callback.run();
                    if (playProfileAnimation == 2) {
                        playProfileAnimation = 1;
                       //avatarImage.setAlpha(1.0f);
                       // avatarContainer.setVisibility(View.GONE);
                        //avatarsViewPager.resetCurrentItem();
                        //avatarsViewPager.setVisibility(View.VISIBLE);
                    }
                }
            });
            animatorSet.setInterpolator(playProfileAnimation == 2 ? CubicBezierInterpolator.DEFAULT : new DecelerateInterpolator());

            AndroidUtilities.runOnUIThread(animatorSet::start, 50);
            return animatorSet;
        }
        return null;
    }

    public void setAnimationProgress(float progress){
        animationProgress = progress;

        listView.setAlpha(progress);

        listView.setTranslationX(AndroidUtilities.dp(48) - AndroidUtilities.dp(48) * progress);

        int color;
        if (playProfileAnimation == 2 && avatarColor != 0) {
            color = avatarColor;
        } else {
            color = Color.parseColor("#f05252");
        }
        int actionBarColor = Color.parseColor("#f05252");
        int r = Color.red(actionBarColor);
        int g = Color.green(actionBarColor);
        int b = Color.blue(actionBarColor);
        int a;

        int rD = (int) ((Color.red(color) - r) * progress);
        int gD = (int) ((Color.green(color) - g) * progress);
        int bD = (int) ((Color.blue(color) - b) * progress);
        int aD;
        topView.setBackgroundColor(Color.rgb(r + rD, g + gD, b + bD));


        color = Theme.getColor(Theme.key_profile_title);
        int titleColor = Theme.getColor(Theme.key_actionBarDefaultTitle);
        r = Color.red(titleColor);
        g = Color.green(titleColor);
        b = Color.blue(titleColor);
        a = Color.alpha(titleColor);

        rD = (int) ((Color.red(color) - r) * progress);
        gD = (int) ((Color.green(color) - g) * progress);
        bD = (int) ((Color.blue(color) - b) * progress);
        aD = (int) ((Color.alpha(color) - a) * progress);

        for (int i = 0; i < 2; i++) {
            if (nameTextView[i] == null || i == 1 && playProfileAnimation == 2) {
                continue;
            }
            nameTextView[i].setTextColor(Color.argb(a + aD, r + rD, g + gD, b + bD));
        }

        color = Theme.getColor(Theme.key_profile_status);
        int subtitleColor = Theme.ACTION_BAR_SUBTITLE_COLOR;
        r = Color.red(subtitleColor);
        g = Color.green(subtitleColor);
        b = Color.blue(subtitleColor);
        a = Color.alpha(subtitleColor);
        rD = (int) ((Color.red(color) - r) * progress);
        gD = (int) ((Color.green(color) - g) * progress);
        bD = (int) ((Color.blue(color) - b) * progress);
        aD = (int) ((Color.alpha(color) - a) * progress);
        for (int i = 0; i < 2; i++) {
            if (onlineTextView[i] == null || i == 1 && playProfileAnimation == 2) {
                continue;
            }
            onlineTextView[i].setTextColor(Color.argb(a + aD, r + rD, g + gD, b + bD));
        }

        extraHeight = (int) (initialAnimationExtraHeight * progress);

        topView.invalidate();
        needLayout(true);

        fragmentView.invalidate();

    }

    private class TopView extends View{
        private int currentColor;
        private Paint paint = new Paint();

        public TopView(Context context){
            super(context);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(widthMeasureSpec) + AndroidUtilities.dp(3));
        }

        @Override
        public void setBackgroundColor(int color) {
            if (color != currentColor) {
                currentColor = color;
                paint.setColor(color);
                invalidate();
            }
        }


        @Override
        protected void onDraw(Canvas canvas) {
            final int height = ActionBar.getCurrentActionBarHeight() + (actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0);
            final float v = extraHeight + height; // + searchTransitionOffset;

            int y1 = (int) (v * (1.0f - mediaHeaderAnimationProgress));

            if (y1 != 0) {
                paint.setColor(currentColor);
                canvas.drawRect(0, 0, getMeasuredWidth(), y1, paint);
            }
            if (y1 != v) {
                int color = Theme.getColor(Theme.key_windowBackgroundWhite);
                paint.setColor(color);
                canvas.drawRect(0, y1, getMeasuredWidth(), v, paint);
            }

            if (parentLayout != null) {
                parentLayout.drawHeaderShadow(canvas, (int) (headerShadowAlpha * 255), (int) v);
            }
        }
    }

    public void setMediaHeaderVisible(boolean visible){
        if(mediaHeaderVisible == visible){
            return;
        }
        mediaHeaderVisible = visible;
        if (headerAnimatorSet != null) {
            headerAnimatorSet.cancel();
        }

        ArrayList<Animator> animators = new ArrayList<>();
        animators.add(ObjectAnimator.ofFloat(actionBar, ACTIONBAR_HEADER_PROGRESS, visible ? 1.0f : 0.0f));
        animators.add(ObjectAnimator.ofFloat(onlineTextView[1], View.ALPHA, visible ? 0.0f : 1.0f));
        animators.add(ObjectAnimator.ofFloat(onlineTextView[2], View.ALPHA, visible ? 1.0f : 0.0f));

        headerAnimatorSet = new AnimatorSet();
        headerAnimatorSet.playTogether(animators);
        headerAnimatorSet.setInterpolator(CubicBezierInterpolator.DEFAULT);
        headerAnimatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if(headerAnimatorSet != null){

                }else{

                }
                headerAnimatorSet = null;
            }
        });
        headerAnimatorSet.setDuration(150);
        headerAnimatorSet.start();
    }

    private void needLayout(boolean animated){
        final int newTop = (actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight();

        FrameLayout.LayoutParams layoutParams;
        if (listView != null && !openAnimationInProgress) {
            layoutParams = (FrameLayout.LayoutParams) listView.getLayoutParams();
            if (layoutParams.topMargin != newTop) {
                layoutParams.topMargin = newTop;
                listView.setLayoutParams(layoutParams);
            }
        }
        if(avatarContainer != null){
            final float diff = Math.min(1f, extraHeight / AndroidUtilities.dp(88f));
            listView.setTopGlowOffset((int) extraHeight);
            listView.setOverScrollMode(extraHeight > AndroidUtilities.dp(88f) && extraHeight < listView.getMeasuredWidth() - newTop ? View.OVER_SCROLL_NEVER : View.OVER_SCROLL_ALWAYS);

            if(writeButton != null){
                writeButton.setTranslationY((actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight() + extraHeight + 0 - AndroidUtilities.dp(29.5f));
                if (!openAnimationInProgress) {
                    boolean setVisible = diff > 0.2f;
                    if (setVisible) {
                        setVisible = true;
                    }
                    boolean currentVisible = writeButton.getTag() == null;
                    if (setVisible != currentVisible) {
                        if (setVisible) {
                            writeButton.setTag(null);
                        } else {
                            writeButton.setTag(0);
                        }
                        if (writeButtonAnimation != null) {
                            AnimatorSet old = writeButtonAnimation;
                            writeButtonAnimation = null;
                            old.cancel();
                        }
                        if (animated) {
                            writeButtonAnimation = new AnimatorSet();
                            if (setVisible) {
                                writeButtonAnimation.setInterpolator(new DecelerateInterpolator());
                                writeButtonAnimation.playTogether(
                                        ObjectAnimator.ofFloat(writeButton, View.SCALE_X, 1.0f),
                                        ObjectAnimator.ofFloat(writeButton, View.SCALE_Y, 1.0f),
                                        ObjectAnimator.ofFloat(writeButton, View.ALPHA, 1.0f)
                                );
                            } else {
                                writeButtonAnimation.setInterpolator(new AccelerateInterpolator());
                                writeButtonAnimation.playTogether(
                                        ObjectAnimator.ofFloat(writeButton, View.SCALE_X, 0.2f),
                                        ObjectAnimator.ofFloat(writeButton, View.SCALE_Y, 0.2f),
                                        ObjectAnimator.ofFloat(writeButton, View.ALPHA, 0.0f)
                                );
                            }
                            writeButtonAnimation.setDuration(150);
                            writeButtonAnimation.addListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    if (writeButtonAnimation != null && writeButtonAnimation.equals(animation)) {
                                        writeButtonAnimation = null;
                                    }
                                }
                            });
                            writeButtonAnimation.start();
                        } else {
                            writeButton.setScaleX(setVisible ? 1.0f : 0.2f);
                            writeButton.setScaleY(setVisible ? 1.0f : 0.2f);
                            writeButton.setAlpha(setVisible ? 1.0f : 0.0f);
                        }
                    }
                }
            }
            avatarX = -AndroidUtilities.dpf2(47f) * diff;
            avatarY = (actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight() / 2.0f * (1.0f + diff) - 21 * AndroidUtilities.density + 27 * AndroidUtilities.density * diff + actionBar.getTranslationY();
            float h = openAnimationInProgress ? initialAnimationExtraHeight : extraHeight;
            if(h > AndroidUtilities.dp(88) || isPulledDown){
                expandProgress = Math.max(0f, Math.min(1f, (h - AndroidUtilities.dp(88f)) / (listView.getMeasuredWidth() - newTop - AndroidUtilities.dp(88f))));
                avatarScale = AndroidUtilities.lerp((42f + 18f) / 42f, (42f + 42f + 18f) / 42f, Math.min(1f, expandProgress * 3f));
                final float durationFactor = Math.min(AndroidUtilities.dpf2(2000f), Math.max(AndroidUtilities.dpf2(1100f), Math.abs(listViewVelocityY))) / AndroidUtilities.dpf2(1100f);

                if(allowPullingDown &&(openingAvatar || expandProgress >= 0.33f)){
                    if (!isPulledDown) {

                        isPulledDown = true;
                        ///overlaysView.setOverlaysVisible(true, durationFactor);
                        //avatarsViewPagerIndicatorView.refreshVisibility(durationFactor);
                        expandAnimator.cancel();
                        float value = AndroidUtilities.lerp(expandAnimatorValues, currentExpanAnimatorFracture);
                        expandAnimatorValues[0] = value;
                        expandAnimatorValues[1] = 1f;
                        expandAnimator.setDuration((long) ((1f - value) * 250f / durationFactor));
                        expandAnimator.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                //setForegroundImage(false);
                                //avatarsViewPager.setAnimatedFileMaybe(avatarImage.getImageReceiver().getAnimation());
                                //avatarsViewPager.resetCurrentItem();
                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                expandAnimator.removeListener(this);
                                 topView.setBackgroundColor(Color.BLACK);
                               // avatarContainer.setVisibility(View.GONE);
                                //avatarsViewPager.setVisibility(View.VISIBLE);
                            }
                        });
                        expandAnimator.start();
                    }

                    if (!expandAnimator.isRunning()) {
                        float additionalTranslationY = 0;
                        if (openAnimationInProgress && playProfileAnimation == 2) {
                            additionalTranslationY = -(1.0f - animationProgress) * AndroidUtilities.dp(50);
                        }
                        nameTextView[1].setTranslationX(AndroidUtilities.dpf2(16f) - nameTextView[1].getLeft());
                        nameTextView[1].setTranslationY(newTop + h - AndroidUtilities.dpf2(38f) - nameTextView[1].getBottom() + additionalTranslationY);
                        onlineTextView[1].setTranslationX(AndroidUtilities.dpf2(16f) - onlineTextView[1].getLeft());
                        onlineTextView[1].setTranslationY(newTop + h - AndroidUtilities.dpf2(18f) - onlineTextView[1].getBottom() + additionalTranslationY);
                        onlineTextView[2].setTranslationX(onlineTextView[1].getTranslationX());
                        onlineTextView[2].setTranslationY(onlineTextView[1].getTranslationY());
                    }
                }else{
                    if (isPulledDown) {
                        isPulledDown = false;

                        //overlaysView.setOverlaysVisible(false, durationFactor);
                        //avatarsViewPagerIndicatorView.refreshVisibility(durationFactor);
                        expandAnimator.cancel();
                        //avatarImage.getImageReceiver().setAllowStartAnimation(true);
                        //avatarImage.getImageReceiver().startAnimation();

                        float value = AndroidUtilities.lerp(expandAnimatorValues, currentExpanAnimatorFracture);
                        expandAnimatorValues[0] = value;
                        expandAnimatorValues[1] = 0f;
//                        if (!isInLandscapeMode) {
//                            expandAnimator.setDuration((long) (value * 250f / durationFactor));
//                        } else {
//                            expandAnimator.setDuration(0);
//                        }
                        expandAnimator.setDuration((long) (value * 250f / durationFactor));
                        topView.setBackgroundColor(Color.parseColor("#f05252"));

//                        if (!doNotSetForeground) {
//                            BackupImageView imageView = avatarsViewPager.getCurrentItemView();
//                            if (imageView != null) {
//                                avatarImage.setForegroundImageDrawable(imageView.getImageReceiver().getDrawableSafe());
//                            }
//                        }
                       // avatarImage.setForegroundAlpha(1f);
                        avatarContainer.setVisibility(View.VISIBLE);
//                        avatarsViewPager.setVisibility(View.GONE);
                        expandAnimator.start();
                    }
                    avatarContainer.setScaleX(avatarScale);
                    avatarContainer.setScaleY(avatarScale);
                    if (expandAnimator == null || !expandAnimator.isRunning()) {
                        refreshNameAndOnlineXY();
                        nameTextView[1].setTranslationX(nameX);
                        nameTextView[1].setTranslationY(nameY);
                        onlineTextView[1].setTranslationX(onlineX);
                        onlineTextView[1].setTranslationY(onlineY);
                        onlineTextView[2].setTranslationX(onlineX);
                        onlineTextView[2].setTranslationY(onlineY);
                    }
                }
            }
            if(openAnimationInProgress && playProfileAnimation == 2){
                float avX = 0;
                float avY = (actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight() / 2.0f - 21 * AndroidUtilities.density + actionBar.getTranslationY();

                nameTextView[0].setTranslationX(0);
                nameTextView[0].setTranslationY((float) Math.floor(avY) + AndroidUtilities.dp(1.3f));
                onlineTextView[0].setTranslationX(0);
                onlineTextView[0].setTranslationY((float) Math.floor(avY) + AndroidUtilities.dp(24));
                nameTextView[0].setScaleX(1.0f);
                nameTextView[0].setScaleY(1.0f);

                nameTextView[1].setPivotY(nameTextView[1].getMeasuredHeight());
                nameTextView[1].setScaleX(1.67f);
                nameTextView[1].setScaleY(1.67f);

                avatarScale = AndroidUtilities.lerp(1.0f, (42f + 42f + 18f) / 42f, animationProgress);

                avatarContainer.setTranslationX(AndroidUtilities.lerp(avX, 0, animationProgress));
                avatarContainer.setTranslationY(AndroidUtilities.lerp((float) Math.ceil(avY), 0f, animationProgress));
                avatarContainer.setScaleX(avatarScale);
                avatarContainer.setScaleY(avatarScale);

                final FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) avatarContainer.getLayoutParams();
                params.width = params.height = (int) AndroidUtilities.lerp(AndroidUtilities.dpf2(42f), (extraHeight + newTop) / avatarScale, animationProgress);
                params.leftMargin = (int) AndroidUtilities.lerp(AndroidUtilities.dpf2(64f), 0f, animationProgress);
                avatarContainer.requestLayout();


            }else if(extraHeight <= AndroidUtilities.dp(88f)) {
                avatarScale = (42 + 18 * diff) / 42.0f;
                float nameScale = 1.0f + 0.12f * diff;
                if (expandAnimator == null || !expandAnimator.isRunning()) {
                    avatarContainer.setScaleX(avatarScale);
                    avatarContainer.setScaleY(avatarScale);
                    avatarContainer.setTranslationX(avatarX);
                    avatarContainer.setTranslationY((float) Math.ceil(avatarY));
                }
                nameX = -21 * AndroidUtilities.density * diff;
                nameY = (float) Math.floor(avatarY) + AndroidUtilities.dp(1.3f) + AndroidUtilities.dp(7) * diff;
                onlineX = -21 * AndroidUtilities.density * diff;
                onlineY = (float) Math.floor(avatarY) + AndroidUtilities.dp(24) + (float) Math.floor(11 * AndroidUtilities.density) * diff;

                for (int a = 0; a < nameTextView.length; a++) {
                    if (nameTextView[a] == null) {
                        continue;
                    }
                    if (expandAnimator == null || !expandAnimator.isRunning()) {
                        nameTextView[a].setTranslationX(nameX);
                        nameTextView[a].setTranslationY(nameY);

                        onlineTextView[a].setTranslationX(onlineX);
                        onlineTextView[a].setTranslationY(onlineY);
                        if (a == 1) {
                            onlineTextView[2].setTranslationX(onlineX);
                            onlineTextView[2].setTranslationY(onlineY);
                        }
                    }
                    nameTextView[a].setScaleX(nameScale);
                    nameTextView[a].setScaleY(nameScale);
                }
            }
            if (!openAnimationInProgress && (expandAnimator == null || !expandAnimator.isRunning())) {
                needLayoutText(diff);
            }
        }

        if (isPulledDown) { //|| overlaysView.animator != null && overlaysView.animator.isRunning()) {
            //final ViewGroup.LayoutParams overlaysLp = overlaysView.getLayoutParams();
            //overlaysLp.width = listView.getMeasuredWidth();
            //overlaysLp.height = (int) (extraHeight + newTop);
            //overlaysView.requestLayout();
        }
    }

    private void refreshNameAndOnlineXY() {
        nameX = AndroidUtilities.dp(-21f) + avatarContainer.getMeasuredWidth() * (avatarScale - (42f + 18f) / 42f);
        nameY = (float) Math.floor(avatarY) + AndroidUtilities.dp(1.3f) + AndroidUtilities.dp(7f) + avatarContainer.getMeasuredHeight() * (avatarScale - (42f + 18f) / 42f) / 2f;
        onlineX = AndroidUtilities.dp(-21f) + avatarContainer.getMeasuredWidth() * (avatarScale - (42f + 18f) / 42f);
        onlineY = (float) Math.floor(avatarY) + AndroidUtilities.dp(24) + (float) Math.floor(11 * AndroidUtilities.density) + avatarContainer.getMeasuredHeight() * (avatarScale - (42f + 18f) / 42f) / 2f;
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter{

        private Context mContext;

        public ListAdapter(Context context){
            mContext = context;
        }
        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return false;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = new HeaderCell(mContext,23);;
            switch (viewType){
                case 1:{
                    view = new HeaderCell(mContext,23);
                    break;
                }
                case 2:{
                    final TextDetailCell textDetailCell = new TextDetailCell(mContext);
                    textDetailCell.setContentDescriptionValueFirst(true);
                    view = textDetailCell;
                    break;
                }
                case 3:{
                    final TextCheckBoxCell textCheckBoxCell = new TextCheckBoxCell(mContext);
                    view = textCheckBoxCell;
                    break;
                }
            }
            return new RecyclerListView.Holder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            switch(holder.getItemViewType()){
                case 1:{
                    HeaderCell headerCell = (HeaderCell)holder.itemView;
                    if(position == numberSectionRow){
                        headerCell.setText("Account");
                    }else if(position == debugHeaderRow){
                        headerCell.setText("Developer options");
                    }
                    break;
                }
                case 2:{
                    TextDetailCell detailCell = (TextDetailCell) holder.itemView;
                    if(position == usernameRow){
                        detailCell.setTextAndValue("@" + userModel.getUsername(), "Username",false);
                    }else if(position == numberRow){
                        detailCell.setTextAndValue("Unknown number", "Tap to change your phone number", true);
                    }else if(position == setUsernameRow){
                        detailCell.setTextAndValue("@" + userModel.getUsername(), "Username", true);
                    }else if(position == bioRow){
                        String value;
                       // if(userModel.get)
                    }
                    break;
                }
                case 3:{
                    TextCheckBoxCell checkBoxCell = (TextCheckBoxCell) holder.itemView;
                    if(position == enableDebugRow){
                        String value;
                        if(isDebugMode){
                            value = "Disable debug mode";
                        }else{
                            value = "Enable debug mode";
                        }
                        checkBoxCell.setTextAndCheck(value, isDebugMode,false);
                    }
                    break;
                }
            }
        }

        @Override
        public int getItemCount() {
            return rowCount;
        }

        @Override
        public int getItemViewType(int position) {
            if(position == numberSectionRow || position == debugHeaderRow){
                return 1;
            }else if(position == usernameRow || position == bioRow || position == numberRow || position == setUsernameRow){
                return 2;
            }else if(position == enableDebugRow){
                return 3;
            }

            return 2;
        }
    }
    public void setProfileDetails(UserModel model){
        this.userModel = model;
    }

    private class NestedFrameLayout extends FrameLayout implements NestedScrollingParent3{
        private NestedScrollingParentHelper nestedScrollingParentHelper;

        public NestedFrameLayout(Context context) {
            super(context);
            nestedScrollingParentHelper = new NestedScrollingParentHelper(this);
        }

        @Override
        public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type, int[] consumed) {
//            if (target == listView && sharedMediaLayoutAttached) {
//                RecyclerListView innerListView = sharedMediaLayout.getCurrentListView();
//                int top = 0;// sharedMediaLayout.getTop();
//                if (top == 0) {
//                    consumed[1] = dyUnconsumed;
//                    innerListView.scrollBy(0, dyUnconsumed);
//                }
//            }
        }
        @Override
        public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {

        }

        @Override
        public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
            return super.onNestedPreFling(target, velocityX, velocityY);
        }
        @Override
        public void onNestedPreScroll(View target, int dx, int dy, int[] consumed, int type) {
            if (target == listView){ //&& sharedMediaRow != -1 && sharedMediaLayoutAttached) {
                boolean searchVisible = actionBar.isSearchFieldVisible();
                int t = 0;//sharedMediaLayout.getTop();
                if (dy < 0) {
                    boolean scrolledInner = false;
                    if (t <= 0) {
                       // RecyclerListView innerListView = sharedMediaLayout.getCurrentListView();
                        //LinearLayoutManager linearLayoutManager = (LinearLayoutManager) innerListView.getLayoutManager();
                        //int pos = linearLayoutManager.findFirstVisibleItemPosition();
//                        if (pos != RecyclerView.NO_POSITION) {
//                            //RecyclerView.ViewHolder holder = innerListView.findViewHolderForAdapterPosition(pos);
//                            int top = holder != null ? holder.itemView.getTop() : -1;
//                            int paddingTop = innerListView.getPaddingTop();
//                            if (top != paddingTop || pos != 0) {
//                                consumed[1] = pos != 0 ? dy : Math.max(dy, (top - paddingTop));
//                                innerListView.scrollBy(0, dy);
//                                scrolledInner = true;
//                            }
//                        }
                    }
                    if (searchVisible) {
                        if (!scrolledInner && t < 0) {
                            consumed[1] = dy - Math.max(t, dy);
                        } else {
                            consumed[1] = dy;
                        }
                    }
                } else {
                    if (searchVisible) {
                       // RecyclerListView innerListView = sharedMediaLayout.getCurrentListView();
                        consumed[1] = dy;
                        if (t > 0) {
                            consumed[1] -= Math.min(consumed[1], dy);
                        }
                        if (consumed[1] > 0) {
                           // innerListView.scrollBy(0, consumed[1]);
                        }
                    }
                }
            }
        }

        @Override
        public boolean onStartNestedScroll(View child, View target, int axes, int type) {
            //return sharedMediaRow != -1 &&
            return axes == ViewCompat.SCROLL_AXIS_VERTICAL;
        }

        @Override
        public void onNestedScrollAccepted(View child, View target, int axes, int type) {
            nestedScrollingParentHelper.onNestedScrollAccepted(child, target, axes);
        }

        @Override
        public void onStopNestedScroll(View target, int type) {
            nestedScrollingParentHelper.onStopNestedScroll(target);
        }
    }

    @Override
    protected void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if(isOpen){
            if(!backward){
                if (playProfileAnimation != 0 && allowProfileAnimation) {
                    openAnimationInProgress = false;
                }
                if (!fragmentOpened) {
                    fragmentOpened = true;
//                    firstLayout = true;
//                    lastMeasuredContentHeight = -1;
                    fragmentView.requestLayout();
                }
            }
            
        }
        transitionAnimationInProress = false;
    }

    @Override
    protected void onTransitionAnimationStart(boolean isOpen, boolean backward) {
        if ((!isOpen && backward || isOpen && !backward) && playProfileAnimation != 0 && allowProfileAnimation && !isPulledDown) {
            openAnimationInProgress = true;
        }
        transitionAnimationInProress = true;
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();

    }

    public void setPlayProfileAnimation(int type){
        if(!AndroidUtilities.isTablet()){
            playProfileAnimation = type;
        }
    }

    @Override
    public boolean onFragmentCreate() {
        updateRowsIds();
        return true;
    }

    public class AvatarImageView extends androidx.appcompat.widget.AppCompatImageView{

        public AvatarImageView(Context context) {
            super(context);
        }
    }

    public void updateRowsIds(){
        int previousCount = rowCount;
        rowCount = 0;

        numberSectionRow = -1;
        numberRow = -1;
        setUsernameRow = -1;
        bioRow = -1;
        enableDebugRow = -1;
        
        usernameRow = -1;
        debugHeaderRow = -1;
        if(userModel.getUid().equals(UserConfig.getUid())){
            numberSectionRow = rowCount++;
            numberRow = rowCount++;
            setUsernameRow = rowCount++;
            //bioRow = rowCount++;
            debugHeaderRow = rowCount++;
            enableDebugRow = rowCount++;
        }else{
            usernameRow = rowCount++;
        }
    }
}