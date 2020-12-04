package com.tyron.hanapbb.ui.components;

import android.util.Log;

import androidx.annotation.Nullable;
import android.recyclerview.widget.DiffUtil;

import com.tyron.hanapbb.ui.models.ConversationsModel;

import java.util.List;

public class DiffUtilCallback extends DiffUtil.Callback {

    List<ConversationsModel> oldMessages;
    List<ConversationsModel> newMessages;

    public DiffUtilCallback(List<ConversationsModel> oldMessages, List<ConversationsModel> newMessages){
        this.oldMessages = oldMessages;
        this.newMessages = newMessages;
    }
    @Override
    public int getOldListSize() {
        return oldMessages.size();
    }

    @Override
    public int getNewListSize() {
        return newMessages.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldMessages.get(oldItemPosition).getLastUid().equals(newMessages.get(newItemPosition).getLastUid());
        //return false;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        boolean s = oldMessages.get(oldItemPosition).getLastTime() == newMessages.get(newItemPosition).getLastTime();
        Log.d("HanapBb/DiffUtil", "areContentsTheSame: " + s);
        return s;
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {

        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}
