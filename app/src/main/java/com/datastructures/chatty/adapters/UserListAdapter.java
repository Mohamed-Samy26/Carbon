package com.datastructures.chatty.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.datastructures.chatty.R;
import com.datastructures.chatty.models.UserModel;
import com.datastructures.chatty.utils.UsersRecyclerViewClick;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserViewHolder> {
    private ArrayList<UserModel> usersList ;
    private final UsersRecyclerViewClick usersRecyclerViewClick;
    public UserListAdapter(ArrayList<UserModel> arrayList,
                           UsersRecyclerViewClick usersRecyclerViewClick) {
        usersList = arrayList;
        this.usersRecyclerViewClick = usersRecyclerViewClick;
    }



    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_item,parent,false);

        return new UserViewHolder(view,usersRecyclerViewClick);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.name.setText(usersList.get(position).getName());
        holder.msg.setText(usersList.get(position).getMsg());
//        holder.image.setImageBitmap(getUserImage(usersList.get(position).getImageUri()));

    }

//    private Bitmap getUserImage(String encodedImage) {
//        byte[] bytes = Base64.getDecoder().decode(encodedImage);
//        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
//    }

    @SuppressLint("NotifyDataSetChanged")
    public void setList(ArrayList<UserModel> usersList){
        this.usersList = usersList;
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
            this.image = (CircleImageView) itemView.findViewById(R.id.profile_image);

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
}
