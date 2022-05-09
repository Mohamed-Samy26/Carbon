package com.datastructures.chatty.screens.chatroom;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.datastructures.chatty.R;
import com.datastructures.chatty.adapters.MessageAdapter;
import com.datastructures.chatty.screens.chat.VideoActivity;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class ChatRoom_activity extends AppCompatActivity {

    //references to activity view items
    private RecyclerView mMessageRecycler;
    private MessageAdapter mMessageAdapter;
    private EditText msgTxt;
    private ImageButton vidCall;

    //local state and current user handle
    private final User currentUser = new User("user" , "user");
    private User calleeUser ;
    private String currentMsg;

    //required data structures
    private ArrayList<Message> messageList = new ArrayList<>();
    private Map<String,String> lastMessage = new HashMap<>();

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide(); //to hide action bar
        setContentView(R.layout.activity_chat_room);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
               finish();
            } else {
               calleeUser = new User(extras.getString("name"), extras.getString("RecPhone"));
            }
        } else {
            calleeUser = new User((String) savedInstanceState.getSerializable("name"),
                    (String) savedInstanceState.getSerializable("RecPhone"));
        }

        System.out.println(">>>>>>>>>>>>>>>>>>>>"+calleeUser.getProfile()+" " + calleeUser.getName());
        //database references
        String currentDoc = getDoc(currentUser.getProfile(), calleeUser.getProfile());
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference docRef = db.collection("chatRooms").document(currentDoc);
        CollectionReference collRef = docRef.collection("Messages");
        collRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Message msg = document.toObject(Message.class);
                    messageList.add(msg);
                }
            } else {
                Log.d(TAG, "Error getting documents: ", task.getException());
            }
        });
        //get UI references
        Button sendBtn = findViewById(R.id.sendBtn);
        msgTxt = findViewById(R.id.msgTxt);
        vidCall = findViewById(R.id.vidCall);

        //resolving messages list and passing it to the view adapter
        mMessageRecycler = findViewById(R.id.recycler_chat);
        mMessageAdapter = new MessageAdapter(this, messageList);
        mMessageRecycler.setLayoutManager(new LinearLayoutManager(this));
        mMessageRecycler.setAdapter(mMessageAdapter);
        RecyclerView rv_messages = this.mMessageRecycler;
        rv_messages.setAdapter(this.mMessageAdapter);

        //listen to chat
        collRef.addSnapshotListener((value, e) -> {
            if (e != null) {
                Log.w(TAG, "Listen failed.", e);
                return;
            }
            ArrayList<Message> newMessages = new ArrayList<>();
            if(value != null) {
                for (QueryDocumentSnapshot doc : value) {
                    Message msg = doc.toObject(Message.class);
                    newMessages.add(msg);
//                Log.d(TAG, ">>>>>>>>>>>>>>>>>>>>> Current Messages in Coll: " + msg.getText() +"\n");
                }
            }
            //Log.d(TAG, "Current Messages in Coll: " + newMessages);
//            System.out.println( ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+ newMessages.size());
            mMessageAdapter.notifyDataSetChanged();
        });

        vidCall.setOnClickListener(view -> {
            Intent intent = new Intent(ChatRoom_activity.this, VideoActivity.class);
            startActivity(intent);
        });
        //Send button logic
        sendBtn.setOnClickListener(view -> {
            currentMsg = msgTxt.getText().toString().trim();
            if (!currentMsg.equals(""))
            {
                // Add a new message object to the end of messages arraylist
                Message tmp = new Message(currentMsg ,
                        new Timestamp(System.currentTimeMillis()).toString()
                        , currentUser.getProfile() );

                messageList.add(mMessageAdapter.getItemCount(), tmp);
                //update the view state and scroll to the new message
                mMessageAdapter.notifyItemInserted(mMessageAdapter.getItemCount());
                mMessageRecycler.smoothScrollToPosition(mMessageAdapter.getItemCount());

                //Send message to firebase and handle success / failure
                lastMessage.put("text" , tmp.getText());
                lastMessage.put("time" , tmp.getTime());
                lastMessage.put("user" , tmp.getUser());
                collRef.document()
                        .set(lastMessage, SetOptions.merge())
                        .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully written!"))
                        .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));
                //empty the message holder to receive the next message
                msgTxt.setText("");
                Log.d(TAG, ">>>>>>>>>>>>>>>>>>>>> Currentpath: " + docRef.getPath() +" "+ db.getApp() + " " + docRef.getId() +"\n");
            }
        });
    }
    private String getDoc(String num1 , String num2){
            if(num1.compareTo(num2) > 0)
                    return num1+num2;
            else return num2+num1;
    }
}