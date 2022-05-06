package com.datastructures.chatty.screens.chat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.datastructures.chatty.R;
import com.datastructures.chatty.adapters.UserListAdapter;
import com.datastructures.chatty.models.UserModel;
import com.datastructures.chatty.screens.chatroom.ChatRoom_activity;
import com.datastructures.chatty.utils.UsersRecyclerViewClick;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Queue;

public class Users extends Fragment implements UsersRecyclerViewClick {

    static final int PICK_CONTACT=1;
    private boolean clicked = false;
    ArrayList<UserModel> userModels;
    private Animation rotOpen;
    private Animation rotClose;
    private Animation toBottom;
    private Animation fromBottom;
    private FloatingActionButton addFab ;
    private FloatingActionButton addExisting ;
    private FloatingActionButton addNew ;


    public Users() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        binding = FragmentUsers inflate(getLayoutInflater());
//        userViewModel = new UserViewModel();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= LayoutInflater.from(getContext()).inflate(R.layout.fragment_users,container,false);
        RecyclerView recyclerView = view.findViewById(R.id.users_RV);
        //Animation initialization
        rotOpen = AnimationUtils.loadAnimation(getContext() , R.anim.rotate_open_anim);
        rotClose = AnimationUtils.loadAnimation(getContext() , R.anim.rotate_close_anim);
        toBottom = AnimationUtils.loadAnimation(getContext() , R.anim.to_bottom_anim);
        fromBottom = AnimationUtils.loadAnimation(getContext() , R.anim.from_bottom_anim);
        
        addFab =  view.findViewById(R.id.add_fab);
        addExisting =  view.findViewById(R.id.add_existing);
        addNew =  view.findViewById(R.id.add_new);

        userModels = buildList();
        recyclerView.setAdapter(new UserListAdapter(userModels, this));
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //Adding Existing user
        addExisting.setOnClickListener(view1 -> openContacts());
        //Adding new user
        addNew.setOnClickListener(view12 -> addNewContact());
        //FAB of addition
        addFab.setOnClickListener(view13 -> onAddButtonClicked());


        return view;
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
                System.out.println(processPhone(selectedContactNumber));
                phones.close();
            }
        }
    }

    private String processPhone(String selectedContactNumber) {
        Queue<Character> phone =  new PriorityQueue<>();
        StringBuilder str = new StringBuilder();

        for (int i = 0 ; i < selectedContactNumber.length();i++){
            if(selectedContactNumber.charAt(i) != ' ' ||selectedContactNumber.charAt(i) != '+' )
                phone.add(selectedContactNumber.charAt(i));
            str.append(phone.peek());
            phone.poll();
        }
        return str.toString();

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
             addNew.startAnimation(fromBottom);
             addFab.startAnimation(rotOpen);
        }else{

             addExisting.startAnimation(toBottom);
             addNew.startAnimation(toBottom);
             addFab.startAnimation(rotClose);

        }
    }
    //visibility setters
    private void setVisibility( boolean clicked) {
        if(!clicked){
            addExisting.setVisibility(View.VISIBLE);
            addNew.setVisibility(View.VISIBLE);
        }else {
            addExisting.setVisibility(View.INVISIBLE);
            addNew.setVisibility(View.INVISIBLE);
        }
    }
    //clickability setter
    private void setClickable(boolean clicked){
        if (!clicked){
            addExisting.setClickable(true);
            addNew.setClickable(true);
        }else {
            addExisting.setClickable(false);
            addNew.setClickable(false);

        }

    }
    private ArrayList<UserModel> buildList(){
        ArrayList<UserModel> arrayList = new ArrayList<>();
        String address = "mipmap-mdpi/user_img.png";
        arrayList.add(new UserModel("Eljoo" , "Yasta ana b7bk yasta",address, "123"));
        arrayList.add(new UserModel("Samy" , "El3nkboot el nono",address, "123"));
        arrayList.add(new UserModel("Kimo" , "Yalla Valo",address, "123"));
        arrayList.add(new UserModel("Omar" , "Yalla Generalz",address, "123"));
        arrayList.add(new UserModel("Nofal" , "Ana mosh 3arefny ana toht mny",address,"123"));
        return arrayList;
    }

    @Override
    public void onUserClickListener(int position) {
        Intent intent = new Intent(getContext() , ChatRoom_activity.class);
        intent.putExtra("name", userModels.get(position).getName());
        intent.putExtra("RecPhone", userModels.get(position).getPhone());
        intent.putExtra("image", userModels.get(position).getImageUri());

        startActivity(intent);
    }
}