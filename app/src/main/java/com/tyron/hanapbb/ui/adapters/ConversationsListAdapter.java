package com.tyron.hanapbb.ui.adapters;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tyron.hanapbb.R;
import com.tyron.hanapbb.ui.components.DiffUtilCallback;
import com.tyron.hanapbb.ui.fragments.ConversationsList;
import com.tyron.hanapbb.ui.models.ConversationsModel;
import com.tyron.hanapbb.ui.models.UserModel;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConversationsListAdapter extends RecyclerView.Adapter<ConversationsListAdapter.ViewHolder> {

    private List<ConversationsModel> model;
    private FirebaseDatabase firebase = FirebaseDatabase.getInstance();
    private DatabaseReference ref = firebase.getReference("users");

    public ConversationsListAdapter(List<ConversationsModel> list){
        model = list;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.conversation_list_item_view,parent,false);
        return new ConversationsListAdapter.ViewHolder(v,viewType);
    }

    public void updateList(List<ConversationsModel> newList){
        DiffUtilCallback callback = new DiffUtilCallback(this.model, newList);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);

        result.dispatchUpdatesTo(this);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        View view = holder.itemView;
        final TextView textview_name = view.findViewById(R.id.textview_name);
        final TextView textview_message = view.findViewById(R.id.textview_lastmessage);
        final CircleImageView photo = view.findViewById(R.id.circleImageView);

        if(model.get(position).getLastMessage() != null){
            textview_message.setText(model.get(position).getLastMessage());

            ref.child(model.get(position).getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
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

        }else {
            textview_name.setText("Loading");
        }
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

    public List<ConversationsModel> getList(){
        return this.model;
    }
}
