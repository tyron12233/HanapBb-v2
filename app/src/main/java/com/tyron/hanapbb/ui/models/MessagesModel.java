package com.tyron.hanapbb.ui.models;

public class MessagesModel
{
    private String uid;

    private String messageId;

    private long time;

    private String message;

    private boolean reply;

    private int type;
    private String replyUid;

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
}