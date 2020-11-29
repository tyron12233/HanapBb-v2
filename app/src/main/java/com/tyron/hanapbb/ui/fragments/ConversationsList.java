package com.tyron.hanapbb.ui.fragments;

import androidx.annotation.RequiresApi;
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

import android.provider.ContactsContract;
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
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.tyron.hanapbb.R;
import com.tyron.hanapbb.emoji.EmojiTextView;
import com.tyron.hanapbb.messenger.AndroidUtilities;
import com.tyron.hanapbb.messenger.MessageObject;
import com.tyron.hanapbb.messenger.NotificationCenter;
import com.tyron.hanapbb.messenger.UserConfig;
import com.tyron.hanapbb.ui.HomeActivity;
import com.tyron.hanapbb.ui.SettingsActivity;
import com.tyron.hanapbb.ui.actionbar.ActionBar;
import com.tyron.hanapbb.ui.actionbar.ActionBarMenu;
import com.tyron.hanapbb.ui.actionbar.BaseFragment;
import com.tyron.hanapbb.ui.adapters.ConversationsListAdapter;
import com.tyron.hanapbb.ui.components.LayoutHelper;
import com.tyron.hanapbb.ui.models.ConversationsModel;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.tyron.hanapbb.ui.models.MessagesModel;
import com.tyron.hanapbb.ui.models.UserModel;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConversationsList extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {

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
    private List<ConversationsModel> adapterList = new ArrayList<>();

    private FloatingActionButton floatingActionButton;

    private ValueEventListener[] listeners;
    private List<String> keys = new ArrayList<>();
    private CircleImageView profile_imageview;

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
                    String chat_id = ds.child("chat_id").getValue(String.class);
                    keys.add(chat_id);
                }
                retrieveConversations();
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

    }

    private void retrieveConversations() {

        for(int i = 0; i < listeners.length; i++){

            final int finalI = i;
            listeners[i] = new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot ds : snapshot.getChildren()){
                        MessagesModel model = ds.getValue(MessagesModel.class);

                        ConversationsModel conv = new ConversationsModel();
                        conv.setLastMessage(model.getMessage());

                        if(model.getType() == MessageObject.CHAT_TYPE_PHOTO){
                            conv.setLastMessage("Sent a photo. ");
                        }
                        conv.setLastTime(model.getTime());

                        int j = 0;
                        for(Iterator<ConversationsModel> iterator = adapterList.iterator(); iterator.hasNext();){
                            ConversationsModel it = iterator.next();
                            if(it.getLastUid().equals(conv.getLastUid())){
                                conv.setLastUid(keys.get(j));
                                conv.setUserId(keys.get(j).replace(UserConfig.getUid(), ""));
                                break;
                            }
                            j++;
                        }
                        if(conv.getLastUid() == null || conv.getUserId() == null){

                                conv.setLastUid(keys.get(finalI));
                                conv.setUserId(keys.get(finalI).replace(UserConfig.getUid(), ""));
                                j= finalI;

                        }

                        if(adapterList.isEmpty()){
                            adapterList.add(conv);
                        }else{
                            if(j < adapterList.size() && adapterList.get(j) == null || adapterList.size() < keys.size()){
                                adapterList.add(conv);
                            }else{
                            int k = 0;
                            for(Iterator<ConversationsModel> iterator = adapterList.iterator(); iterator.hasNext();) {
                                ConversationsModel it = iterator.next();
                                if (it.getLastUid().equals(conv.getLastUid())) {
                                    adapterList.set(k, conv);
                                }
                                k++;
                            }
                            }
                        }

                        if(adapterList.size() < keys.size() || adapterList.get(finalI) == null){
                            list.getAdapter().notifyDataSetChanged();
                        }else{
                            Collections.sort(adapterList, new Comparator<ConversationsModel>() {
                                @Override
                                public int compare(ConversationsModel o2, ConversationsModel o1) {
                                    return Long.compare(o1.getLastTime(),o2.getLastTime());
                                }
                            });
                            Collections.sort(keys, Comparator.comparing(item -> adapterList.indexOf(item)));
                            //((ConversationsListAdapter)list.getAdapter()).updateList(conversations_list);
                            list.getAdapter().notifyDataSetChanged();
                        }
                    }
                    conversations_list.clear();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };

        }

        for(int i = 0; i < listeners.length; i++){
            chatRef.child(keys.get(i)).child("messages").limitToLast(1).addValueEventListener(listeners[i]);
        }
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        if(id == NotificationCenter.didClickConversation){
            presentFragment(new ChatFragment((String) args[0]));
        }
    }

}