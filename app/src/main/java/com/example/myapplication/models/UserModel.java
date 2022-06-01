package com.example.myapplication.models;

import java.util.ArrayList;

import omari.hamza.storyview.model.MyStory;
public class UserModel  {
    private String name;
    private String imageUri;
    private String phone;
    private ArrayList<String> friends;
    private ArrayList<MyStory> stories;
    private String lastStory;
    private boolean hasStory;


    public UserModel( ArrayList<MyStory> stories,  String name,
                      String lastStory, boolean hasStory,
                      ArrayList<String> friends,
                      String storyUrl,
                      String imageUri) {
        this.friends = friends;
        this.stories = stories;
        this.name = name;
        this.lastStory = lastStory;
        this.hasStory = hasStory;
        this.storyUrl=storyUrl;
        this.imageUri = imageUri;
    }

    public ArrayList<MyStory> getStories() {
        return stories;
    }

    public void setStories(ArrayList<MyStory> stories) {
        this.stories = stories;
    }


    public ArrayList<String> getFriends() {
        return friends;
    }

    public void setFriends(ArrayList<String> friends) {
        this.friends = friends;
    }

    public String getLastStory() {
        return lastStory;
    }

    public void setLastStory(String lastStory) {
        this.lastStory = lastStory;
    }

    public boolean isHasStory() {
        return hasStory;
    }

    public void setHasStory(boolean hasStory) {
        this.hasStory = hasStory;
    }

    public String getStoryUrl() {
        return storyUrl;
    }

    public void setStoryUrl(String storyUrl) {
        this.storyUrl = storyUrl;
    }

    private String storyUrl;
    public UserModel() {
    }


    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String uri) {
        this.imageUri = uri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    private String msg;

    public String getMsg() {
        return msg;
    }

    public UserModel(String name, String msg, String uri, String phone) {
        this.name = name;
        this.msg = msg;
        this.imageUri=uri;
        this.phone = phone;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
