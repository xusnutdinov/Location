package com.example.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat

object LocationHelper : LocationListener {

    private var locationManager: LocationManager? = null
    private var locationUpdater: ((Location) -> Unit?)? = null

    // Изменение координат устройства
    override fun onLocationChanged(location: Location) {
        locationUpdater?.invoke(location)
    }

    // Отслеживание координат
    fun startLocating(context: Context, locationUpdateListener: (Location)->Unit) {
        locationUpdater = locationUpdateListener
        locationManager = (context.getSystemService(Context.LOCATION_SERVICE) as LocationManager).also {
            locationManager ->
            if (ActivityCompat.checkSelfPermission(
                            context,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(
                            context,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    10L,
                    0.6F,
                    this
            )
        }
    }

    fun stopLocating() {
        locationManager?.removeUpdates(this)
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
    }

    override fun onProviderEnabled(provider: String) {
    }

    override fun onProviderDisabled(provider: String) {
    }
}