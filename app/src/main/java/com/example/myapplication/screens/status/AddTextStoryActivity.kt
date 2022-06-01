package com.example.myapplication.screens.status

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.datastructures.chatty.R
import com.example.myapplication.models.StoryModel
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_add_text_story.*
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

class AddTextStoryActivity : AppCompatActivity() {

    private lateinit var selectedImgUri: Uri

    val uid : String? by lazy {
        restorePrefTheme("phone")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        ) //to hide title bar

        supportActionBar!!.hide() //to hide action bar


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_text_story)

        Log.d("Text" , uid!!)
        addTextStoryToFireBase.setOnClickListener {
            if (descriptionText.text.isNotEmpty()) {
                sendToFireBase()

            } else {
                Toast.makeText(
                    this,
                    "PLease write a description first",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        descriptionText.addTextChangedListener {
            if (descriptionText.length() >= 700){
                Toast.makeText(this , "Maximum length is 700 letter" , Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun takeScreenshot(view: View): Bitmap {
        view.isDrawingCacheEnabled = true
        view.buildDrawingCache(true)
        addTextStoryToFireBase.visibility = View.INVISIBLE
        val b = Bitmap.createBitmap(view.drawingCache)
        view.isDrawingCacheEnabled = false
        return b
    }

    private fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path =
            MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }

    private fun restorePrefTheme(key : String): String? {
        val pref = this.getSharedPreferences("mypref", AppCompatActivity.MODE_PRIVATE)
        return  pref.getString(key, null).toString()

    }


    private fun sendToFireBase() {
        hideKeyboard(this)
        val bitmap: Bitmap = takeScreenshot(main)

        selectedImgUri = getImageUri(this, bitmap)!!
        val storageRef = FirebaseStorage.getInstance().reference
        val fireStoreRef = FirebaseFirestore.getInstance()
        val randomKey = UUID.randomUUID().toString()
        val riverRef = storageRef.child("stories/$randomKey")
        val realTimeDatabaseRef = Firebase.database.reference

        riverRef.putFile(selectedImgUri)
            .addOnSuccessListener {

                riverRef.downloadUrl.addOnSuccessListener {uri ->

                    val sdf = SimpleDateFormat("dd/M/yyyy HH:mm:ss")
                    val currentDate = sdf.format(Date())

                    onBackPressed()

                    realTimeDatabaseRef.child("users").child(uid!!).child(randomKey).setValue(StoryModel(uri.toString(), null,  currentDate))
                        .addOnSuccessListener {
                            fireStoreRef.collection("users").document(uid!!).update("hasStory" , true)
                            fireStoreRef.collection("users").document(uid!!).update("lastStory" , currentDate)
                            fireStoreRef.collection("users").document(uid!!).update("storyUrl" , uri)
                        }
                        .addOnFailureListener {
                            Toast.makeText(
                                this,
                                it.message.toString(),
                                Toast.LENGTH_LONG
                            ).show()

                        }


                }
                    .addOnFailureListener {
                        Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                    }




            }

            .addOnFailureListener {
                Toast.makeText(this , it.message , Toast.LENGTH_LONG).show()
            }

            .addOnProgressListener {

            }




    }

    private fun hideKeyboard(activity : Activity) {
        var imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

}