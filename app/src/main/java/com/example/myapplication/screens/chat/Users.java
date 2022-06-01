package com.example.myapplication.screens.chat;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.datastructures.chatty.R;
import com.example.myapplication.adapters.UserListAdapter;
import com.example.myapplication.models.UserModel;
import com.example.myapplication.screens.chatroom.ChatRoom;
import com.example.myapplication.utils.SharedPreferenceClass;
import com.example.myapplication.utils.UsersRecyclerViewClick;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Queue;

public class Users extends Fragment implements UsersRecyclerViewClick {

    static final int PICK_CONTACT=1;
    private ArrayList<UserModel> userModels;
    private ArrayList<String> friendsNumbers;
    private UserListAdapter userListAdapter;
    private String phone;
    private SharedPreferences sharedPreferences;
    private ArrayList<String> friends ;
    private final CollectionReference usersReference = FirebaseFirestore.getInstance().collection("users");

    private boolean clicked = false;
    private Animation rotOpen;
    private Animation rotClose;
    private Animation toBottom;
    private Animation fromBottom;
    private FloatingActionButton addFab ;
    private FloatingActionButton addExisting ;
    private FloatingActionButton addNew ;
    private FloatingActionButton addGroup ;
    private RecyclerView recyclerView;




    public Users() {}


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view= LayoutInflater.from(getContext()).inflate(R.layout.fragment_users,container,false);

        recyclerView = view.findViewById(R.id.users_RV);
        //Animation initialization
        rotOpen = AnimationUtils.loadAnimation(getContext() , R.anim.rotate_open_anim);
        rotClose = AnimationUtils.loadAnimation(getContext() , R.anim.rotate_close_anim);
        toBottom = AnimationUtils.loadAnimation(getContext() , R.anim.to_bottom_anim);
        fromBottom = AnimationUtils.loadAnimation(getContext() , R.anim.from_bottom_anim);

        addFab =  view.findViewById(R.id.add_fab);
        addExisting =  view.findViewById(R.id.add_existing);
        addNew =  view.findViewById(R.id.add_new);
        addGroup = view.findViewById(R.id.add_new_group);

        sharedPreferences = this.getActivity().getSharedPreferences("mypref", Context.MODE_PRIVATE);
        phone = sharedPreferences.getString("phone", null);

