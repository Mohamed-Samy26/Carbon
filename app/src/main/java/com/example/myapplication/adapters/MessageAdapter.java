package com.example.myapplication.adapters;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.datastructures.chatty.R;
import com.example.myapplication.screens.chatroom.Message;

import java.io.IOException;
import java.util.ArrayList;


public class MessageAdapter extends RecyclerView.Adapter {

    @NonNull
    private Context mContext;
    private ArrayList<Message> mMessageList;
    private String currentUser;
    private static MediaPlayer mediaPlayer = new MediaPlayer();
    private Runnable runnable;
    private Handler mHandler = new Handler();


    public MessageAdapter(Context context, ArrayList<Message> messageList , String currentUser) {
        mContext = context;
        mMessageList = messageList;
        this.currentUser = currentUser;
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
        else if(viewType == 3){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.record_item, parent, false);
            return new SentRecordHolder(view);
        }
        else if(viewType == 4){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.othr_record_item, parent, false);
            return new ReceivedRecordHolder(view);
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
                break;
            case 3:
                assert holder instanceof SentRecordHolder;
                ((SentRecordHolder) holder).bind(message);
                break;
            case 4:
                assert holder instanceof ReceivedRecordHolder;
                ((ReceivedRecordHolder) holder).bind(message);
                break;
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
        }

        void bind(Message message) {
            messageText.setText(message.getText());
            timeText.setText(formateTime(message));
        }
    }
    public int getItemViewType(int position) {
        Message message = (Message) mMessageList.get(position);
        if (message.getUser().equals(currentUser) && !message.getIsRecord()) {
            return 1;
        }
        else if (!message.getUser().equals(currentUser) && !message.getIsRecord()) {
            return 2;
        }
        else if (message.getUser().equals(currentUser) && message.getIsRecord()) {
            return 3;
        }
        else{return 4;}
    }
    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, nameText;

        ReceivedMessageHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.TVrec_txt);
            timeText =  itemView.findViewById(R.id.TVrec_timeStamp);
            nameText =  itemView.findViewById(R.id.TVsender);
        }
        void bind(@NonNull Message message) {
            messageText.setText(message.getText());
            timeText.setText(formateTime(message));
            nameText.setText(message.getUser());
        }
    }
    private class ReceivedRecordHolder extends RecyclerView.ViewHolder {

        Button fab;
        SeekBar seekBar;
        TextView nameText;
        ReceivedRecordHolder(View itemView) {
            super(itemView);
            fab = itemView.findViewById(R.id.play);
            seekBar =  itemView.findViewById(R.id.seekBar);
            nameText =  itemView.findViewById(R.id.recordUser);
        }
        void bind(@NonNull Message message) {
            nameText.setText(message.getUser());
            fab.setOnClickListener(view -> {
                if (mediaPlayer != null){
                    if (mediaPlayer.isPlaying()){
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                        mediaPlayer.release();
                    }
                }
                mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(message.getText());
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                seekBar.setMax( mediaPlayer.getDuration());
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        seekBar.setProgress(mediaPlayer.getCurrentPosition());
                        mHandler.postDelayed(runnable, 1000);
                        mHandler.postDelayed(runnable, 1000);
                    }
                };

                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                        if (b)
                            mediaPlayer.seekTo(i);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }

                });
                mediaPlayer.start();
            });
        }
    }
    private class SentRecordHolder extends RecyclerView.ViewHolder {
        Button fab;
        SeekBar seekBar;
        TextView nameText;
        SentRecordHolder(View itemView) {
            super(itemView);
            fab = itemView.findViewById(R.id.play);
            seekBar =  itemView.findViewById(R.id.seekBar);
            nameText =  itemView.findViewById(R.id.recordUser);
        }
        void bind(@NonNull Message message) {
            nameText.setText(message.getUser());
            fab.setOnClickListener(view -> {
                if (mediaPlayer != null){
                    if (mediaPlayer.isPlaying()){
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                        mediaPlayer.release();
                    }
                }
                mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(message.getText());
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                seekBar.setMax( mediaPlayer.getDuration());
                runnable = () -> {
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    mHandler.postDelayed(runnable, 1000);
                };
                mHandler.postDelayed(runnable, 1000);
                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                        if (b)
                            mediaPlayer.seekTo(i);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }

                });
                mediaPlayer.start();
            });
        }
    }

    private String formateTime(Message message){
        return message.getTime().substring(5 ,
                message.getTime().length()-7 >0 ?
                        message.getTime().length()-7
                        : message.getTime().length());
    }
}

