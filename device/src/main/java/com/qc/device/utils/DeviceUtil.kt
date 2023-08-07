package com.qc.device.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.qc.device.model.Device
import com.qc.device.model.Result
import com.qc.device.model.ResultError
import com.qc.device.utils.device.getBatter
import com.qc.device.utils.device.getCPU
import com.qc.device.utils.device.getDeviceInfo
import com.qc.device.utils.device.getFiles
import com.qc.device.utils.device.getLocale
import com.qc.device.utils.device.getNetwork
import com.qc.device.utils.device.getScreen
import com.qc.device.utils.device.getSensorList
import com.qc.device.utils.device.getSimList
import com.qc.device.utils.device.getSpace
import com.qc.device.utils.device.getWifi
import com.qc.device.utils.device.getWifiList
import java.util.Date

class DeviceUtil(val activity: ComponentActivity) {
    private var device: Device? = null
    private var onResult: ((Result<Device>) -> Unit)? = null
    private val permission =
        activity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            this.onResult?.invoke(Result(ResultError.RESULT_OK, null, device()))
            this.onResult = null
        }

    fun getDevice(onResult: (Result<Device>) -> Unit) {
        this.onResult = onResult
        val keys = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            keys.add(Manifest.permission.READ_MEDIA_IMAGES)
            keys.add(Manifest.permission.READ_MEDIA_AUDIO)
            keys.add(Manifest.permission.READ_MEDIA_VIDEO)
        }

        for (key in keys) {
            if (ContextCompat.checkSelfPermission(
                    activity,
                    key
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permission.launch(keys.toTypedArray())
                return
            }
        }

        this.onResult?.invoke(Result(ResultError.RESULT_OK, null, device()))
        this.onResult = null
    }

    private fun device(): Device {
        if (device != null) return device!!
        val simList = getSimList()
        device = Device(
            batter = getBatter(),
            cpu = getCPU(),
            createdAt = Date().time,
            device = getDeviceInfo().apply {
                simList.forEach { sim ->
                    if (imei.isBlank() && sim.imei.isNotBlank()) {
                        imei = sim.imei
                    }
                    if (imsi.isBlank() && sim.imei.isNotBlank()) {
                        imsi = sim.imsi
                    }
                    if (meid.isBlank() && sim.meid.isNotBlank()) {
                        meid = sim.meid
                    }
                }
            },
            file = getFiles(),
            isTable = false,
            locale = getLocale(),
            network = getNetwork(),
            regWifi = getWifi(),
            regWifiList = getWifiList(true),
            wifiList = getWifiList(false),
            screen = getScreen(),
            sensorList = getSensorList(),
            sim = simList,
            space = getSpace(),
        )
        return device!!
    }

    @SuppressLint("HardwareIds")
    fun getAndroidID(): String =
        try {
            Settings.Secure.getString(activity.contentResolver, Settings.Secure.ANDROID_ID) ?: ""
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
}