package com.example.myapplication.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.datastructures.chatty.R;
import com.example.myapplication.models.UserModel;
import com.example.myapplication.utils.UsersRecyclerViewClick;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserViewHolder> {

    public ArrayList<UserModel> getUsersList() {
        return usersList;
    }

    private ArrayList<UserModel> usersList ;
    private final UsersRecyclerViewClick usersRecyclerViewClick;
    private boolean dark = false;


    public  UserListAdapter(ArrayList<UserModel> arrayList,
                           UsersRecyclerViewClick usersRecyclerViewClick , boolean dark) {
        usersList = arrayList;
        this.usersRecyclerViewClick = usersRecyclerViewClick;
        this.dark = dark;
    }



    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (dark)
          view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_item_dark,parent,false);
        else
          view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_item,parent,false);

        return new UserViewHolder(view,usersRecyclerViewClick);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.name.setText(usersList.get(position).getName());
        holder.msg.setText(usersList.get(position).getMsg());
        loadImage(holder.image, holder.itemView,usersList.get(position).getImageUri());

    }



    @SuppressLint("NotifyDataSetChanged")
    public void setList(ArrayList<UserModel> usersList){
        this.usersList = usersList;
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void addToList(UserModel userModel){
        this.usersList.add(userModel);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView msg;
        CircleImageView image;
        public UserViewHolder(@NonNull View itemView,UsersRecyclerViewClick usersRecyclerViewClick) {
            super(itemView);
            this.name =(TextView) itemView.findViewById(R.id.name);
            this.msg = (TextView) itemView.findViewById(R.id.msg);
            this.image = (CircleImageView) itemView.findViewById(R.id.current_user_profile_image);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(usersRecyclerViewClick != null){
                        int pos= getAdapterPosition();
                        if(pos != RecyclerView.NO_POSITION){
                            usersRecyclerViewClick.onUserClickListener(pos);
                        }
                    }
                }
            });
        }
    }

    //TODO
    public static void loadImage(CircleImageView circleImageView, View view, String url){
        Glide.with(view).load(url).diskCacheStrategy(DiskCacheStrategy.ALL).into(circleImageView);

    }
}
