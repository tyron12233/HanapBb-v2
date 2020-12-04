package com.tyron.hanapbb.ui.adapters;

import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import android.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tyron.hanapbb.R;
import com.tyron.hanapbb.messenger.MessageObject;
import com.tyron.hanapbb.messenger.NotificationCenter;
import com.tyron.hanapbb.messenger.UserConfig;
import com.tyron.hanapbb.ui.models.ConversationsIdModel;
import com.tyron.hanapbb.ui.models.UserModel;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConversationsListAdapter extends RecyclerView.Adapter<ConversationsListAdapter.ViewHolder> {

    private final List<ConversationsIdModel> model;
    private final FirebaseDatabase firebase = FirebaseDatabase.getInstance();
    private final DatabaseReference ref = firebase.getReference("users");
    private final DatabaseReference chatRef = firebase.getReference("chats");


    public ConversationsListAdapter(List<ConversationsIdModel> list){
        model = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.conversation_list_item_view,parent,false);
        return new ConversationsListAdapter.ViewHolder(v,viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        View view = holder.itemView;
        final TextView textview_name = view.findViewById(R.id.textview_name);
        final TextView textview_message = view.findViewById(R.id.textview_lastmessage);
        final CircleImageView photo = view.findViewById(R.id.circleImageView);
        final ViewGroup root = view.findViewById(R.id.root);
        final TextView time = view.findViewById(R.id.textView7);

        chatRef.child(model.get(position).getChat_id()).child("messages").limitToLast(1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot ds : snapshot.getChildren()) {
                    time.setText(DateUtils.getRelativeTimeSpanString(time.getContext(), ds.child("time").getValue(Long.class)));
                    if(ds.child("type").getValue(Integer.class) == MessageObject.CHAT_TYPE_PHOTO){
                        textview_message.setText("Sent a photo.");
                    }else{
                        textview_message.setText(ds.child("message").getValue(String.class));
                    }

                    ref.child(model.get(position).getChat_id().replace(UserConfig.getUid(), "")).

                            addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    UserModel model = snapshot.getValue(UserModel.class);
                                    textview_name.setText(model.getName());
                                    Glide.with(view).load(model.getPhotoUrl()).placeholder(R.drawable.ic_photo_camera_black_36dp).into(photo);


                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        root.setOnClickListener(ignore -> {
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.didClickConversation, model.get(position).getChat_id());
        });
    }

    @Override
    public int getItemCount() {
        if (model.size() >= 1) return model.size();
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public ViewHolder(View v, int viewType) {
            super(v);
        }
    }

    public List<ConversationsIdModel> getList(){
        return this.model;
    }
}
