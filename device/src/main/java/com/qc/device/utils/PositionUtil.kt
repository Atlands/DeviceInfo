package com.qc.device.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.qc.device.model.Position
import com.qc.device.model.Result
import com.qc.device.model.ResultError
import java.util.Date

class PositionUtil(val activity: ComponentActivity) {
    private var mLocation: Location? = null
    private val manager by lazy {
        activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    private var onResult: ((Result<Position?>) -> Unit)? = null
    private val permission =
        activity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (it[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
                _getPosition()
            } else {
                error(ResultError.LOCATION_PERMISSION, "gps permission denied")
            }
        }

    fun getPosition(onResult: (Result<Position?>) -> Unit) {
        this.onResult = onResult
        if (ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            _getPosition()
        } else {
            val keys = arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            permission.launch(keys)
        }
    }

    private fun _getPosition() {
//        val isOpen = manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
//        if (!isOpen) {
//            error(ResultError.GPS_ENABLED, "GPS service not enabled")
//            return
//        }
        position(Manifest.permission.ACCESS_FINE_LOCATION, LocationManager.GPS_PROVIDER)
        if (this.onResult != null) {
            position(Manifest.permission.ACCESS_COARSE_LOCATION, LocationManager.NETWORK_PROVIDER)
        }
        success(null)
    }

    private fun success(position: Position?) {
        if (onResult == null) return
        this.onResult?.invoke(Result(ResultError.RESULT_OK, null, position))
        this.onResult = null
    }

    private fun error(code: Int, message: String) {
        if (onResult == null) return
        onResult?.invoke(
            Result(
                code,
                message,
                null
            )
        )
        onResult = null
    }


    private fun position(key: String, provider: String) {
        if (ContextCompat.checkSelfPermission(
                activity,
                key
            ) != PackageManager.PERMISSION_GRANTED
        ) return

        if (mLocation != null) {
            geocoder(mLocation!!)
        } else {
            try {
                val location = manager.getLastKnownLocation(provider)
                if (location == null) {
                    manager.requestLocationUpdates(provider, 0, 0F, locationListener)
                } else {
                    geocoder(location)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun geocoder(location: Location) {
        this.mLocation = location
        val position = Position(
            latitude = location.latitude,
            longitude = location.longitude
        )
        try {
            val geocoder = Geocoder(activity)
            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                val gLocation = addresses.first()
                position.apply {
                    address = gLocation.getAddressLine(0) ?: ""
                    geo_time = Date().formatDate()
                    gps_address_province = gLocation.adminArea ?: ""
                    gps_address_city = gLocation.locality ?: ""
                    gps_address_street = gLocation.subLocality ?: ""
                }
            }
        } catch (_: Exception) {
        }
        success(position)
    }


    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            try {
                mLocation = location
            } catch (_: Exception) {
            }
        }

        override fun onLocationChanged(locations: MutableList<Location>) {}

        override fun onFlushComplete(requestCode: Int) {}

        @Deprecated("Deprecated in Java")
        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        }

        override fun onProviderEnabled(provider: String) {}

        override fun onProviderDisabled(provider: String) {}
    }
}