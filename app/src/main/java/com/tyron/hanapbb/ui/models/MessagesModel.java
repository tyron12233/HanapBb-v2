package com.tyron.hanapbb.ui.models;

import com.google.firebase.database.Exclude;

public class MessagesModel
{
    private String uid;

    private String messageId;

    private long time;

    private String message;

    private boolean reply;

    private int type;
    private String replyUid;
    private String temporaryPhoto;
    private boolean uploading;
    private String photoUrl;

    public String getUid ()
    {
        return uid;
    }

    public void setUid (String uid)
    {
        this.uid = uid;
    }

    public String getMessageId ()
    {
        return messageId;
    }

    public void setMessageId (String messageId)
    {
        this.messageId = messageId;
    }

    public long getTime ()
    {
        return time;
    }

    public void setTime (long time)
    {
        this.time = time;
    }

    public String getMessage ()
    {
        return message;
    }

    public void setMessage (String message)
    {
        this.message = message;
    }

    public boolean getReply ()
    {
        return reply;
    }

    public void setReply (boolean reply)
    {
        this.reply = reply;
    }

    public int getType ()
    {
        return type;
    }

    public void setType (int type)
    {
        this.type = type;
    }

    public void setReplyUid(String uid) {
        this.replyUid = uid;
    }
    public String getReplyUid(){
        return replyUid;
    }

    @Exclude
    public String getTemporaryPhoto(){
        return temporaryPhoto;
    }
    public void setTemporaryPhoto(String path) {
        temporaryPhoto = path;
    }
    @Exclude
    public void setUploading(boolean b) {
        uploading = b;
    }
    @Exclude
    public boolean isUploading(){
        return uploading;
    }

    public String getPhotoUrl(){
        return photoUrl;
    }
    public void setPhotoUrl(String photoUrl){
        this.photoUrl = photoUrl;
    }
}