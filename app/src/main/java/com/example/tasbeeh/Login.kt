package com.example.tasbeeh

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Login : AppCompatActivity() {
    private var jsonPlaceHolderApi:JsonPlaceHolderAPI?=null
    public var list=ArrayList<SignupModel>()
    public lateinit var sp: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        sp=getSharedPreferences("login", Context.MODE_PRIVATE)
        getSignUpDataFromApi()
        var signIn = findViewById<Button>(R.id.signin)
        var editTextPhone=findViewById<EditText>(R.id.loginPhone)

        var editTextPassword=findViewById<EditText>(R.id.loginPassword)

        if (sp.getBoolean("logged", false)) {
            var intent= Intent(this,MainActivity::class.java)
            startActivity(intent)
        }
        else {


            signIn.setOnClickListener {

                //print(list?.size)
                var phone: String = editTextPhone.text.toString()
                var password = editTextPassword.text.toString()
                var flag = false;
                for (l in list) {
                    if (phone.equals(l?.getPhone()) && password.equals(l?.getPassword())) {
                        sp.edit().putBoolean("logged", true).apply()
                        sp.edit().putString("username", phone).apply()
                        val intent = Intent(applicationContext, MainActivity::class.java)
                        startActivity(intent)
                        Toast.makeText(this, "Login Successfully", Toast.LENGTH_SHORT).show()

                        flag = true
                    }

                }
                if (flag == false) {
                    Toast.makeText(this, "Incorrect Username And Password", Toast.LENGTH_SHORT)
                            .show()
                }


            }
        }


    }

    fun getSignUpDataFromApi() {
        val builder = Retrofit.Builder()
        builder.baseUrl("https://guardiangrab.lifeklix.com/")
        builder.addConverterFactory(GsonConverterFactory.create())
        val retrofit = builder
                .build()
        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderAPI::class.java)


        val call = jsonPlaceHolderApi!!.getSignUpFromApi()
        call!!.enqueue(object : Callback<List<SignupModel?>?> {



            override fun onResponse(call: Call<List<SignupModel?>?>, response: Response<List<SignupModel?>?>) {
                if (!response.isSuccessful) {

                }
                val callData = response.body()!!
                for (post in callData)
                {
                    var name= post!!.getName()
                    var phone=post!!.getPhone()
                    var email=post!!.getEmail()
                    var password=post!!.getPassword()
                    var signupModel=SignupModel(name,phone,email,password)
                    list?.add(signupModel)
                    print(list?.size)
                }

            }
            override fun onFailure(call: Call<List<SignupModel?>?>, t: Throwable) {

            }
        })


    }



}
