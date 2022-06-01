package com.example.myapplication.models

class StoryModel() {
    var uri : String = ""
    var description: String? = null
    var date: String = "01/1/2020 12:00:00"

    constructor( uri: String , description: String? , date : String) : this(){
        this.uri = uri
        this.description = description
        this.date = date
    }
}