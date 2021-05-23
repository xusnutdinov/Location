package com.example.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var btnStart: FloatingActionButton
    private lateinit var output: LinearLayout
    private lateinit var currentLocation: TextView
    private lateinit var btnGetFromDB: Button
    private var runService = false
    private val dbHelper = DbHelper(this)

    @SuppressLint("Recycle", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnStart = findViewById(R.id.startBtn)
        output = findViewById(R.id.textBar)
        currentLocation = findViewById(R.id.currentLocation)
        btnGetFromDB = findViewById(R.id.button)

        btnStart.setOnClickListener {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED){
                requestPermissions(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ),
                    0
                )
            } else {
                runService = !runService
                changeServiceState(runService)
            }
        }

        btnGetFromDB.setOnClickListener {
            val db : SQLiteDatabase = dbHelper.writableDatabase
            dbHelper.getData(db, output, this)
        }

        if (LocationService.running) {
            currentLocation.text = "Отслеживание начато"
            runService = true
        }
    }

    @SuppressLint("SetTextI18n")
    private fun changeServiceState(b: Boolean = false) {
        if(!LocationService.running || b) {
            val db: SQLiteDatabase = dbHelper.writableDatabase
            currentLocation.text = "Отслеживание начато"
            dbHelper.clearDB(db)
            sendCommand(Constants.START_LOCATION_SERVICE)
        } else {
            currentLocation.text = "Отслеживание прекращено"
            sendCommand(Constants.STOP_LOCATION_SERVICE)
        }
    }

    private fun sendCommand(command: String) {
        Intent(
            this, LocationService::class.java
        ).apply {
            action = command
            if (action == Constants.START_LOCATION_SERVICE) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(this)
                } else {
                    startService(this)
                }
            } else {
                stopService(this)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 0 && permissions.size > 1 &&
                permissions[1] == Manifest.permission.ACCESS_COARSE_LOCATION &&
                permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION &&
                (grantResults[0] == PackageManager.PERMISSION_GRANTED ||
                        grantResults[1] == PackageManager.PERMISSION_GRANTED
                        )) {
            changeServiceState(true)
        }
    }
}