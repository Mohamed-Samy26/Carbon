package com.datastructures.chatty.models;

public class UserModel {
    private String name;
    private String imageUri;
    private String phone;

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
