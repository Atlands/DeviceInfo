package com.qc.device.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
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
                error()
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

    private fun _getPosition(){
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

    private fun error() {
        if (onResult == null) return
        onResult?.invoke(
            Result(
                ResultError.LOCATION_PERMISSION,
                "gps permission denied",
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
        if (!manager.isProviderEnabled(provider)) return

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


    private val locationListener = LocationListener { location ->
        try {
            mLocation = location
        } catch (_: Exception) {
        }
    }
}