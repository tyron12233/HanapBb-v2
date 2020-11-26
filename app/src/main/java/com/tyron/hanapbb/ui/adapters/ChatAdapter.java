package com.tyron.hanapbb.ui.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tyron.hanapbb.R;
import com.tyron.hanapbb.emoji.Emoji;
import com.tyron.hanapbb.emoji.EmojiTextView;
import com.tyron.hanapbb.messenger.AndroidUtilities;
import com.tyron.hanapbb.messenger.FirebaseUtilities;
import com.tyron.hanapbb.messenger.UserConfig;
import com.tyron.hanapbb.ui.components.DiffUtilCallback;
import com.tyron.hanapbb.ui.fragments.ChatFragment;
import com.tyron.hanapbb.ui.models.MessagesModel;
import com.tyron.hanapbb.messenger.MessageObject;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    public static final int VIEW_TYPE_EMPTY = 40;

    public List<MessagesModel> chatModel;
    private MessagesModel model;

    public ChatAdapter(List<MessagesModel> chatModel){
        this.chatModel = chatModel;
    }
    public void updateList(List<MessagesModel> newList){
        DiffUtilCallback callback = new DiffUtilCallback(this.chatModel, newList);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback,true);
        chatModel.clear();
        chatModel.addAll(newList);
        result.dispatchUpdatesTo(this);

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
            holder.message_linear.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    FirebaseUtilities.removeFromPath("chats/" + ChatFragment.getChatId().concat("/").concat("messages/") + currentMessage.getMessageId());
                    return false;
                }
            });
            int viewType = holder.getItemViewType();
            float tl = 30, tr = 30, bl = 30, br = 30;

            GradientDrawable drawable = new GradientDrawable();

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
            String message = currentMessage.getMessage();//(String) Emoji.replaceEmoji(currentMessage.getMessage(),holder.message_textview.getPaint().getFontMetricsInt(),40,false);
            switch (viewType) {

                case MessageObject.MESSAGE_TYPE_REPLY_AUTHOR:
                    setReplyMessages(currentMessage.getReplyUid(),holder.message_reply_textview);
                    holder.name_reply_textview.setText((ChatFragment.getName()));
                case MessageObject.MESSAGE_TYPE_NORMAL_AUTHOR:
                    holder.message_textview.setText(message);
                    drawable.setColor(Color.parseColor("#fe6262"));
                    break;
                case MessageObject.MESSAGE_TYPE_REPLY_RECEIVER:
                    setReplyMessages(currentMessage.getReplyUid(),holder.message_reply_textview);
                    holder.name_reply_textview.setText("You");
                case MessageObject.MESSAGE_TYPE_NORMAL_RECEIVER:
                    holder.message_textview.setText(message);
                    drawable.setColor(Color.parseColor("#eeeeee"));
                    break;
            }
            drawable.setCornerRadii(new float[]{tl, tl, tr, tr, br, br, bl, bl});
            holder.message_linear.setBackground(drawable);
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
            return MessageObject.MESSAGE_TYPE_NORMAL_AUTHOR;
        }else{
            //message type is receiver
            if(model.getReply()) return MessageObject.MESSAGE_TYPE_REPLY_RECEIVER;
            return MessageObject.MESSAGE_TYPE_NORMAL_RECEIVER;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private EmojiTextView message_textview;
        private LinearLayout message_linear;
        private EmojiTextView message_reply_textview;
        private TextView name_reply_textview;

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
            }
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