        userModels = new ArrayList<>();
        buildList();
        int nightModeFlags =
                this.getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            userListAdapter = new UserListAdapter(userModels, this , true);
        } else {
            userListAdapter = new UserListAdapter(userModels, this , false);
        }
        new ItemTouchHelper(itemTouchHelper).attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(userListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //Adding Existing user
        addExisting.setOnClickListener(view1 -> openContacts());
        //Adding new user
        addNew.setOnClickListener(view12 -> addNewContact());
        //FAB of addition
        addFab.setOnClickListener(view13 -> onAddButtonClicked());
        addGroup.setOnClickListener(view14 -> OnNewGroupPressed());


        return view;
    }

    private void OnNewGroupPressed() {
        ArrayList<String> filteredNumbers = new ArrayList<>();
        Intent intent = new Intent(this.getActivity() , AddGroupActivity.class);
        for (String num : friendsNumbers){
            if (num.length() == 11){
                filteredNumbers.add(num);
            }
        }
        intent.putExtra("phones" ,filteredNumbers);
        startActivity(intent);
    }

    //waiting for result from contacts
    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        if (reqCode == PICK_CONTACT) {
            if (resultCode == Activity.RESULT_OK) {
                getSelectedPhoneNumber(data);
            }
        }
    }


    //gets the contact from contacts cursor
    private void getSelectedPhoneNumber(Intent data) {
        Uri contactData = data.getData();

        Cursor c =  requireActivity().managedQuery(contactData, null, null, null, null);
        if (c.moveToFirst()) {
            String id =c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
            String hasPhone =c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER));

            if (hasPhone.equalsIgnoreCase("1")) {
                Cursor phones = requireActivity().getContentResolver().query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ id,
                        null, null);
                phones.moveToFirst();
                String selectedContactNumber = phones.getString(phones.getColumnIndexOrThrow("data1"));
                selectedContactNumber = processPhone(selectedContactNumber);
                phones.close();
                Log.d("user :::::" , selectedContactNumber);
                addToFriends(selectedContactNumber);
            }
        }
    }

    private void addToFriends(String friendPhone) {
        try {
            DocumentReference docRef = usersReference.document(phone);
            docRef.get().addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if(doc.exists()){
                        DocumentReference friendReference = usersReference.document(friendPhone);
                        friends = (ArrayList<String>) doc.get("friends");
                        friendReference.get().addOnCompleteListener(task1 -> {
                            DocumentSnapshot doc2 = task1.getResult();
                            if(doc2.exists()){
                                if(friends != null){
                                    if(!friends.contains(friendPhone)){
                                        if(!friendPhone.equals(phone)){
                                            friends.add(friendPhone);
                                            docRef.update("friends", friends);
                                            getUserData(friendPhone);
                                        }else{
                                            Toast.makeText(getActivity(), "Error ", Toast.LENGTH_SHORT).show();
                                        }
                                    }else{
                                        Toast.makeText(getActivity(), "User Already Exists !!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }else{
                                Toast.makeText(getActivity(), "User doesn't Exist !!", Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                }
            });
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    private void getUserData(String phone ) {
        try {
            DocumentReference docRef = usersReference.document(phone);

            docRef.get().addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if(doc.exists()){

                        UserModel user = new UserModel();
                        user.setName(Objects.requireNonNull(doc.get("name")).toString());
                        user.setMsg(Objects.requireNonNull(doc.get("description")).toString());
                        user.setImageUri(Objects.requireNonNull(doc.get("profileImageUrl")).toString());
                        user.setPhone(phone);
                        userListAdapter.addToList(user);
                    }else {
                        Toast.makeText(getActivity(),
                                "User doesn't exist !",
                                Toast.LENGTH_LONG).show();
                    }
                }
            });
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

    }

    private String processPhone(String selectedContactNumber) {
        StringBuilder str = new StringBuilder();

        for (int i = 0 ; i < selectedContactNumber.length();i++){
            if(selectedContactNumber.charAt(i) != ' ' ||selectedContactNumber.charAt(i) != '+')
                str.append(selectedContactNumber.charAt(i));
        }
        Log.d("user ::::::::::::::" ,str.toString().trim().substring(str.length()-11,str.length()) );
        return str.toString().trim().substring(str.length()-11,str.length());

    }

    //grants permission and opens contacts app
    private void openContacts() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[] { Manifest.permission.READ_CONTACTS }, PICK_CONTACT);
        }
        else {
            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            startActivityForResult(intent, PICK_CONTACT);
        }
    }
    //grants permission and opens new contact activity
    private void addNewContact() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[] { Manifest.permission.WRITE_CONTACTS }, PICK_CONTACT);
        }
        else
            AddNewContact.start(getContext());
    }

    //floating button functionalities
    private void onAddButtonClicked() {
        setVisibility(clicked);
        setAnimation(clicked);
        setClickable(clicked);
        clicked = !clicked;
    }
    //animation setters
    private void setAnimation( boolean clicked) {
        if(!clicked) {
            addExisting.startAnimation(fromBottom);
            addGroup.startAnimation(fromBottom);
            addNew.startAnimation(fromBottom);
            addFab.startAnimation(rotOpen);
        }else{

            addExisting.startAnimation(toBottom);
            addNew.startAnimation(toBottom);
            addGroup.startAnimation(toBottom);
            addFab.startAnimation(rotClose);

        }
    }
    //visibility setters
    private void setVisibility( boolean clicked) {
        if(!clicked){
            addExisting.setVisibility(View.VISIBLE);
            addNew.setVisibility(View.VISIBLE);
            addGroup.setVisibility(View.VISIBLE);
        }else {
            addExisting.setVisibility(View.INVISIBLE);
            addNew.setVisibility(View.INVISIBLE);
            addGroup.setVisibility(View.INVISIBLE);
        }
    }
    //clickability setter
    private void setClickable(boolean clicked){
        if (!clicked){
            addExisting.setClickable(true);
            addNew.setClickable(true);
            addGroup.setClickable(true);

        }else {
            addExisting.setClickable(false);
            addNew.setClickable(false);
            addGroup.setClickable(false);

        }

    }

    private void buildList(){
        try {
            DocumentReference docRef = usersReference.document(phone);
            docRef.get().addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if(doc.exists()){
                        friendsNumbers = (ArrayList<String>) doc.get("friends");
                        for (int i = 0 ; i< friendsNumbers.size();i++){
                            getUserData(friendsNumbers.get(i));
                        }
                    }
                    else {
                        Toast.makeText(this.getActivity(),
                                "User doesn't exist !",
                                Toast.LENGTH_LONG).show();
                    }
                }
            });
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void onUserClickListener(int position) {
        Intent intent = new Intent(getContext() , ChatRoom.class);
        intent.putExtra("name", userModels.get(position).getName());
        intent.putExtra("phone", phone);
        intent.putExtra("RecPhone", userModels.get(position).getPhone());
        intent.putExtra("image", userModels.get(position).getImageUri());
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }

    ItemTouchHelper.SimpleCallback itemTouchHelper = new ItemTouchHelper.SimpleCallback(0 , ItemTouchHelper.RIGHT) {
        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            Drawable icon = ContextCompat.getDrawable(getContext(),R.drawable.delete);
            ColorDrawable background = new ColorDrawable(Color.RED);
            View itemView = viewHolder.itemView;

            int backgroundCornerOffset =21;
            int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight())/2;
            int iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight())/2;
            int iconBottom = iconTop + icon.getIntrinsicHeight();

            if(dX>0){//right
                int iconLeft = itemView.getLeft() + iconMargin;
                int iconRight = itemView.getLeft() + iconMargin + icon.getIntrinsicWidth();
                icon.setBounds(iconLeft,iconTop,iconRight,iconBottom);
                background.setBounds(itemView.getLeft(),itemView.getTop()
                        , itemView.getLeft() + ((int)dX) + backgroundCornerOffset,itemView.getBottom());
            }else{
                background.setBounds(0,0,0,0);
            }
            background.draw(c);
            icon.draw(c);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Delete task");
            builder.setMessage("Are you sure you want do delete this ?");
            builder.setPositiveButton("Confirm", (dialogInterface, i) -> {
                String removedPhone = userModels.get(viewHolder.getAdapterPosition()).getPhone();
                userModels.remove(viewHolder.getAdapterPosition());
                friendsNumbers.remove(removedPhone);
                usersReference.document(phone).update("friends" , friendsNumbers);
                userListAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
            });
            builder.setNegativeButton(android.R.string.cancel, (dialogInterface, i) -> userListAdapter.notifyItemChanged(viewHolder.getAdapterPosition()));
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    };
}