package com.example.tasbeeh

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.annotation.Nullable


class SqliteCallLog  //SQLiteDatabase db;
    (@Nullable context: Context?) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, 1)
{
    override fun onCreate(sqLiteDatabase: SQLiteDatabase)
    { //sqLiteDatabase=getWritableDatabase();
//db=getWritableDatabase();
//sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        val CREATE_TABLE_CallLog =
            "CREATE TABLE IF NOT EXISTS $TABLE_NAME ($CallLog_number VARCHAR, $CallLog_type VARCHAR, $CallLog_date VARCHAR,$CallLog_duration VARCHAR,$CallLog_name VARCHAR,$CallLog_username VARCHAR)"
        sqLiteDatabase.execSQL(CREATE_TABLE_CallLog)
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, i: Int, i1: Int) {
    sqLiteDatabase.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
    onCreate(sqLiteDatabase)
}

    companion object {
        var DATABASE_NAME = "guardianGrab"
        const val TABLE_NAME = "callLog"
        const val CallLog_number = "phoneNumber"
        const val CallLog_type = "callType"
        const val CallLog_date = "callDate"
        const val CallLog_duration = "callDuration"
        const val CallLog_name = "contactName"
        const val CallLog_username = "username"
    }
}
