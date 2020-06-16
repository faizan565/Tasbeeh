package com.example.tasbeeh
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.annotation.Nullable


class SqliteSmsLog

    (@Nullable context: Context?) :
    SQLiteOpenHelper(context, SqliteCallLog.DATABASE_NAME, null, 1) {
    override fun onCreate(sqLiteDatabase: SQLiteDatabase) { //sqLiteDatabase=getWritableDatabase();
//db=getWritableDatabase();
//sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        val CREATE_TABLE_CallLog =
            "CREATE TABLE IF NOT EXISTS ${SqliteSmsLog.TABLE_NAME} (${SqliteSmsLog.SMSLOG_number} VARCHAR, ${SqliteSmsLog.SMSLOG_body} VARCHAR, ${SqliteSmsLog.SMSLOG_date} VARCHAR,${SqliteSmsLog.SMSLOG_name} VARCHAR,${SqliteSmsLog.SMSLOG_username} VARCHAR)"
        sqLiteDatabase.execSQL(CREATE_TABLE_CallLog)
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, i: Int, i1: Int) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS ${SqliteSmsLog.TABLE_NAME}")
        onCreate(sqLiteDatabase)
    }

    companion object {
        var DATABASE_NAME = "guardianGrab"
        const val TABLE_NAME = "smsLog"
        const val SMSLOG_number = "number"
        const val SMSLOG_body = "body"
        const val SMSLOG_date = "date"
        const val SMSLOG_name = "name"
        const val SMSLOG_username="username"
    }
}



