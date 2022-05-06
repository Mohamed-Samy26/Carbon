package com.datastructures.chatty.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.datastructures.chatty.R;
import com.datastructures.chatty.screens.chatroom.Message;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;


public class MessageAdapter extends RecyclerView.Adapter {
    @NonNull
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy" , Locale.ENGLISH); //to handle formatting dates
    private Context mContext;
    private List<Message> mMessageList;
    public MessageAdapter(Context context, List<Message> messageList) {
        mContext = context;
        mMessageList = messageList;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType == 1){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.user_msg, parent, false);
            return new SentMessageHolder(view);
        }
        else if(viewType == 2){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.other_msg, parent, false);
            return new ReceivedMessageHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = (Message) mMessageList.get(position);

        switch (holder.getItemViewType()) {
            case 1:
                ((SentMessageHolder) holder).bind(message);
                break;
            case 2:
                ((ReceivedMessageHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return mMessageList != null ? mMessageList.size() : 0;
    }
    private class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, nameText;
        ImageView profileImage;

        SentMessageHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.TVuser_msg);
            timeText = itemView.findViewById(R.id.TVuser_ts);
            //nameText =  itemView.findViewById(R.id.TVuserDate);
            //profileImage = itemView.findViewById(R.id.sender_img);
        }

        void bind(Message message) {
            messageText.setText(message.getText());
            // Format the stored timestamp into a readable String using method.
            timeText.setText(  new Timestamp(System.currentTimeMillis()).toString());
            //nameText.setText(message.getUser().getName());
        }
    }
    public int getItemViewType(int position) {
        Message message = (Message) mMessageList.get(position);

        if (message.getUser().equals("user")) { // TODO: create a shared preference of profile
            // If the current user is the sender of the message
            return 1;
        } else {
            // If another user sent the message
            return 2;
        }
    }
    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, nameText;
        ImageView profileImage;

        ReceivedMessageHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.TVrec_txt);
            timeText =  itemView.findViewById(R.id.TVrec_timeStamp);
            nameText =  itemView.findViewById(R.id.TVsender);
            //profileImage = itemView.findViewById(R.id.sender_img);
        }
        void bind(@NonNull Message message) {
            messageText.setText(message.getText());
            timeText.setText(  new Timestamp(System.currentTimeMillis()).toString());
            nameText.setText(message.getUser());
        }
    }

}

