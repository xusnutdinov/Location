package com.example.location

import android.location.Location
import androidx.lifecycle.MutableLiveData

object LocationData {
    val location = MutableLiveData<Location>()
}