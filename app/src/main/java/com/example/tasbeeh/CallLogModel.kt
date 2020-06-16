package com.example.tasbeeh

public class CallLogModel(s: String?, s1: String?, s2: String?, s3: String?, s4: String?,s5:String?) {
    private var id = 0
    private var phoneNumber: String?=s
    private var callType: String? = s1
    private var callDate: String? = s2
    private var callDuration: String? = s3
    private var contactName: String? =s4
    private var username:String?=s5

    fun getId(): Int {
        return id
    }
    fun getPhoneNumber(): String? {
        return phoneNumber
    }

    fun getCallType(): String? {
        return callType
    }

    fun getCallDate(): String? {
        return callDate
    }

    fun getCallDuration(): String? {
        return callDuration
    }

    fun getContactName(): String? {
        return contactName
    }

    fun getUserName():String?{
        return username
    }
}