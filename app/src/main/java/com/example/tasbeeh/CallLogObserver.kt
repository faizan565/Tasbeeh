package com.example.tasbeeh
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler

private var times: Int=0

class CallLogObserver(handler: Handler?) : ContentObserver(handler) {
    override fun deliverSelfNotifications(): Boolean {
        return true
    }

    override fun onChange(selfChange: Boolean) {
     //   super.onChange(selfChange)
    }


    override fun onChange(selfChange: Boolean, uri: Uri) {
        //   super.onChange(true, CallLog.Calls.CONTENT_URI);

        // println("Onchange")

        //println("Call Log VISIT " + MainActivity.times)
        //  MainActivity.times++
        val main = MainActivity()

        val sqliteMostRecentCallLog: CallLogModel;
        val c = main.getMostRecentCallLog(MainActivity?.contentResolver,MainActivity?.contextMe)
        val sqliteCallLog = MainActivity.getLastCallLogFromSqlite(MainActivity.contentResolver,MainActivity?.contextMe);
        if (sqliteCallLog != null)
        {
            if (sqliteCallLog?.getCallDate().equals(c?.getCallDate()))
            {

            }
            else
            {
                if (c != null) {
                    main.insertDataInSqlLite(c)
                }

            }

        }
        else if(sqliteCallLog==null)
        {
            if (c != null) {
                main.insertDataInSqlLite(c)
            }
        }

    }
}