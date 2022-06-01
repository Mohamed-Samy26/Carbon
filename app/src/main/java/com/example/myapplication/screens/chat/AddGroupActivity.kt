package com.example.myapplication.screens.chat

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.adapters.GroupsRecyclerAdapter
import com.example.myapplication.models.GroupNumbersModel
import com.example.myapplication.ui.main.Home
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_create_group.*
import java.sql.Timestamp


class AddGroupActivity : AppCompatActivity() {

    // Declaring the DataModel Array
    private var groupNumbersModels : ArrayList<GroupNumbersModel> = ArrayList()
    private var finalNumbers: ArrayList<String> = ArrayList()
    private val sharedPreferences by lazy {
        getSharedPreferences("mypref" , MODE_PRIVATE)
    }
    private val phone by lazy {
        sharedPreferences.getString("phone" , null)
    }
    private val phones by lazy {
        intent.extras!!.getStringArrayList("phones")
    }
    private val collectionReference = FirebaseFirestore.getInstance().collection("users")


    private var groupMap: MutableMap<String, Any> = HashMap()
    private var groupImgUri :Uri? = null
    // Declaring the elements from the main layout file


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        finalNumbers.add(phone.toString())
        setContentView(com.datastructures.chatty.R.layout.activity_create_group)
        // Setting the adapter
        Log.d("data model" , phones.toString())


        val mAdapter = GroupsRecyclerAdapter(createList())
        val manager = LinearLayoutManager(this)
        recycler_view.setHasFixedSize(true)
        recycler_view.layoutManager = manager
        recycler_view.adapter = mAdapter

        group_image.setOnClickListener {
            pickImg()
        }
        create_button.setOnClickListener {
            if (validateName()){
                getSelectedNumbers()
                if ( finalNumbers.size != 1 ){
                    createMap()
                    createGroup()
                    backToHome()
                }
            }
        }

    }

    private fun backToHome() {
        val intent = Intent(this, Home::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    private fun getSelectedNumbers() {
        for (model in groupNumbersModels) {
            if (model.isSelected) {
                finalNumbers.add(model.text)
            }
        }
    }

    private fun createList(): ArrayList<GroupNumbersModel> {
        for (i in phones!!){
            groupNumbersModels.add(
                GroupNumbersModel(
                    i
                )
            )
        }
        return groupNumbersModels
    }


    private fun createMap() {

        groupMap["name"] = group_name.text.toString()
        groupMap["description"] = group_description.text.toString()
        if (groupImgUri == null ){
            groupImgUri =
                Uri.parse("https://10play.com.au/ip/s3/2022/01/28/a9333564010931a07b777e8c32f2ce8c-1123582.png?image-profile=image_max&io=landscape")

        }
        groupMap["profileImageUrl"] = groupImgUri.toString()
        groupMap["members"] = finalNumbers
    }

    private fun createGroup(){
        val groupDocRef: String = phone + Timestamp(System.currentTimeMillis())
            .toString().replace("\\s".toRegex(), "")

        collectionReference.document(groupDocRef)
            .set(groupMap)
            .addOnSuccessListener { aVoid: Void? ->
                Toast.makeText(this, "successfully registered", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener { e: Exception? ->
                Toast.makeText(this, "not registered", Toast.LENGTH_LONG).show()
            }
        addGroupToConversations(groupDocRef)
    }

    private fun addGroupToConversations(groupDocRef :String) {
        for(i in finalNumbers){
            val docRef :DocumentReference = FirebaseFirestore.getInstance()
            .collection("users")
            .document(i)
            docRef.get().addOnCompleteListener { task: Task<DocumentSnapshot> ->
                if (task.isSuccessful) {
                    val doc = task.result
                    if (doc.exists()) {
                        val friends = doc["friends"] as java.util.ArrayList<String?>?
                        friends?.add(groupDocRef)
                        docRef.update("friends", friends)
                    } else {
                        Toast.makeText(
                            this,
                            "Error ",
                            Toast.LENGTH_SHORT
                        ).show()

                    }

                }

            }

        }

    }



    private fun validateName(): Boolean {
        val name: String = group_name.text.toString()
        return if (name.isEmpty()) {
            group_name.error = "Field cannot be empty"
            false
        } else if (name.length >= 30) {
            group_name.error = "Name too long"
            false
        } else {
            group_name.error = null
            true
        }
    }

    private fun pickImg() {
        val intent = Intent("android.intent.action.GET_CONTENT")
        intent.type = "image/*"
        this.startActivityForResult(intent, 3)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            groupImgUri = data.data
            group_image.setImageURI(groupImgUri)
        }
    }
}
