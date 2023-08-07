package com.qc.device.utils.device

import android.Manifest
import android.annotation.SuppressLint
import android.app.LocaleManager
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.location.Location
import android.location.LocationManager
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.os.Build.getRadioVersion
import android.os.SystemClock
import android.provider.Settings
import android.telephony.TelephonyManager
import android.view.ViewConfiguration
import androidx.core.app.ActivityCompat
import androidx.core.app.LocaleManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.location.LocationListenerCompat
import androidx.core.location.LocationManagerCompat
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.qc.device.model.Device
import com.qc.device.utils.DeviceUtil
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import java.io.File


@SuppressLint("HardwareIds")
fun DeviceUtil.getDeviceInfo(): Device.DeviceInfo {
    return Device.DeviceInfo(
        name = activity.getUserDefinedDeviceName(),//只获取设备名，可编辑的
        brand = Build.BRAND,
        model = Build.MODEL,
        serial = Build.SERIAL,
        androidId = getAndroidID(),
        gaid = getGoogleId(activity),
        gsfid = getGSFID()?:"",
        buildId = Build.ID,
        buildNumber = Build.VERSION.SDK_INT,
        buildTime = Build.TIME,
        version = getSystemVersion(), //Build.DISPLAY, //只获取空格后面的部分
        macAddress = getMac(),
        isRooted = isRoot(),
        isSimulator = isEmulator(activity),
        isUSBDebug = isOpenUSBDebug(activity),
        isGpsFaked = activity.isMockGpsSync(), //false,//
        updateMills = SystemClock.uptimeMillis(),
        elapsedRealtime = SystemClock.elapsedRealtime(),
        lastBootTime = 0,//TODO
        baseBandVersion = getBBVersion(),
        kernelVersion = getKernelVersion(),
        physicalKeyboard = activity.hasPhysicalKeyboard(),
        keyboard = getKeyboard(),
        bluetoothCount = activity.getBluetoothCount().toLong(),
        bluetoothMac = getBluetoothMac(),
        radioVersion = getRadioVersion(),
        board = Build.BOARD,
        buildFingerprint = Build.FINGERPRINT,
        ringerMode = activity.getRingerMode().toLong(),
        isAirplane = activity.isAirplaneModeOn(),
        host = Build.HOST,
        manufacturerName = Build.MANUFACTURER
    )
}

fun Context.isMockGpsSync(): Boolean {
    var mockLoc = "0"
    try {
        mockLoc = Settings.Secure.getString(contentResolver, Settings.Secure.ALLOW_MOCK_LOCATION)
    } catch (_: Exception) {

    }
    return !mockLoc.equals("0");
}
fun isMockGps(context: Context) = callbackFlow<Boolean> {
    try {
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            trySend(false)
        }

        val listener = object : LocationListenerCompat {
            override fun onLocationChanged(location: Location) {
                val isFake = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    location.isMock()
                } else {
                    location.isFromMockProvider()
                }
                trySend(isFake)
                close()
            }
        }
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, listener)
        awaitClose {
            lm.removeUpdates(listener)
        }
    } catch (_: Exception) {
        trySend(false)
        close()
    }
}

fun Context.hasPhysicalKeyboard() :Boolean {
    return ViewConfiguration.get(this).hasPermanentMenuKey() || this.resources.configuration.keyboard != Configuration.KEYBOARD_NOKEYS
}

