package com.example.tasbeeh
public class SmsLogModel(s: String?, s1: String?, s2: String?, s3: String?,s4:String?) {
    private var id=0
    private var number: String? = s
    private var body: String? = s1
    private var date: String? = s2
    private var name: String? = s3
    private var username:String?=s4

    fun getId(): Int {
        return id
    }
    fun getPhoneNumber(): String? {
        return number
    }

    fun getBody(): String? {
        return body
    }

    fun getSmsDate(): String? {
        return date
    }

    fun getContactName(): String? {
        return name
    }

    fun getUserName():String?{
        return username
    }
}