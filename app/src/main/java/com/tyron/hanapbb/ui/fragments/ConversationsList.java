package com.tyron.hanapbb.ui.fragments;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.tyron.hanapbb.R;
import com.tyron.hanapbb.emoji.EmojiTextView;
import com.tyron.hanapbb.messenger.AndroidUtilities;
import com.tyron.hanapbb.messenger.UserConfig;
import com.tyron.hanapbb.ui.HomeActivity;
import com.tyron.hanapbb.ui.SettingsActivity;
import com.tyron.hanapbb.ui.actionbar.ActionBar;
import com.tyron.hanapbb.ui.actionbar.ActionBarMenu;
import com.tyron.hanapbb.ui.actionbar.BaseFragment;
import com.tyron.hanapbb.ui.components.LayoutHelper;
import com.tyron.hanapbb.ui.models.ConversationsModel;

import java.util.ArrayList;
import java.util.List;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.tyron.hanapbb.ui.models.MessagesModel;
import com.tyron.hanapbb.ui.models.UserModel;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConversationsList extends BaseFragment {

    private Context context;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference conversationsRef = firebaseDatabase.getReference("conversations/");
    private DatabaseReference profileRef = firebaseDatabase.getReference("users/");
    private Query query = conversationsRef.child(UserConfig.getUid()).limitToLast(30);
    private DatabaseReference chatRef = firebaseDatabase.getReference("chats");

    private CircleImageView avatar;

    private FirebaseRecyclerOptions<ConversationsModel> options = new FirebaseRecyclerOptions.Builder<ConversationsModel>()
            .setQuery(query,ConversationsModel.class).build();

    private RecyclerView list;

    private List<ConversationsModel> conversations_list = new ArrayList<ConversationsModel>();

    private FloatingActionButton floatingActionButton;

    private CircleImageView profile_imageview;

    public static ConversationsList newInstance() {
        return new ConversationsList();
    }



    @Override
    public boolean onFragmentCreate() {
        adapter.startListening();

        return super.onFragmentCreate();
    }

    @Override
    public void onFragmentDestroy() {
        adapter.stopListening();
        super.onFragmentDestroy();
    }

//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        switch(item.getItemId()){
//            case R.id.action_settings:
//                startActivity(new Intent(getContext(), SettingsActivity.class));
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//
//    }


    @Override
    public View createView(Context context) {
        this.context = context;

        LayoutInflater inflater = LayoutInflater.from(context);
        fragmentView = new FrameLayout(context);


        View view = inflater.inflate(R.layout.conversations_list_fragment, (ViewGroup) fragmentView, false);

        ((ViewGroup) fragmentView).addView(view);
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        floatingActionButton = view.findViewById(R.id.floatingActionButton);
        profile_imageview = view.findViewById(R.id.toolbar_icon);

        actionBar.setAddToContainer(false);
        list = view.findViewById(R.id.list);

        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(getParentActivity());
        list.setLayoutManager(linearLayoutManager);
        list.setAdapter(adapter);

        initialize();
        getParentActivity().getWindow().getDecorView().setOnApplyWindowInsetsListener((view1, insets) -> {
            int marginBottom;
            marginBottom = insets.getSystemWindowInsetBottom() - insets.getStableInsetBottom();
            int marginTop = insets.getSystemWindowInsetTop() - insets.getStableInsetTop();

            if(marginTop == 0 | marginBottom == 0){
                marginBottom = insets.getStableInsetBottom();
                marginTop = insets.getSystemWindowInsetTop();
            }
            if (view1 != null) {
                ConstraintLayout.LayoutParams params1 = (ConstraintLayout.LayoutParams) floatingActionButton.getLayoutParams();
                params1.bottomMargin += marginBottom;
                floatingActionButton.setLayoutParams(params1);

                ((ConstraintLayout.LayoutParams)toolbar.getLayoutParams()).topMargin = marginTop;
            }

            return view1.onApplyWindowInsets(insets);
        });

        return fragmentView;
    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//    }

    private void initialize() {
//        floatingActionButton.setOnClickListener((view) -> {
//            ((HomeActivity)getActivity()).replaceFragment((Fragment) new SearchUsersFragment());
//        });
        String url = UserConfig.config.getPhotoUrl();
        Glide.with(context).load(url).into(profile_imageview);
    }

//    @Override
//    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
//        inflater.inflate(R.menu.conversations_menu, menu);
//    }


    FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<ConversationsModel, ViewHolder>(options) {
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.conversation_list_item_view, parent, false);

            return new ViewHolder(view);
        }

        @Override
        protected void onBindViewHolder(ViewHolder holder, final int position, ConversationsModel model) {
            View view = holder.itemView;
            final TextView textview_name = view.findViewById(R.id.textview_name);
            final EmojiTextView textview_message = view.findViewById(R.id.textview_lastmessage);
            final ViewGroup rootView = view.findViewById(R.id.root);
            final CircleImageView chat_pic = view.findViewById(R.id.circleImageView);

            textview_name.setTypeface(textview_name.getTypeface(), Typeface.BOLD);

            profileRef.child(model.getChat_id().replace(UserConfig.getUid(),"")).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    UserModel model = snapshot.getValue(UserModel.class);
                    textview_name.setText(model.getName());
                    Glide.with(context).load(model.getPhotoUrl()).centerCrop().into(chat_pic);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            chatRef.child(model.getChat_id()).child("messages").limitToLast(1).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot ds : snapshot.getChildren()){
                        MessagesModel model = ds.getValue(MessagesModel.class);

                        textview_message.setText(model.getMessage());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            rootView.setOnClickListener((v) -> {
                presentFragment(new ChatFragment(model.getChat_id()),false,false);
            });
        }
    };
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public ViewHolder(View v, int viewType) {
            super(v);
        }
    }
}