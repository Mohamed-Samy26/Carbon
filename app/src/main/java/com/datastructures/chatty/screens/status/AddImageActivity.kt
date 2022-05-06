package com.datastructures.chatty.screens.status

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.datastructures.chatty.R
import com.datastructures.chatty.models.StoryModel
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_add_image.*
import java.text.SimpleDateFormat
import java.util.*

class AddImageActivity : AppCompatActivity() {


    lateinit var selectedImgUri : Uri
    var uid = "01017046725"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_image)

        pickImg()

        addImageStoryToFireBase.setOnClickListener {
            if (storyImage.drawable != null){
                sendToFireBase()
            } else {
                Toast.makeText(this , "Please Pick A Image" , Toast.LENGTH_LONG).show()

                pickImg()
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && data != null){
            selectedImgUri = data.data!!
            storyImage.setImageURI(selectedImgUri)
        }
    }

    fun pickImg(){
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent , 3)
    }



    private fun sendToFireBase() {

        val storageRef = FirebaseStorage.getInstance().reference
        val realTimeDatabaseRef = Firebase.database.reference
        val fireStoreRef = FirebaseFirestore.getInstance()
        val randomKey = UUID.randomUUID().toString()
        val riverRef = storageRef.child("stories/$randomKey")

        riverRef.putFile(selectedImgUri)

            .addOnSuccessListener {

                riverRef.downloadUrl.addOnSuccessListener {
                    val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                    val currentDate = sdf.format(Date())

                    onBackPressed()


                    realTimeDatabaseRef.child("users").child(uid).child(randomKey).setValue(
                        StoryModel(it.toString() ,Idescription.text.toString()  , currentDate) )
                        .addOnSuccessListener {
                            fireStoreRef.collection("users").document(uid).update("hasStory" , true)
                            fireStoreRef.collection("users").document(uid).update("lastStory" , currentDate)

                        }
                        .addOnFailureListener {
                            Toast.makeText(this , it.message , Toast.LENGTH_LONG).show()
                        }
                }

            }

            .addOnFailureListener{
                Toast.makeText(this, it.message , Toast.LENGTH_LONG).show()
                onBackPressed()

            }
            .addOnProgressListener {

            }
    }

}