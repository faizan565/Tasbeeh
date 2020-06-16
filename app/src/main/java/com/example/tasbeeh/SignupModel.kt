package com.example.tasbeeh


class SignupModel(s: String?, s1: String?, s2: String?, s3: String?) {
    private var id = 0
    private var name: String? = s
    private var phone: String? = s1
    private var email: String? = s2
    private var password: String? = s3


    fun getId(): Int {
        return id
    }

    fun getName(): String? {
        return name
    }

    fun getPhone(): String? {
        return phone
    }

    fun getEmail(): String? {
        return email
    }

    fun getPassword(): String? {
        return password
    }


}