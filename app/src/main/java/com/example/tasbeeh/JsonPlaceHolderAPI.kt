package com.example.tasbeeh

import android.provider.Telephony
import retrofit2.Call

import retrofit2.http.Body

import retrofit2.http.GET

import retrofit2.http.POST

interface JsonPlaceHolderAPI {

    @GET("getCalls")  //getting data while getCalls is appended in url
    fun  getPost(): Call<List<CallLogModel?>?>? //retrofit will auto generate necessary code of this method

    @POST("CallLog")
    fun createPost(@Body callDetails: CallLogModel?): Call<CallLogModel?>? //List<Post>

    @GET("getSms")
    fun  getSmsDetails():Call<List<SmsLogModel?>?>?

    @POST("SmsLog")
    fun sendSmsDetails(@Body smsDetails: SmsLogModel?): Call<SmsLogModel?>?

    @POST("sendLocation")
    fun sendLocationDetailsIntoApi(@Body locationDetails:LocationModel?):Call<LocationModel?>?

    @GET("getLocation")
    fun getLocationFromApi():Call<List<LocationModel?>?>?
    @POST("sendSignUp")
    fun sendSignUpDetailsIntoApi(@Body signUpModel:SignupModel?):Call<SignupModel?>?

    @GET("getSignUp")
    fun getSignUpFromApi():Call<List<SignupModel?>?>?




}