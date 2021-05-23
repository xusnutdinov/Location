package com.example.location

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.location.Location
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService

class LocationService : LifecycleService() {

    companion object {
        var running = false
    }
    private val dbHelper = DbHelper(this)

    private fun locationChanged(l: Location) {
        LocationData.location.postValue(l)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.apply {
            when(action) {
                Constants.START_LOCATION_SERVICE -> {
                    val db: SQLiteDatabase = dbHelper.writableDatabase
                    LocationData.location.observe(this@LocationService){
                        dbHelper.dbInsert(db, it.longitude, it.latitude)
                    }
                    startLocationService(intent)
                }
                Constants.STOP_LOCATION_SERVICE -> {
                    LocationData.location.removeObservers(this@LocationService)
                    stopLocationService(intent)
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun stopLocationService(intent: Intent) {
        running = false
        LocationHelper.stopLocating()
        stopService(intent)
    }

    private fun startLocationService(intent: Intent) {
        running = true
        LocationHelper.startLocating(applicationContext, ::locationChanged)
        val channelID = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        } else ""
        val notificationBuilder = NotificationCompat.Builder(
                this, channelID
        )
        val notification = notificationBuilder
                .setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setContentText("Служба по отслеживанию запущена")
                .build()
        startForeground(Constants.SERVICE_ID, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(): String {
        val channelName = "Location Service Channel"
        val channel = NotificationChannel(
                Constants.SERVICE_CHANNEL_ID,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
        )
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(channel)
        return Constants.SERVICE_CHANNEL_ID
    }
}