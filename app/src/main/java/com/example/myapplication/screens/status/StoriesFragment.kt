package com.example.myapplication.screens.status
import Statue_Adapter
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.datastructures.chatty.R
import com.example.myapplication.models.StoryModel
import com.example.myapplication.models.UserModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_stories.*
import omari.hamza.storyview.StoryView
import omari.hamza.storyview.model.MyStory
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class StoriesFragment : Fragment(R.layout.fragment_stories) {


    val uid : String? by lazy {
        restorePrefTheme("phone")
    }
    private fun restorePrefTheme(key : String): String? {
        val pref = requireContext().getSharedPreferences("mypref", AppCompatActivity.MODE_PRIVATE)
        return  pref.getString(key, null).toString()

    }
    private val fireStoreRef by lazy {
        FirebaseFirestore.getInstance()
    }

    private val databaseRef by lazy {
        Firebase.database.reference
    }

    //    var uid = "01017046725"


    val storyOfFriends : ArrayList<UserModel> by lazy {
        ArrayList()
    }

    lateinit var myUser : UserModel

    val adapter = Statue_Adapter()


    @SuppressLint("RestrictedApi")
    override fun onResume() {
        super.onResume()
        fireStoreRef.collection("users").document(uid!!).get()
            .addOnSuccessListener {

    val simpleDateFormat = SimpleDateFormat("dd/M/yyyy HH:mm:ss")
    var friends = it.get("friends") as ArrayList<String>

    if (friends.size != 0) {
        for (friend in friends) {

    fireStoreRef.collection("users").document(friend).get()
        .addOnSuccessListener {
                itFriend->


    if (itFriend.getBoolean("hasStory") == true) { //

        databaseRef.child("users").child(friend).orderByChild("date")
            .addValueEventListener(object : ValueEventListener {

                override fun onDataChange(dataSnapShot: DataSnapshot) {
                    storyOfFriends.clear()

                    if (!dataSnapShot.exists()){
                        itFriend.reference.update("hasStory" , false)
                    }else {
                        val stories: ArrayList<MyStory> = ArrayList()
                        var lastStory = StoryModel("", "", "")

                        for (snapShot in dataSnapShot.children) {

                            val story = snapShot.getValue<StoryModel>()
                            lastStory = story!!

                            val currentDate = simpleDateFormat.format(Date())

                            val diff = (simpleDateFormat.parse(currentDate).time - simpleDateFormat.parse(story!!.date).time)

                            val seconds = diff / 1000
                            val minutes = seconds / 60
                            val hours = minutes / 60
                            if (hours >= 24) {
                                snapShot.ref.removeValue()
                                continue
                            } else {
                                stories.add(
                                    MyStory(
                                        story.uri,
                                        simpleDateFormat.parse(
                                            story.date
                                        ),
                                        story.description
                                    )
                                )
                            }


                        }




                        val currentDate = simpleDateFormat.format(Date())

                        val diff = (simpleDateFormat.parse(currentDate)!!.time - simpleDateFormat.parse(lastStory.date)!!.time)
                        val seconds = diff / 1000
                        val minutes = seconds / 60
                        val hours = minutes / 60
                        var lastStoryTime = ""




                        if (hours == 0L && minutes == 0L) {
                            lastStoryTime = "now"
                        } else if (hours == 0L) {
                            lastStoryTime = "${(minutes % 60)} minutes ago"
                        } else {
                            if (currentDate.substring(0, 2) == lastStory.date.substring(0, 2)) {
                                lastStoryTime = "Today   "
                            } else {
                                lastStoryTime = "YesterDay   "
                            }

                            if (lastStory.date.substring(10, 12).toInt() > 12) {

                                lastStoryTime = "$lastStoryTime${(lastStory.date.substring(10, 12).toInt() - 12)}:${lastStory.date.substring(13, 15)} PM"

                            } else if (lastStory.date.substring(10, 12).toInt() < 12) {
                                lastStoryTime = "$lastStoryTime${(lastStory.date.substring(10, 12))}:${lastStory.date.substring(13, 15)} AM"
                            }

                        }

                        lastStoryTime.replace("-" , "")

                        storyOfFriends.add(
                            UserModel(
                                stories,
                                itFriend.get("name").toString(),
                                lastStoryTime,
                                itFriend.getBoolean("hasStory")!!,
                                itFriend.get("friends") as ArrayList<String>,
                                itFriend.get("storyUrl").toString(),
                                itFriend.get("profileImageUrl").toString()
                            )
                        )



                    }
                    adapter.submitList(storyOfFriends)
                    rvStatus.adapter = adapter

                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        requireContext(),
                        error.message,
                        Toast.LENGTH_LONG
                    ).show()
                }

            })


            }
        }

        .addOnFailureListener {
            Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG)
                .show()
        }

    }
    }



    if (it.getBoolean("hasStory") == true){

        databaseRef.child("users").child(uid!!).orderByChild("date").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(dataSnapShot: DataSnapshot) {

                if (!dataSnapShot.exists()){
                    it.reference.update("hasStory" , false)
                } else {


                    val myStories: ArrayList<MyStory> = ArrayList<MyStory>()

                    var lastStory = StoryModel("", "", "")
                    for (snapShot in dataSnapShot.children) {
                        val story = snapShot.getValue<StoryModel>()

                        lastStory = story!!
                        val currentDate = simpleDateFormat.format(Date())
                        val diff = (simpleDateFormat.parse(currentDate)!!.time - simpleDateFormat.parse(story.date)!!.time)
                        val seconds = diff / 1000
                        val minutes = seconds / 60
                        val hours = minutes / 60

                        if (hours >= 24) {
                            snapShot.ref.removeValue()
                            continue
                        } else {
                            myStories.add(
                                MyStory(
                                    story.uri,
                                    simpleDateFormat.parse(story.date),
                                    story.description
                                )
                            )

                        }

                        if (myStories.size > 0 ){
                            showMyStory()
                        }

                    }




                    if (it.getBoolean("hasStory") == true) {


                        val currentDate = simpleDateFormat.format(Date())
                        val diff = (simpleDateFormat.parse(currentDate)!!.time - simpleDateFormat.parse(lastStory.date)!!.time)
                        val seconds = diff / 1000
                        val minutes = seconds / 60
                        val hours = minutes / 60
                        var lastStoryTime = ""

                        if (currentDate.substring(0, 2) == lastStory.date.substring(0, 2)) {
                            day.text = "Today"
                        } else {
                            day.text = "Yesterday"
                        }

                        if (hours == 0L && minutes == 0L) {

                            lastStoryTime = "now"
                            day.visibility = View.GONE

                        } else if (hours == 0L) {

                            day.visibility = View.GONE
                            lastStoryTime = "${(minutes % 60)} minutes ago"
                        } else {

                            day.visibility = View.VISIBLE


                        if (lastStory.date.substring(10, 12).toInt() > 12) {

                            lastStoryTime =
                                "${lastStory.date.substring(10, 12).toInt() - 12} : ${lastStory.date.substring(13, 15)} PM"

                        } else if (lastStory.date.substring(10, 12).toInt() < 12) {
                            lastStoryTime = "${(lastStory.date.substring(10, 12))}:${lastStory.date.substring(13, 15)} AM"
                        }
                        }

                        lastStoryTime.replace("-" , "")

                        myUser = UserModel(
                            myStories,
                            it.get("name").toString(),
                            lastStoryTime,
                            it.getBoolean("hasStory") as Boolean,
                            friends,
                            it.get("storyUrl").toString(),
                            it.get("profileImageUrl").toString()
                        )
                    } else {
                        myUser = UserModel(
                            myStories,
                            it.get("name").toString(),
                            "",
                            it.getBoolean("hasStory") as Boolean,
                            friends,
                            it.get("storyUrl").toString(),
                            it.get("profileImageUrl").toString()
                        )
                    }



                    myLastStory.text = myUser.lastStory
                    myStoryTitle.text = myUser.name
                    Glide.with(requireContext()).load(myUser.storyUrl).into(myStoryImg)


                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext() , error.message , Toast.LENGTH_LONG).show()
                }


            })



        } else {
            myUser = UserModel(null, "", "", false, null, null, null)

            hideMyStory()
        }
    }
    .addOnFailureListener {
        Toast.makeText(requireContext() , it.message , Toast.LENGTH_LONG).show()
    }

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        myStoryImg.setOnClickListener {
            myLastStory.text = myUser.lastStory
            StoryView.Builder(parentFragmentManager)
                .setStoriesList(myUser.stories)
                .setStoryDuration(5000)
                .setTitleText(myUser.name)
                .setTitleLogoUrl(myUser.imageUri)
                .setStartingIndex(0)
                .setOnStoryChangedCallback { position ->

                }.build()
                .show()

            onResume()


        }

        rvStatus.layoutManager = LinearLayoutManager(requireContext())




        adapter.setOnItemClickListner {
            myLastStory.text = myUser.lastStory

            StoryView.Builder(parentFragmentManager)
                .setStoriesList(it.stories)
                .setStoryDuration(5000)
                .setTitleText(it.name)
                .setTitleLogoUrl(it.imageUri)
                .setStartingIndex(0)
                .build()
                .show()

        }



        addTextStory.setOnClickListener {

            addStory.collapseImmediately()
            val intent : Intent = Intent(this.activity, AddTextStoryActivity::class.java)
            intent.putExtra("phone" , uid)
            startActivity(intent)
        }

        addImagedStory.setOnClickListener {

            addStory.collapseImmediately()
            val intent : Intent = Intent(this.activity, AddImageStoryActivity::class.java)
            intent.putExtra("phone" , uid)
            startActivity(intent)

        }
    }



    fun hideMyStory(){
        myLastStory.visibility = View.GONE
        myStoryImg.visibility = View.GONE
        myStoryTitle.visibility = View.GONE
        lineView.visibility = View.GONE
        day.visibility = View.GONE
    }




    fun showMyStory(){
        myLastStory.visibility = View.VISIBLE
        myStoryImg.visibility = View.VISIBLE
        myStoryTitle.visibility = View.VISIBLE
        lineView.visibility = View.VISIBLE
        day.visibility = View.VISIBLE
    }
}
