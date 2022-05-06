package com.datastructures.chatty.screens.chat;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.datastructures.chatty.R;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

public class VideoActivity extends  AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
    }

    public void onButtonClick(View view) {

        EditText roomIdInput = findViewById(R.id.room_id);
        String roomId = roomIdInput.getText().toString();

        if(roomId.length() > 0){
            JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions
                    .Builder()
                    .setRoom(roomId)
                    .build();
            JitsiMeetActivity.launch(this,options);


        }

    }
}