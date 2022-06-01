package com.example.myapplication.screens.status

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.datastructures.chatty.R
import com.example.myapplication.models.StoryModel
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_add_image.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.jvm.internal.Intrinsics

class AddImageStoryActivity : AppCompatActivity() {


    private lateinit var selectedImgUri : Uri
    val uid : String? by lazy {
        restorePrefTheme("phone")
    }
    private fun restorePrefTheme(key : String): String? {
        val pref = this.getSharedPreferences("mypref", AppCompatActivity.MODE_PRIVATE)
        return  pref.getString(key, null).toString()

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        ) //to hide title bar

        supportActionBar!!.hide() //to hide action bar


        setContentView(R.layout.activity_add_image)

        checkStoragePermission()
        pickImg()

        addImageStoryToFireBase.setOnClickListener {
            if (storyImage.drawable != null){
                hideKeyboard(this)
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
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101) {
            if (grantResults.isNotEmpty() && grantResults[0] == 0) {
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



    @SuppressLint("SimpleDateFormat")
    private fun sendToFireBase() {

        val storageRef = FirebaseStorage.getInstance().reference
        val realTimeDatabaseRef = Firebase.database.reference
        val fireStoreRef = FirebaseFirestore.getInstance()
        val randomKey = UUID.randomUUID().toString()
        val riverRef = storageRef.child("stories/$randomKey")

        riverRef.putFile(selectedImgUri)

            .addOnSuccessListener {

                riverRef.downloadUrl.addOnSuccessListener {uri ->
                    val sdf = SimpleDateFormat("dd/M/yyyy HH:mm:ss")
                    val currentDate = sdf.format(Date())

                    onBackPressed()


                    realTimeDatabaseRef.child("users").child(uid!!).child(randomKey).setValue(
                        StoryModel(uri.toString() ,Idescription.text.toString()  , currentDate) )
                        .addOnSuccessListener {
                            fireStoreRef.collection("users").document(uid!!).update("hasStory" , true)
                            fireStoreRef.collection("users").document(uid!!).update("lastStory" , currentDate)
                            fireStoreRef.collection("users").document(uid!!).update("storyUrl" , uri)

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

    private fun hideKeyboard(activity : Activity) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

}
