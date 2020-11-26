package com.tyron.hanapbb.ui.adapters;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tyron.hanapbb.R;
import com.tyron.hanapbb.ui.models.ConversationsModel;

import java.util.List;

public class ConversationsListAdapter extends RecyclerView.Adapter<ConversationsListAdapter.ViewHolder> {

    private List<ConversationsModel> model;

    public ConversationsListAdapter(List<ConversationsModel> list){
        model = list;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.conversation_list_item_view,parent,false);
        return new ConversationsListAdapter.ViewHolder(v,viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        View view = holder.itemView;
        final TextView textview_name = view.findViewById(R.id.textview_chatname);

        textview_name.setTypeface(textview_name.getTypeface(), Typeface.BOLD);
    }

    @Override
    public int getItemCount() {
        if (model.size() >= 1) return model.size();
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public ViewHolder(View v, int viewType) {
            super(v);
        }
    }
}
