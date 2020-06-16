package com.example.tasbeeh
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.annotation.Nullable

class SqliteLocation
    (@Nullable context: Context?) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, 1)
    {
        override fun onCreate(sqLiteDatabase: SQLiteDatabase)
        { //sqLiteDatabase=getWritableDatabase();
//db=getWritableDatabase();
//sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
            val CREATE_TABLE_location =
                "CREATE TABLE IF NOT EXISTS $TABLE_NAME ($latitude VARCHAR,$longitude VARCHAR,$last_address VARCHAR,$date VARCHAR,$username VARCHAR,)"
            sqLiteDatabase.execSQL(CREATE_TABLE_location)
        }

        override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, i: Int, i1: Int) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
            onCreate(sqLiteDatabase)
        }

        companion object {
            var DATABASE_NAME = "guardianGrab"
            const val TABLE_NAME = "location"
            const val latitude = "latitude"
            const val longitude = "longitude"
            const val last_address = "last_address"
            const val date = "date"
            const val username = "username"
        }


}