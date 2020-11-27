package com.tyron.hanapbb.ui.components;

import android.os.Message;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import com.tyron.hanapbb.ui.models.MessagesModel;

import java.util.List;

public class DiffUtilCallback extends DiffUtil.Callback {

    List<MessagesModel> oldMessages;
    List<MessagesModel> newMessages;

    public DiffUtilCallback(List<MessagesModel> oldMessages, List<MessagesModel> newMessages){
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
        return oldMessages.get(oldItemPosition).equals(newMessages.get(newItemPosition));
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldMessages.get(oldItemPosition).getMessage().equals(newMessages.get(newItemPosition).getMessage());
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {

        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}
