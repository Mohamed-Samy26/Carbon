package com.example.myapplication.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.datastructures.chatty.R;
import com.example.myapplication.models.GroupNumbersModel;

import java.util.ArrayList;
import java.util.List;

public class GroupsRecyclerAdapter extends RecyclerView.Adapter<GroupsRecyclerAdapter.MyViewHolder> {

    private final List<GroupNumbersModel> mGroupNumbersModelList;

    public GroupsRecyclerAdapter(ArrayList<GroupNumbersModel> groupNumbersModelList) {
        mGroupNumbersModelList = groupNumbersModelList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_recycler_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final GroupNumbersModel groupNumbersModel = mGroupNumbersModelList.get(position);
        holder.textView.setText(groupNumbersModel.getText());
        holder.view.setBackgroundColor(groupNumbersModel.isSelected() ? Color.CYAN : Color.WHITE);
        holder.textView.setOnClickListener(view -> {
            groupNumbersModel.setSelected(!groupNumbersModel.isSelected());
            holder.view.setBackgroundColor(groupNumbersModel.isSelected() ? Color.CYAN : Color.WHITE);
        });
    }

    @Override
    public int getItemCount() {
        return mGroupNumbersModelList == null ? 0 : mGroupNumbersModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final View view;
        private final TextView textView;

        private MyViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            textView = itemView.findViewById(R.id.text_view);
        }
    }
}
