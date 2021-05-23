package com.example.location

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.LinearLayout
import android.widget.TextView
import com.example.location.Constants.DATABASE_NAME
import com.example.location.Constants.DATABASE_VERSION

class DbHelper(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {
    private val contentValues = ContentValues()

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("create TABLE data_table (" +
                "_id integer primary key," +
                "lon text," +
                "lat text" +
                ")")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("drop table if exists data_table")
        onCreate(db)
    }

    fun dbInsert(db: SQLiteDatabase?, lon: Double, lat: Double) {
        contentValues.put("lon", lon.toString())
        contentValues.put("lat", lat.toString())
        db?.insert("data_table", null, contentValues)
    }

    fun clearDB(db: SQLiteDatabase) {
        db.delete("data_table", null, null)
    }

    fun getData(db: SQLiteDatabase, output: LinearLayout?, context: Context) {
        output?.removeAllViews()
        val cursor = db.query("data_table", null,null,null,null,null,null,null)

        if (cursor.moveToFirst()) {
            val lonIndex = cursor.getColumnIndex("lon")
            val latIndex = cursor.getColumnIndex("lat")
            do {
                val newTextView = TextView(context)
                newTextView.text = "Lon: ${cursor.getString(lonIndex)}, Lat: ${cursor.getString(latIndex)}"
                output?.addView(newTextView, 0)
            } while (cursor.moveToNext())
        } else {
            val newTextView = TextView(context)
            newTextView.text = "В базе ничего нет"
            output?.addView(newTextView, 0)
        }
    }
}