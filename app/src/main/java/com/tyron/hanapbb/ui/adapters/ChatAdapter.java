package com.tyron.hanapbb.ui.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tyron.hanapbb.R;
import com.tyron.hanapbb.emoji.EmojiTextView;
import com.tyron.hanapbb.emoji.EmojiUtils;
import com.tyron.hanapbb.messenger.AndroidUtilities;
import com.tyron.hanapbb.messenger.FirebaseUtilities;
import com.tyron.hanapbb.messenger.UserConfig;
import com.tyron.hanapbb.ui.components.LayoutHelper;
import com.tyron.hanapbb.ui.fragments.ChatFragment;
import com.tyron.hanapbb.ui.models.MessagesModel;
import com.tyron.hanapbb.messenger.MessageObject;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    public static final int VIEW_TYPE_EMPTY = 40;

    public List<MessagesModel> chatModel;
    private MessagesModel model;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference chatRef = firebaseDatabase.getReference("chats");
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReference("chats");

    private String chat_id;

    public boolean isUploading;
    public boolean isTyping;

    public ChatAdapter(List<MessagesModel> chatModel, String chat_id){
        this.chatModel = chatModel;
        this.chat_id = chat_id;

        chatRef = firebaseDatabase.getReference("chats/" + chat_id);
        storageRef = storage.getReference("chats" + chat_id + "media");
    }

    public void setTypingStatus(boolean status){
        isTyping = status;
    }
    public void updateList(List<MessagesModel> newList){
        //DiffUtilCallback callback = new DiffUtilCallback(this.chatModel, newList);
        //DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback,true);
        chatModel.clear();
        chatModel.addAll(newList);
        //result.dispatchUpdatesTo(this);

    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ChatAdapter.ViewHolder viewHolder = null;
        View view = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch(viewType){
            case MessageObject.MESSAGE_TYPE_NORMAL_AUTHOR:
                view = inflater.inflate(R.layout.chat_type_normal_author, parent, false);
                break;
            case MessageObject.MESSAGE_TYPE_REPLY_AUTHOR:
                view = inflater.inflate(R.layout.chat_type_reply_author,parent,false);
                break;
            case MessageObject.MESSAGE_TYPE_NORMAL_RECEIVER:
                view = inflater.inflate(R.layout.chat_type_normal_receiver,parent,false);
                break;
            case MessageObject.MESSAGE_TYPE_REPLY_RECEIVER:
                view = inflater.inflate(R.layout.chat_type_reply_receiver,parent,false);
                break;
            case MessageObject.MESSAGE_TYPE_PHOTO_AUTHOR:
                view = inflater.inflate(R.layout.chat_type_photo_author,parent,false);
                break;
            case MessageObject.MESSAGE_TYPE_PHOTO_RECEIVER:
                view = inflater.inflate(R.layout.chat_type_photo_receiver,parent,false);
                break;
            case VIEW_TYPE_EMPTY:
                view = inflater.inflate(R.layout.chat_empty_state, parent,false);
                break;
            default:
                view = inflater.inflate(R.layout.conversation_list_item_view,parent,false);

        }
        viewHolder = new ChatAdapter.ViewHolder(view, viewType);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if(!chatModel.isEmpty()) {
            MessagesModel currentMessage = chatModel.get(position);

            int viewType = holder.getItemViewType();
            float tl = 30, tr = 30, bl = 30, br = 30;

            GradientDrawable drawable = new GradientDrawable();

            String message = currentMessage.getMessage();

            switch (viewType) {

                case MessageObject.MESSAGE_TYPE_REPLY_AUTHOR:
                    setReplyMessages(currentMessage.getReplyUid(),holder.message_reply_textview);
                    holder.name_reply_textview.setText((ChatFragment.getName()));
                case MessageObject.MESSAGE_TYPE_NORMAL_AUTHOR:
                    if(EmojiUtils.isOnlyEmojis(message)) holder.message_textview.setEmojiSize(AndroidUtilities.dp(30));
                    holder.message_textview.setText(message);
                    drawable.setColor(Color.parseColor("#fe6262"));
                    break;
                case MessageObject.MESSAGE_TYPE_REPLY_RECEIVER:
                    setReplyMessages(currentMessage.getReplyUid(),holder.message_reply_textview);
                    holder.name_reply_textview.setText("You");
                case MessageObject.MESSAGE_TYPE_NORMAL_RECEIVER:
                    if(EmojiUtils.isOnlyEmojis(message)) holder.message_textview.setEmojiSize(AndroidUtilities.dp(30));
                    holder.message_textview.setText(message);
                    drawable.setColor(Color.parseColor("#eeeeee"));
                    break;
                case MessageObject.MESSAGE_TYPE_PHOTO_AUTHOR:
                    drawable.setColor(Color.parseColor("#fe6262"));

                    if(currentMessage.isUploading()) {
                        Glide.with(holder.message_photo_imageview).load(currentMessage.getTemporaryPhoto()).transform(new RoundedCorners(8)).into(holder.message_photo_imageview);
                        tl = 8;
                        tr = 8;
                        br = 8;
                        bl = 8;

                        LinearLayout background_scrim = new LinearLayout(holder.photo_holder.getContext());

                        Context context = holder.photo_holder.getContext();
                        background_scrim.setBackgroundColor(Color.parseColor("#fe6262"));

                        ImageView imageView = new ImageView(context);

                        imageView.setImageDrawable(context.getDrawable(R.drawable.ic_close));

                        FrameLayout layout = new FrameLayout(context);
                        layout.setBackgroundColor(Color.parseColor("#fe6262"));

                        GradientDrawable drawab = new GradientDrawable();
                        drawab.setCornerRadii(new float[] {8,8,8,8,8,8,8,8 });
                        drawab.setColor(Color.parseColor("#fe6262"));
                        holder.photo_holder.setBackground(drawab);

                        ProgressBar progressBar = new ProgressBar(context, null, android.R.attr.progressBarStyle);
                        progressBar.setIndeterminate(false);
                        progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#fe6262"), PorterDuff.Mode.MULTIPLY);

                        holder.photo_holder.addView(progressBar, LayoutHelper.createFrame(AndroidUtilities.dp(28), AndroidUtilities.dp(28), Gravity.CENTER));

                        holder.photo_holder.addView(layout, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
                        holder.photo_holder.addView(imageView, LayoutHelper.createFrame(AndroidUtilities.dp(18), AndroidUtilities.dp(18), Gravity.CENTER));

                        //upload task
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        Bitmap bitmap = BitmapFactory.decodeFile(currentMessage.getTemporaryPhoto());
                        bitmap.compress(Bitmap.CompressFormat.JPEG,20,baos);
                        byte[] data = baos.toByteArray();

                        UploadTask uploadTask = storageRef.putBytes(data);
                        isUploading = true;
                        if(isTyping){
                            chatRef.child("settings").child(UserConfig.getUid()).setValue("typing,uploading");
                        }else{
                            chatRef.child("settings").child(UserConfig.getUid()).setValue("uploading");
                        }
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //TODO: handle unsuccessful upload
                            }
                        });

                        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                StorageMetadata metadata = taskSnapshot.getMetadata();
                                storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        if(isTyping){
                                            chatRef.child("settings").child(UserConfig.getUid()).setValue("typing");
                                        }else{
                                            chatRef.child("settings").child(UserConfig.getUid()).setValue("default");
                                        }
                                        chatModel.get(position).setUploading(false);
                                        chatModel.get(position).setPhotoUrl(uri.toString());
                                        chatRef.child("messages").child(currentMessage.getMessageId()).setValue(chatModel.get(position));
                                        notifyItemChanged(position);
                                        isUploading = false;
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });
                            }
                        });
                    }else{
                        Glide.with(holder.photo_holder.getContext()).load(currentMessage.getPhotoUrl()).into(holder.message_photo_imageview);
                    }
                    break;
                case MessageObject.MESSAGE_TYPE_PHOTO_RECEIVER:
                    Glide.with(holder.photo_holder.getContext()).load(currentMessage.getPhotoUrl()).into(holder.message_photo_imageview);
            }
            switch(viewType) {

                case MessageObject.MESSAGE_TYPE_NORMAL_AUTHOR:
                case MessageObject.MESSAGE_TYPE_NORMAL_RECEIVER:
                case MessageObject.MESSAGE_TYPE_REPLY_AUTHOR:
                case MessageObject.MESSAGE_TYPE_REPLY_RECEIVER:
                MessagesModel prevMessage = null;
                MessagesModel nextMessage = null;
                if (position - 1 > 0 || position - 1 == 0) {
                    prevMessage = chatModel.get(position - 1);
                }
                if (position + 1 <= getItemCount() - 1) {
                    nextMessage = chatModel.get(position + 1);
                }

                if (prevMessage != null && currentMessage.getUid().equals(prevMessage.getUid())) {
                    if (UserConfig.getUid().equals(currentMessage.getUid())) {
                        tr = 8;
                    } else {
                        tl = 8;
                    }

                    Context context = holder.message_linear.getContext();

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(holder.message_linear.getLayoutParams());
                    params.setMargins(AndroidUtilities.dp(context, 8),
                            AndroidUtilities.dp(context, 1),
                            AndroidUtilities.dp(context, 8),
                            AndroidUtilities.dp(context, 1));

                    holder.message_linear.setLayoutParams(params);
                }
                if (nextMessage != null && currentMessage.getUid().equals(nextMessage.getUid())) {
                    if (UserConfig.getUid().equals(currentMessage.getUid())) {
                        br = 8;
                    } else {
                        bl = 8;
                    }
                    Context context = holder.message_linear.getContext();
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(holder.message_linear.getLayoutParams());
                    params.setMargins(AndroidUtilities.dp(context, 8),
                            AndroidUtilities.dp(context, 1),
                            AndroidUtilities.dp(context, 8),
                            AndroidUtilities.dp(context, 1));

                    holder.message_linear.setLayoutParams(params);

                }
                    drawable.setCornerRadii(new float[]{tl, tl, tr, tr, br, br, bl, bl});
                    holder.message_linear.setBackground(drawable);
                break;
            }
        }
    }
    private void setReplyMessages(final String uid, TextView message){
        FirebaseDatabase firebase = FirebaseDatabase.getInstance();
        DatabaseReference chatRef = firebase.getReference("chats");
        chatRef.child(ChatFragment.getChatId()).child("messages").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    message.setText(snapshot.child("message").getValue(String.class));
                    message.setTypeface(message.getTypeface(), Typeface.NORMAL);
                }else{
                    message.setText("Deleted message");
                    message.setTypeface(message.getTypeface(), Typeface.ITALIC);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        if(chatModel.size() > 0) return chatModel.size();
        return 1;
    }

    @Override
    public int getItemViewType(int position) {

        if(chatModel.isEmpty()) return VIEW_TYPE_EMPTY;

        MessagesModel model = chatModel.get(position);
        int type = model.getType();

        if(type == MessageObject.CHAT_TYPE_SYSTEM) return MessageObject.CHAT_TYPE_SYSTEM;

        if(UserConfig.getUid().equals(model.getUid())){
            //message type is author
            if(model.getReply()) return MessageObject.MESSAGE_TYPE_REPLY_AUTHOR;
            if(model.getType() == MessageObject.CHAT_TYPE_PHOTO) return MessageObject.MESSAGE_TYPE_PHOTO_AUTHOR;
            return MessageObject.MESSAGE_TYPE_NORMAL_AUTHOR;
        }else{
            //message type is receiver
            if(model.getReply()) return MessageObject.MESSAGE_TYPE_REPLY_RECEIVER;
            if(model.getType() ==  MessageObject.CHAT_TYPE_PHOTO) return MessageObject.MESSAGE_TYPE_PHOTO_RECEIVER;
            return MessageObject.MESSAGE_TYPE_NORMAL_RECEIVER;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private EmojiTextView message_textview;
        private LinearLayout message_linear;
        private EmojiTextView message_reply_textview;
        private TextView name_reply_textview;
        private ImageView message_photo_imageview;
        private FrameLayout photo_holder;

        public ViewHolder(@NonNull View itemView, int type) {
            super(itemView);
            initUniversalViews();
            switch(type){
                case MessageObject.MESSAGE_TYPE_NORMAL_AUTHOR:
                    initAuthorViews(itemView);
                    break;
                case MessageObject.MESSAGE_TYPE_NORMAL_RECEIVER:
                    initReceiverViews();
                    break;
                case MessageObject.MESSAGE_TYPE_REPLY_AUTHOR:
                    initAuthorViews(itemView);
                case MessageObject.MESSAGE_TYPE_REPLY_RECEIVER:
                    initReceiverViews();
                    initReplyViews();
                    break;
                case MessageObject.MESSAGE_TYPE_PHOTO_AUTHOR:
                    initAuthorPhotoViews();
                    photo_holder = itemView.findViewById(R.id.photo_holder);
                    break;
                case MessageObject.MESSAGE_TYPE_PHOTO_RECEIVER:
                    photo_holder = itemView.findViewById(R.id.photo_holder);
                    message_photo_imageview = itemView.findViewById(R.id.imageView11);
                    break;
            }
        }

        private void initAuthorPhotoViews() {
            message_photo_imageview = itemView.findViewById(R.id.imageView11);
        }

        private void initReceiverViews() {
            message_textview = itemView.findViewById(R.id.textview_message);
        }

        private void initUniversalViews() {
            message_linear = itemView.findViewById(R.id.linear_message);
        }

        private void initAuthorViews(View view) {
            message_textview = view.findViewById(R.id.textview_message);
        }
        private void initReplyViews(){
            message_reply_textview = itemView.findViewById(R.id.textview_reply_message);
            name_reply_textview = itemView.findViewById(R.id.textview_reply_name);
        }
    }
}
