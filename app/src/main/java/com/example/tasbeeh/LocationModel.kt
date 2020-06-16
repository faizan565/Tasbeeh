package com.example.tasbeeh
public class LocationModel(s: String?, s1: String?, s2: String?, s3: String?,s4:String?) {
    private var id = 0
    private var latitude: String? = s
    private var longitude: String? = s1
    private var area: String? = s2
    private var date: String? = s3
    private var username:String?=s4



    fun getId(): Int {
        return id
    }
    fun getLatitude(): String? {
        return latitude
    }

    fun getLongitude(): String? {
        return longitude
    }

    fun getArea(): String? {
        return area
    }

    fun getDate(): String? {
        return date
    }
    fun getUsername():String?{
        return username
    }

}