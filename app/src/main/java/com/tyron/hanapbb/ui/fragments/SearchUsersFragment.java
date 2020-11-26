package com.tyron.hanapbb.ui.fragments;

import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.paging.DatabasePagingOptions;
import com.firebase.ui.database.paging.FirebaseRecyclerPagingAdapter;
import com.firebase.ui.database.paging.LoadingState;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.tyron.hanapbb.R;
import com.tyron.hanapbb.messenger.UserConfig;
import com.tyron.hanapbb.ui.HomeActivity;
import com.tyron.hanapbb.ui.models.ConversationsModel;
import com.tyron.hanapbb.ui.models.UserModel;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchUsersFragment extends Fragment {

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference("users");
    private DatabaseReference conversationsRef = firebaseDatabase.getReference("conversations");
    private Query query = databaseReference;

    private RecyclerView recyclerView;

    private PagedList.Config config = new PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPrefetchDistance(10)
            .setPageSize(20)
            .build();
    private DatabasePagingOptions<UserModel> options = new DatabasePagingOptions.Builder<UserModel>()
            .setLifecycleOwner(this)
            .setQuery(query, config, UserModel.class)
            .build();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle){
        View view = inflater.inflate(R.layout.fragment_search_users, container, false);

        recyclerView = view.findViewById(R.id.recyclerview1);

        initialize();
        return view;
    }

    private void initialize() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    FirebaseRecyclerPagingAdapter<UserModel, ItemViewHolder> adapter = new FirebaseRecyclerPagingAdapter<UserModel, ItemViewHolder>(options){

        @NonNull
        @Override
        public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_find_users_item, parent, false);
            return new ItemViewHolder(view);
        }

        @Override
        protected void onBindViewHolder(@NonNull ItemViewHolder viewHolder, int position, @NonNull UserModel model) {
            View view = viewHolder.itemView;
            final TextView textview_name = view.findViewById(R.id.textview_chatname);
            final TextView textview_username = view.findViewById(R.id.textview_lastmessage);
            final CircleImageView profile = view.findViewById(R.id.circleImageView);
            final ViewGroup rootView = view.findViewById(R.id.root);

            textview_username.setText("@" + model.getUsername());
            Glide.with(getContext()).load(Uri.parse(model.getPhotoUrl())).centerCrop().into(profile);

            if (!model.getUid().equals(UserConfig.getUid())) {
                textview_name.setText(model.getName());
                rootView.setOnClickListener((view1) -> {
                    conversationsRef.child(UserConfig.getUid()).child(model.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {

                        int compare = model.getUid().compareTo(UserConfig.getUid());
                        String chat_id = (compare == 0 ? UserConfig.getUid() + model.getUid() : model.getUid() + UserConfig.getUid());

                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                ((HomeActivity) getActivity()).replaceFragment(ChatFragment.newInstance(chat_id));
                            } else {
                                ConversationsModel newModel = new ConversationsModel();


                                newModel.setChat_id(chat_id);

                                conversationsRef.child(UserConfig.getUid()).child(model.getUid()).setValue(newModel);
                                conversationsRef.child(model.getUid()).child(UserConfig.getUid()).setValue(newModel);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                });
            }else{
                textview_name.setTypeface(textview_name.getTypeface(), Typeface.BOLD);

            }
        }

        @Override
        protected void onLoadingStateChanged(@NonNull LoadingState state) {

        }
    };

    private class ItemViewHolder extends RecyclerView.ViewHolder {
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
