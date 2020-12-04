package com.tyron.hanapbb.ui.fragments;

import androidx.appcompat.widget.Toolbar;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.recyclerview.widget.LinearLayoutManager;
import android.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.tyron.hanapbb.R;
import com.tyron.hanapbb.messenger.NotificationCenter;
import com.tyron.hanapbb.messenger.UserConfig;
import com.tyron.hanapbb.ui.ProfileActivity;
import com.tyron.hanapbb.ui.SettingsActivity;
import com.tyron.hanapbb.ui.actionbar.BaseFragment;
import com.tyron.hanapbb.ui.adapters.ConversationsListAdapter;
import com.tyron.hanapbb.ui.models.ConversationsIdModel;
import com.tyron.hanapbb.ui.models.ConversationsModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConversationsList extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {

    private Context context;

    private final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private final DatabaseReference conversationsRef = firebaseDatabase.getReference("conversations/");
    private final DatabaseReference profileRef = firebaseDatabase.getReference("users/");
    private final Query query = conversationsRef.child(UserConfig.getUid()).limitToLast(30);
    private final DatabaseReference chatRef = firebaseDatabase.getReference("chats");

    private CircleImageView avatar;

    private final FirebaseRecyclerOptions<ConversationsModel> options = new FirebaseRecyclerOptions.Builder<ConversationsModel>()
            .setQuery(query,ConversationsModel.class).build();

    private RecyclerView list;

    private final Set<ConversationsModel> conversations_list = new HashSet<ConversationsModel>();
    private final List<ConversationsIdModel> adapterList = new ArrayList<>();

    private FloatingActionButton floatingActionButton;

    private ValueEventListener[] listeners;
    private final List<String> keys = new ArrayList<>();
    private CircleImageView profile_imageview;
    private ImageView settings_btn;

    public static ConversationsList newInstance() {
        return new ConversationsList();
    }



    @Override
    public boolean onFragmentCreate() {
        NotificationCenter.getInstance().addObserver(this,NotificationCenter.didClickConversation);

        return super.onFragmentCreate();
    }

    @Override
    public void onFragmentDestroy() {
        NotificationCenter.getInstance().removeObserver(this,NotificationCenter.didClickConversation);
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

        ConversationsListAdapter adapter = new ConversationsListAdapter(adapterList);
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

    private void initialize() {

        conversationsRef.child(UserConfig.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listeners = new ValueEventListener[(int) snapshot.getChildrenCount()];
                for(DataSnapshot ds : snapshot.getChildren()) {
                   ConversationsIdModel idModel = ds.getValue(ConversationsIdModel.class);
                   adapterList.add(idModel);
                   list.getAdapter().notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        floatingActionButton.setOnClickListener((view) -> {
            presentFragment(new SearchUsersFragment(),false,false);
        });
        String url = UserConfig.config.getPhotoUrl();
        Glide.with(context).load(url).into(profile_imageview);
        settings_btn = fragmentView.findViewById(R.id.imageView3);

        settings_btn.setOnClickListener(ignore -> {
            ProfileActivity fragment = new ProfileActivity();
            fragment.setProfileDetails(UserConfig.config);
            presentFragment(fragment);
        });
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        if(id == NotificationCenter.didClickConversation){
            presentFragment(new ChatFragment((String) args[0]));
        }
    }

}