package com.tyron.hanapbb.ui.fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.paging.DatabasePagingOptions;
import com.firebase.ui.database.paging.FirebaseRecyclerPagingAdapter;
import com.firebase.ui.database.paging.LoadingState;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.tyron.hanapbb.R;
import com.tyron.hanapbb.messenger.UserConfig;
import com.tyron.hanapbb.ui.actionbar.BackDrawable;
import com.tyron.hanapbb.ui.actionbar.BaseFragment;
import com.tyron.hanapbb.ui.models.ConversationsIdModel;
import com.tyron.hanapbb.ui.models.UserModel;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchUsersFragment extends BaseFragment {

    private Context context;
    public SearchUsersFragment(){

    }
    private final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private final DatabaseReference databaseReference = firebaseDatabase.getReference("users");
    private final DatabaseReference conversationsRef = firebaseDatabase.getReference("conversations");
    private final Query query = databaseReference;

    private RecyclerView recyclerView;

    private final PagedList.Config config = new PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPrefetchDistance(10)
            .setPageSize(40)
            .build();
    private DatabasePagingOptions<UserModel> options;

    private FirebaseRecyclerPagingAdapter<UserModel, ItemViewHolder> adapter;

    @Override
    public View createView(Context context) {
        this.context = context;

        fragmentView = new FrameLayout(context);
        options = new DatabasePagingOptions.Builder<UserModel>()
                .setLifecycleOwner((LifecycleOwner) context)
                .setQuery(query, config, UserModel.class)
                .build();

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.fragment_search_users, (ViewGroup) fragmentView, false);

        recyclerView = view.findViewById(R.id.recyclerview1);

        initialize();

        ((ViewGroup) fragmentView).addView(view);

        BackDrawable back = new BackDrawable(false);
        back.setColorFilter(Color.parseColor("#000000"), PorterDuff.Mode.MULTIPLY);

        actionBar.setBackButtonDrawable(back);
        actionBar.setBackgroundColor(Color.parseColor("#ffffff"));
        actionBar.setCastShadows(false);
        actionBar.setTitle("Users");
        actionBar.setItemsBackgroundColor(Color.parseColor("#000000"));
        return fragmentView;
    }

    private void initialize() {
        adapter = new FirebaseRecyclerPagingAdapter<UserModel, ItemViewHolder>(options){

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
                Glide.with(context).load(Uri.parse(model.getPhotoUrl())).centerCrop().into(profile);

                if (!model.getUid().equals(UserConfig.getUid())) {
                    textview_name.setText(model.getName());
                    rootView.setOnClickListener((view1) -> {
                        conversationsRef.child(UserConfig.getUid()).child(model.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {

                            final int compare = model.getUid().compareTo(UserConfig.getUid());
                            final String chat_id = (compare == 0 ? UserConfig.getUid() + model.getUid() : model.getUid() + UserConfig.getUid());

                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    presentFragment(new ChatFragment(chat_id), true,false);
                                } else {
                                    ConversationsIdModel newModel = new ConversationsIdModel();


                                    newModel.setChat_id(chat_id);

                                    conversationsRef.child(UserConfig.getUid()).child(model.getUid()).setValue(newModel);
                                    conversationsRef.child(model.getUid()).child(UserConfig.getUid()).setValue(newModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            presentFragment(new ChatFragment(newModel.getChat_id()),true,false);
                                        }
                                    });
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
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }


    private class ItemViewHolder extends RecyclerView.ViewHolder {
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
