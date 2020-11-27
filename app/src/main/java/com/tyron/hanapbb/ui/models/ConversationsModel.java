package com.tyron.hanapbb.ui.models;

import androidx.annotation.Nullable;

import com.google.firebase.database.Exclude;
import com.tyron.hanapbb.ui.fragments.ConversationsList;

public class ConversationsModel {
  //  private String chat_id;
    private String lastMessage;
    private long time;
    private String uid;
    private String userId;

    public ConversationsModel(){

    }
    public ConversationsModel(String lastMessage, long time, String uid, String UserId){
        this.lastMessage = lastMessage;
        this.time = time;
        this.uid = uid;
        this.userId = UserId;
    }

//    public String getChat_id(){
//        return chat_id;
//    }
//    public void setChat_id(String chat_id){
//        this.chat_id = chat_id;
//    }

    @Exclude
    public String getLastMessage(){
        return lastMessage;
    }
    public void setLastMessage(String message){
        this.lastMessage = message;
    }

    public void setLastTime(long time) {
        this.time = time;
    }
    @Exclude
    public long getLastTime(){
        return time;
    }


    public void setLastUid(String uid) {
        this.uid = uid;
    }
    public String getLastUid(){
        return uid;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
