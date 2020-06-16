package com.example.tasbeeh

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class broadccast :BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
           // println("Service stopped")
            context?.startService(Intent(context, MyService::class.java))
    }

}