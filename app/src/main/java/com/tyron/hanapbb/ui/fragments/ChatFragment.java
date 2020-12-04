package com.tyron.hanapbb.ui.fragments;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.preference.PreferenceManager;
import android.recyclerview.widget.ItemTouchHelper;
import android.recyclerview.widget.LinearSmoothScroller;
import android.recyclerview.widget.RecyclerView;
import android.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import com.opensooq.supernova.gligar.GligarPicker;
import com.tyron.hanapbb.R;
import com.tyron.hanapbb.emoji.EmojiEditText;
import com.tyron.hanapbb.emoji.EmojiPopup;
import com.tyron.hanapbb.emoji.listeners.OnEmojiPopupDismissListener;
import com.tyron.hanapbb.emoji.listeners.OnEmojiPopupShownListener;
import com.tyron.hanapbb.emoji.listeners.OnSoftKeyboardCloseListener;
import com.tyron.hanapbb.messenger.AndroidUtilities;
import com.tyron.hanapbb.messenger.MessageObject;
import com.tyron.hanapbb.messenger.NotificationCenter;
import com.tyron.hanapbb.messenger.UserConfig;
import com.tyron.hanapbb.ui.PhotoAlbumPickerActivity;
import com.tyron.hanapbb.ui.actionbar.ActionBar;
import com.tyron.hanapbb.ui.actionbar.ActionBarMenu;
import com.tyron.hanapbb.ui.actionbar.BackDrawable;
import com.tyron.hanapbb.ui.actionbar.BaseFragment;
import com.tyron.hanapbb.ui.actionbar.MenuDrawable;
import com.tyron.hanapbb.ui.actionbar.Theme;
import com.tyron.hanapbb.ui.adapters.ChatAdapter;
import com.tyron.hanapbb.ui.cells.ChatAvatarCell;
import com.tyron.hanapbb.ui.components.CubicBezierInterpolator;
import com.tyron.hanapbb.ui.components.ISwipeControllerActions;
import com.tyron.hanapbb.ui.components.LayoutHelper;
import com.tyron.hanapbb.ui.components.MyItemAnimator;
import com.tyron.hanapbb.ui.components.SwipeController;
import com.tyron.hanapbb.ui.models.MessagesModel;
import com.tyron.hanapbb.ui.models.UserModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ChatFragment extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {

    private Context context;
    private final GligarPicker picker = new GligarPicker();
    private RecyclerView recyclerview;

    private final List<MessagesModel> loadModel = new ArrayList<MessagesModel>();
    private List<MessagesModel> chatModel;

    private final FirebaseDatabase firebase = FirebaseDatabase.getInstance();
    private final DatabaseReference usersRef = firebase.getReference("users");
    private final DatabaseReference chatRef = firebase.getReference("chats");
    private Query chatQuery;
    private String receiver_id;

    long last_text_edit = 0;
    long delay = 1500;

    private UserModel receiverModel;

    PhotoAlbumPickerActivity photoAlbumPickerActivity;


    private TextView textview_chatname;
    private TextView textview_reply_name;
    private TextView textview_reply_message;
    private static String chat_name;
    private ImageView btn_close_reply;

    private LinearLayout linear_send;
    private LinearLayout bottom_panel;
    private ImageButton attachment_button;
    private ImageButton emoji_button;
    private EmojiEditText message_edittext;
    private ChatAdapter adapter;
    private SwipeRefreshLayout refreshLayout;

    private ConstraintLayout reply_layout;
    private ConstraintLayout rootView;

    private final boolean isUser = false;
    private boolean firstLoad = true;
    private boolean isReply = true;
    private boolean debugMode;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private int replySelected = 0;

    private ViewGroup root;

    EmojiPopup emojiPopup;

    private int keyboard_height;
    private SharedPreferences settingsPref;


    ValueEventListener firstLoadEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            for (DataSnapshot data : snapshot.getChildren()) {
                MessagesModel model = data.getValue(MessagesModel.class);
                loadModel.add(model);
            }

            Collections.reverse(loadModel);
            for (int i = 0; i < (loadModel.isEmpty() ? 0 : loadModel.size()); i++) {
                MessagesModel model = loadModel.get(i);

                if (firstLoad) {
                    chatModel.add(0, model);
                } else {
                    if(chatModel.isEmpty()) {
                        chatModel.add(0,model);
                    }else{
                        if (!chatModel.get(0).getMessageId().equals(model.getMessageId())) {
                            chatModel.add(0, model);
                        }
                    }
                }
            }
            adapter.notifyItemRangeInserted(0,loadModel.size()-1);
            adapter.notifyItemChanged(0);
            if(!chatModel.isEmpty()) {
                chatRef.child(chat_id).child("messages").orderByKey().startAt(chatModel.get(0).getMessageId()).addChildEventListener(chatEventListener);
            }else{
                chatRef.child(chat_id).child("messages").addChildEventListener(chatEventListener);
            }
            firstLoad = false;
            refreshLayout.setRefreshing(false);
            loadModel.clear();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };
    ChildEventListener chatEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            if (!firstLoad) {
                MessagesModel addedMessage = snapshot.getValue(MessagesModel.class);

                if(chatModel.isEmpty()){
                    chatModel.add(addedMessage);
                    adapter.notifyItemInserted(adapter.getItemCount() + 1);
                }else {
                    MessagesModel prevMessage = chatModel.get(chatModel.size()- 1);
                    if (prevMessage.getMessageId().equals(addedMessage.getMessageId())) {

                    } else {
                        if (addedMessage.getTime() > prevMessage.getTime()) {
                            chatModel.add(addedMessage);

                            RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(context) {
                                @Override
                                protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                                    return super.calculateSpeedPerPixel(displayMetrics)- 0.5f;
                                }
                            };
                            smoothScroller.setTargetPosition(adapter.getItemCount() > 1? adapter.getItemCount() - 1 : 0 );
                            if(((LinearLayoutManager)recyclerview.getLayoutManager()).findLastCompletelyVisibleItemPosition() > adapter.getItemCount() - 4){
                                recyclerview.getLayoutManager().startSmoothScroll(smoothScroller);
                              //  recyclerview.getLayoutManager().smoothScrollToPosition(recyclerview, new RecyclerView.State(), adapter.getItemCount()-1);
                            }

                            adapter.notifyItemInserted(adapter.getItemCount() + 1);
                            adapter.notifyItemChanged(adapter.getItemCount() - 2);
                        }
                    }
                }
            }
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            int i = 0;
            MessagesModel model = snapshot.getValue(MessagesModel.class);
            for(Iterator<MessagesModel> iterator = chatModel.iterator(); iterator.hasNext();){
                MessagesModel it = iterator.next();
                if(it.getMessageId().equals(model.getMessageId())) {
                    chatModel.set(i,model);
                    adapter.notifyItemChanged(i,true);
                    Log.d("ITEM", "ITEM CHANGED AT " + i);
                    break;

                }
                i++;
            }
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot snapshot) {
            chatRef.child(chat_id).child("messages").child(snapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(!dataSnapshot.exists()){
                        int i = 0;
                        MessagesModel model = snapshot.getValue(MessagesModel.class);
                        for(Iterator<MessagesModel> iterator = chatModel.iterator(); iterator.hasNext();){
                            MessagesModel it = iterator.next();
                            if(it.getMessageId().equals(model.getMessageId())) {
                                iterator.remove();
                                adapter.notifyItemRemoved(i);
                                Log.d("ITEM", "ITEM REMOVED AT " + i);
                                break;

                            }
                            i++;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };
    private static String chat_id;
    private final int limit = 12;

    private ChatAvatarCell avatarCell;

    public ChatFragment(String chat_id) {
        ChatFragment.chat_id = chat_id;
    }


    @Override
    public View createView(Context context) {

        Theme.createCommonResources(context);

        this.context = context;

        LayoutInflater inflater = LayoutInflater.from(context);

        getParentActivity().getWindow().getDecorView().setSystemUiVisibility(0);

        fragmentView = new FrameLayout(context);
        View view = inflater.inflate(R.layout.activity_chat, (ViewGroup) fragmentView, false);
        recyclerview = view.findViewById(R.id.recyclerview1);

        ((ViewGroup) fragmentView).addView(view);

        avatarCell = new ChatAvatarCell(context,this, chat_id, receiverModel);
        actionBar.createActionMode();
       // actionBar.setExtraHeight(AndroidUtilities.dp(6));
        ActionBarMenu menu = actionBar.createMenu();


        avatarCell.setLayoutParams(LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT,LayoutHelper.MATCH_PARENT, Gravity.TOP | Gravity.START, 50,0,0,0));

        Drawable menuDrawable = new MenuDrawable();
        menu.addItem(542,avatarCell);
        menu.addItem(44, menuDrawable);

        BackDrawable backButton = new BackDrawable(false);
        actionBar.setBackButtonDrawable(backButton);
        actionBar.setBackgroundColor(Color.parseColor("#f05252"));

        actionBar.createActionMode();

        avatarCell.setOnClickListener(ignore -> avatarCell.clicked());

        initialize(view);
        initializeLogic();
        initKeyboardAnimation();
        listenForTypingUpdates();

        fetchProfile();


        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {

                }
            }
        });
        return fragmentView;
    }

    private void listenForTypingUpdates() {
        chatRef.child(chat_id).child("settings").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String  status = snapshot.child(receiver_id).getValue(String.class);
                    if(status != null) {
                        if (status.contains("typing")) {
                            avatarCell.setTypingAnimation(true);
                        }
                        if(status.contains("uploading")) {
                            avatarCell.setFileSendingAnimation(true);
                        }
                        if(status.equals("default")){
                            avatarCell.setTypingAnimation(false);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static String getName(){
        return chat_name;
    }
    public static String getChatId(){
        return chat_id;
    }
    private void fetchProfile() {
        usersRef.child(chat_id.replace(UserConfig.getUid(), "")).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel model = snapshot.getValue(UserModel.class);
                avatarCell.setUserModel(model);

                if(model != null) {
                    chat_name = model.getName();
                    avatarCell.setTitle(chat_name);
                    String url = model.getPhotoUrl();
                    avatarCell.setPicture(url);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initializeLogic() {
        receiver_id = chat_id.replace(UserConfig.getUid(), "");

        toggleReplyLayout();

        linear_send.setOnClickListener((v) -> {
            if (message_edittext.getText().toString().trim().length() > 0)
                sendMessage();
        });
        attachment_button.setOnClickListener((view) -> {
            //isUser = !isUser;
//            picker.withFragment(this);
//            picker.limit(1);
//            picker.disableCamera(true);
//            picker.show();

            Intent intent = getParentActivity().getIntent();
            photoAlbumPickerActivity = new PhotoAlbumPickerActivity(
                    new String[]{"image/jpeg"},
                    1,
                    true,
                    "Select a photo",
                    false
            );
            photoAlbumPickerActivity.setDelegate(mPhotoAlbumPickerActivityDelegate);
            presentFragment(photoAlbumPickerActivity,false,false);


//
//            BottomSheet.BottomSheetCell[] buttons = new BottomSheet.BottomSheetCell[5];
//
//            for (int a = 0; a < buttons.length; a++) {
//                buttons[a] = new BottomSheet.BottomSheetCell(context, 0);
//
//                buttons[a].setPadding(AndroidUtilities.dp(7), 0, AndroidUtilities.dp(7), 0);
//                buttons[a].setTag(a);
//                //buttons[a].setBackgroundDrawable(Theme.getSelectorDrawable(false));
//                String text;
//
//                switch (a) {
//                    case 0:
//                        text = "Forever";
//                        break;
//                    case 1:
//                        text = "Days";
//                        break;
//                    case 2:
//                        text = "Weeks";
//                        break;
//                    case 3:
//                        text = "Months";
//                        break;
//                    case 4:
//                    default:
//                        text = "User restrictions";
//                        break;
//                }
//                buttons[a].setTextAndIcon(text, 0);
//                layout.addView(buttons[a], LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
//            }
        });

        refreshLayout.setOnRefreshListener(() -> {
            chatRef.removeEventListener(chatEventListener);
            firstLoad = false;
            if(chatModel.isEmpty()) {
                chatQuery = chatRef.child(chat_id).child("messages").limitToFirst(limit);
            }else{
                chatQuery = chatRef.child(chat_id).child("messages").orderByKey().endAt(chatModel.get(0).getMessageId()).limitToLast(limit);
            }
            chatQuery.addListenerForSingleValueEvent(firstLoadEventListener);
        });

        btn_close_reply.setOnClickListener((v) -> {
            toggleReplyLayout();
        });

        emoji_button.setOnClickListener((v) -> {
            emojiPopup.toggle();
        });
        message_edittext.setOnClickListener(ignore ->{
           // resizeView(root, root.getHeight(), AndroidUtilities.getScreenHeight() - 459);
        });

        Handler handler = new Handler(Looper.getMainLooper());
        Runnable typingCheck = () -> {
            if (System.currentTimeMillis() > (last_text_edit + delay - 500)) {
                adapter.setTypingStatus(false);
                if(adapter.isUploading){
                    chatRef.child(chat_id).child("settings").child(UserConfig.getUid()).setValue("uploading");
                }else{
                    chatRef.child(chat_id).child("settings").child(UserConfig.getUid()).setValue("default");
                }

            }
        };

        ((TextView)message_edittext).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeCallbacks(typingCheck);
            }

            @Override
            public void afterTextChanged(Editable s) {

                    last_text_edit = System.currentTimeMillis();
                    handler.postDelayed(typingCheck, delay);
                    adapter.setTypingStatus(true);
                    if(adapter.isUploading){
                        chatRef.child(chat_id).child("settings").child(UserConfig.getUid()).setValue("typing,uploading");
                    }else {
                        chatRef.child(chat_id).child("settings").child(UserConfig.getUid()).setValue("typing");
                    }
            }
        });
    }

    private void sendMessage() {
        MessagesModel model = new MessagesModel();

        String message_id = chatRef.push().getKey();

        model.setType(MessageObject.CHAT_TYPE_MESSAGE);
        model.setMessage(message_edittext.getText().toString());
        model.setReply(isReply);
        if(isReply){
            model.setReplyUid(chatModel.get(replySelected).getMessageId());
            toggleReplyLayout();
        }
        model.setUid(UserConfig.getUid());
        model.setTime(System.currentTimeMillis());
        model.setMessageId(message_id);

        chatRef.child(chat_id).child("messages").child(message_id).setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        });
        adapter.notifyItemInserted(adapter.getItemCount() + 1);
        message_edittext.setText("");


        recyclerview.post(() -> {
            LinearSmoothScroller linearSmoothScroller = new LinearSmoothScroller(recyclerview.getContext()) {

                @Override
                protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                    return super.calculateSpeedPerPixel(displayMetrics) - 0.3f;
                }
            };

            linearSmoothScroller.setTargetPosition(adapter.getItemCount()-1);
            recyclerview.getLayoutManager().startSmoothScroll(linearSmoothScroller);
        });
        Runnable delay = ()->{
            adapter.notifyItemChanged(adapter.getItemCount() - 2);
        };
        handler.postDelayed(delay, 450);


    }

    private void initialize(View view) {

        settingsPref = PreferenceManager.getDefaultSharedPreferences(context);

        debugMode = settingsPref.getBoolean("debug", false);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        layoutManager.setStackFromEnd(true);
        recyclerview.setLayoutManager(layoutManager);

        textview_reply_name = view.findViewById(R.id.textview_reply_name);
        textview_reply_message = view.findViewById(R.id.textview_reply_message);
        btn_close_reply = view.findViewById(R.id.btn_close);

        chatModel = new ArrayList<MessagesModel>();

        adapter = new ChatAdapter(chatModel, chat_id);
        recyclerview.setAdapter(adapter);
        recyclerview.setItemAnimator(new MyItemAnimator());
        SwipeController controller = new SwipeController(context, new ISwipeControllerActions(){
            @Override
            public void onSwipePerformed(int adapterPosition) {
                textview_reply_name.setText(!chatModel.get(adapterPosition).getUid().equals(UserConfig.getUid()) ? avatarCell.getTitleText() : "Replying to yourself");
                textview_reply_message.setText(chatModel.get(adapterPosition).getMessage());
                replySelected = adapterPosition;
                if(!isReply){
                    handler.postDelayed(() -> {
                        toggleReplyLayout();
                    },120);

                }
            }
        });
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(controller);

        itemTouchHelper.attachToRecyclerView(recyclerview);

        bottom_panel = view.findViewById(R.id.bottom_panel);
        linear_send = view.findViewById(R.id.linear_send);
        attachment_button = view.findViewById(R.id.btn_attachment);

        message_edittext = view.findViewById(R.id.message_edittext);

        textview_chatname = view.findViewById(R.id.textview_chatname);

        refreshLayout = view.findViewById(R.id.swiperefreshlayout);
        reply_layout = view.findViewById(R.id.reply_layout);
        rootView = view.findViewById(R.id.root);

        emoji_button = view.findViewById(R.id.emoji_button);
        root = getParentActivity().getWindow().findViewById(android.R.id.content);

        getParentActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        OnEmojiPopupDismissListener popupDismissListener = new OnEmojiPopupDismissListener() {
            @Override
            public void onEmojiPopupDismiss() {
                initKeyboardAnimation();
                //resizeView(root,root.getHeight(),AndroidUtilities.getScreenHeight() - keyboard_height);
            }
        };
        OnSoftKeyboardCloseListener keyboardCloseListener = new OnSoftKeyboardCloseListener() {
            @Override
            public void onKeyboardClose() {
                resizeView(root,root.getHeight(),AndroidUtilities.getScreenHeight());
            }
        };


        OnEmojiPopupShownListener popupShownListener = new OnEmojiPopupShownListener() {
            @Override
            public void onEmojiPopupShown() {
                resizeView(root,root.getHeight(),AndroidUtilities.getScreenHeight()-keyboard_height);
            }
        };

         emojiPopup = EmojiPopup.Builder.fromRootView(root)
                .setOnEmojiPopupDismissListener(popupDismissListener)
                .setOnEmojiPopupShownListener(popupShownListener)
                 .setOnSoftKeyboardCloseListener(keyboardCloseListener)
                .setKeyboardAnimationStyle(android.R.style.Animation)
                 .build(message_edittext);


    }


    private void initRecycler() {
        chatQuery = chatRef.child(chat_id).child("messages").orderByChild("time").limitToLast(limit);
        chatQuery.addListenerForSingleValueEvent(firstLoadEventListener);


    }
    private void initKeyboardAnimation(){
        getParentActivity().getWindow().getDecorView().setOnApplyWindowInsetsListener((view,insets) -> {
            if(view != null) {
//                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) toolbar.getLayoutParams();
//                params.setMargins(0,insets.getStableInsetTop(),0,0);
                int screenHeight = AndroidUtilities.getScreenHeight();

                int[] location = new int[2];
                bottom_panel.getLocationOnScreen(location);

                if(debugMode)avatarCell.setSubTitle("System inset bottom: " + insets.getSystemWindowInsetBottom() + "\n stable inset bottom: " + insets.getStableInsetBottom() + "\n Screen height: " + AndroidUtilities.getScreenHeight() + "\n EditText Position Y: " + location[1] + "\n EditText height : " + bottom_panel.getHeight());

                int keyboardOffset = 410;
                int offset = insets.getSystemWindowInsetBottom() - insets.getStableInsetBottom();
                    if (offset > keyboardOffset) {
                        SharedPreferences.Editor editor = getParentActivity().getSharedPreferences("config", Context.MODE_PRIVATE).edit();
                        editor.putInt("KEYBOARD_HEIGHT", offset);
                        editor.apply();
                        keyboard_height = offset;

                        int start = root.getHeight();
                        int end = screenHeight - offset;
                        resizeView(root, start, end);
                    }else{
                        resizeView(root, root.getHeight(), AndroidUtilities.getScreenHeight());
                    }


                    Log.d("HanapBb", "Window insets dispatched \n" + "screen height : " + screenHeight + "\n inset bottom " + insets.getSystemWindowInsetBottom());
                }

            return view.onApplyWindowInsets(insets);
        });
    }
    private void resizeView(View view, int start, int end){
        ValueAnimator valueAnimator = ValueAnimator.ofInt(start, end);
        valueAnimator.addUpdateListener(animation -> {
            view.getLayoutParams().height = (int) animation.getAnimatedValue();
            view.requestLayout();
        });
        valueAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
        valueAnimator.setDuration(260);
        valueAnimator.start();
    }
    private void toggleReplyLayout(){

        if(isReply){
            AndroidUtilities.collapse(reply_layout,200,1);
        }else{
//            reply_layout.setVisibility(View.VISIBLE);
            AndroidUtilities.expand(reply_layout,200,122);

        }
        isReply = !isReply;
    }

    @Override
    protected void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        super.onTransitionAnimationEnd(isOpen, backward);
        if(adapter.getItemCount() == 1 || adapter.getItemCount() < 1 || adapter.getItemViewType(0) == ChatAdapter.VIEW_TYPE_EMPTY) {
            initRecycler();
        }
    }

    @Override
    public void onFragmentDestroy() {
     //   NotificationCenter.getInstance().removeObserver(this, NotificationCenter.);
        super.onFragmentDestroy();
    }

    private final PhotoAlbumPickerActivity.PhotoAlbumPickerActivityDelegate mPhotoAlbumPickerActivityDelegate = new PhotoAlbumPickerActivity.PhotoAlbumPickerActivityDelegate() {
        @Override
        public void didSelectPhotos(ArrayList<String> photos, ArrayList<String> captions) {
            sendPhoto(photos.get(0));
        }

        @Override
        public boolean didSelectVideo(String path) {
           return false;
        }

        @Override
        public void startPhotoSelectActivity() {
        }
    };

    private void sendPhoto(String path) {
        String message_id = chatRef.push().getKey();
        MessagesModel model = new MessagesModel();
        model.setType(MessageObject.CHAT_TYPE_PHOTO);
        model.setReply(false);
        model.setTime(System.currentTimeMillis());
        model.setMessageId(message_id);
        model.setUid(UserConfig.getUid());

        model.setTemporaryPhoto(path);
        model.setUploading(true);
        chatModel.add(model);
        adapter.notifyItemInserted(adapter.getItemCount() - 1);
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {

    }
}
