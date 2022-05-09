package com.datastructures.chatty.screens.status

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.datastructures.chatty.R
import com.datastructures.chatty.models.StoryModel
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_add_image.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.jvm.internal.Intrinsics


class AddImageActivity : AppCompatActivity() {


    lateinit var selectedImgUri : Uri
    var uid = "01017046725"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_image)

        checkStoragePermission()
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

    private fun checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == -1
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                101
            )
        } else {
            Toast.makeText(this, "Permission already granted", Toast.LENGTH_LONG).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        Intrinsics.checkNotNullParameter(permissions, "permissions")
        Intrinsics.checkNotNullParameter(grantResults, "grantResults")
        super.onRequestPermissionsResult(requestCode, permissions!!, grantResults)
        if (requestCode == 101) {
            if (grantResults.size != 0 && grantResults[0] == 0) {
                Toast.makeText(this, "Storage Permission Granted", Toast.LENGTH_LONG).show()
            } else {
                onBackPressed()
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