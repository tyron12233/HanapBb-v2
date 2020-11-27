package com.tyron.hanapbb.ui.models;

public class ConversationsIdModel {
    private String chat_id;

    public ConversationsIdModel(){

    }
    public ConversationsIdModel(String id){
        chat_id = id;
    }
    public void setChat_id(String chat_id) {
        this.chat_id = chat_id;
    }

    public String getChat_id() {
        return chat_id;
    }
}