fun Context.getBluetoothCount() : Int {
    val adapter = (getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
    return if (ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.BLUETOOTH_CONNECT
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        return -1
    } else {
        adapter.bondedDevices.size
    }
}
fun Context.isAirplaneModeOn(): Boolean {
    return Settings.Global.getInt(contentResolver, Settings.Global.AIRPLANE_MODE_ON, 0) != 0
}
fun Context.getRingerMode(): Int {
    val am = getSystemService(Context.AUDIO_SERVICE) as AudioManager
    if(am.ringerMode == AudioManager.RINGER_MODE_NORMAL) {
        return AudioManager.RINGER_MODE_NORMAL
    }
    if (am.ringerMode == AudioManager.RINGER_MODE_VIBRATE) {
        return AudioManager.RINGER_MODE_VIBRATE
    }
    if (am.ringerMode == AudioManager.RINGER_MODE_SILENT) {
        return AudioManager.RINGER_MODE_SILENT
    }
    return -1
}

fun getSystemVersion(): String {
    return Build.DISPLAY.let {
        it.substring(it.indexOf(' ') + 1)
    }
}

fun DeviceUtil.getGSFID(): String? {
    val uri = Uri.parse("content://com.google.android.gsf.gservices")
    val params = arrayOf("android_id")
    val cursor = activity.contentResolver.query(uri, null, null, params, null) ?: return ""
    val id = if (!cursor.moveToFirst() || cursor.columnCount < 2) {
        ""
    }
    else {
        try {
            java.lang.Long.toHexString(cursor.getString(1).toLong())
        } catch (e: NumberFormatException) {
            e.printStackTrace()
            null
        }
    }
    cursor.close()
    return id
}

private fun isOpenUSBDebug(context: Context): Boolean = try {
    Settings.Secure.getInt(context.contentResolver, Settings.Secure.ADB_ENABLED, 0) > 0
} catch (e: Exception) {
    e.printStackTrace()
    false
}

/**
 * 判断是否模拟器
 */
@SuppressLint("HardwareIds")
private fun isEmulator(context: Context): Boolean = try {
    val url = "tel:" + "123456"
    val intent = Intent()
    intent.data = Uri.parse(url)
    intent.action = Intent.ACTION_DIAL
    // 是否可以处理跳转到拨号的 Intent
    val canCallPhone = intent.resolveActivity(context.packageManager) != null
    Build.FINGERPRINT.startsWith("generic") || Build.FINGERPRINT.lowercase()
        .contains("vbox") || Build.FINGERPRINT.lowercase()
        .contains("test-keys") || Build.MODEL.contains("google_sdk") || Build.MODEL.contains(
        "Emulator"
    ) || Build.MODEL
        .contains("MuMu") || Build.MODEL.contains("virtual") || Build.SERIAL.equals(
        "android",
        ignoreCase = true
    ) || Build.MANUFACTURER
        .contains("Genymotion") || Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith(
        "generic"
    ) || ("google_sdk"
            == Build.PRODUCT) || ((context
        .getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).networkOperatorName
        .lowercase()
            == "android") || !canCallPhone
} catch (e: Exception) {
    e.printStackTrace()
    false
}

/**
 * 是否root
 */
private fun isRoot(): Boolean {
    return try {
        !(!File("/system/bin/su").exists() &&
                !File("/system/xbin/su").exists())
    } catch (e: Exception) {
        false
    }
}

private fun getGoogleId(context: Context): String =
    try {
        val adInfo: AdvertisingIdClient.Info =
            AdvertisingIdClient.getAdvertisingIdInfo(context)
        adInfo.id ?: ""
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
        ""
    }

private fun Context.getUserDefinedDeviceName(): String {
    val f1 = {
        Settings.System.getString(contentResolver, "bluetooth_name")
    }
    val f2 = {
        Settings.Secure.getString(contentResolver, "bluetooth_name")
    }
    val f3 = {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            null
        } else {
            BluetoothAdapter.getDefaultAdapter().name
        }
    }
    val f4 = {
        Settings.System.getString(contentResolver, "device_name")
    }

    val f5 = {
        Settings.Secure.getString(contentResolver, "lock_screen_owner_info")
    }

    //按以下顺序依次尝试获取，因为不存在统一的获取方法，不保证能成功获取。这个顺序在多数设备上大概率能成功。
    return listOf(f2, f3, f1, f4, f5)
        .map { it.invoke() }
        .firstOrNull {
            it != null && it.isNotEmpty()
        }?:""
}