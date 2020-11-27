package com.tyron.hanapbb.ui.models;

import com.google.firebase.database.Exclude;

public class ConversationsModel {
    private String chat_id;
    private String lastMessage;
    public ConversationsModel(){

    }
    public ConversationsModel(String chat_id){
        this.chat_id = chat_id;
    }

    public String getChat_id(){
        return chat_id;
    }
    public void setChat_id(String chat_id){
        this.chat_id = chat_id;
    }

    @Exclude
    public String getLastMessage(){
        return lastMessage;
    }
    public void setLastMessage(String message){
        this.lastMessage = message;
    }
}
