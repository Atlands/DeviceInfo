package com.qc.device.utils.device

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Proxy
import androidx.core.content.ContextCompat
import com.qc.device.model.Device
import com.qc.device.utils.DeviceUtil
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

fun DeviceUtil.getNetwork(): Device.Network {
    val networkInfo = getNetworkInfo()
    return Device.Network(
        dns = getLocalDNS() ?: "",
        networkType = networkInfo?.type ?: 0,
        networkSubType = networkInfo?.subtype ?: 0,
        networkName = networkInfo?.subtypeName ?: "",
        phoneType = telephonyManager?.phoneType ?: 0,
        isUsingVPN = networkInfo?.type == ConnectivityManager.TYPE_VPN,
        vpnAddress = Proxy.getDefaultHost(),
        httpProxyPort = httpProxyPort(),
        isUsingProxyPort = httpProxyPort() != -1,
        networkOperatorName = telephonyManager?.networkOperatorName ?: "",
        simCount = telephonyManager?.phoneCount ?: 0
    )
}

fun httpProxyPort(): Int =
    try {
        Proxy.getDefaultPort()
    } catch (e: Exception) {
        -1
    }

private fun DeviceUtil.getNetworkInfo(): NetworkInfo? {
    if (ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.ACCESS_NETWORK_STATE
        ) != PackageManager.PERMISSION_GRANTED
    ) return null
    val connectivityManager =
        activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
            ?: return null
    return connectivityManager.activeNetworkInfo
}


//fun DeviceUtil.getNetworkType(): String? {
//
//    if (ContextCompat.checkSelfPermission(
//            activity,
//            Manifest.permission.ACCESS_NETWORK_STATE
//        ) != PackageManager.PERMISSION_GRANTED
//    ) {
//        return "NETWORK_NO"
//    }
//    if (isEthernet()) {
//        return "NETWORK_ETHERNET"
//    }
//
//    val connectivityManager =
//        activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
//            ?: return null
//    val info = connectivityManager.activeNetworkInfo
//    return if (info != null && info.isAvailable) {
//        when (info.type) {
//            ConnectivityManager.TYPE_WIFI -> {
//                "NETWORK_WIFI"
//            }
//
//            ConnectivityManager.TYPE_MOBILE -> {
//                when (info.subtype) {
//                    TelephonyManager.NETWORK_TYPE_GSM, TelephonyManager.NETWORK_TYPE_GPRS, TelephonyManager.NETWORK_TYPE_CDMA, TelephonyManager.NETWORK_TYPE_EDGE, TelephonyManager.NETWORK_TYPE_1xRTT, TelephonyManager.NETWORK_TYPE_IDEN -> "NETWORK_2G"
//                    TelephonyManager.NETWORK_TYPE_TD_SCDMA, TelephonyManager.NETWORK_TYPE_EVDO_A, TelephonyManager.NETWORK_TYPE_UMTS, TelephonyManager.NETWORK_TYPE_EVDO_0, TelephonyManager.NETWORK_TYPE_HSDPA, TelephonyManager.NETWORK_TYPE_HSUPA, TelephonyManager.NETWORK_TYPE_HSPA, TelephonyManager.NETWORK_TYPE_EVDO_B, TelephonyManager.NETWORK_TYPE_EHRPD, TelephonyManager.NETWORK_TYPE_HSPAP -> "NETWORK_3G"
//                    TelephonyManager.NETWORK_TYPE_IWLAN, TelephonyManager.NETWORK_TYPE_LTE -> "NETWORK_4G"
//                    TelephonyManager.NETWORK_TYPE_NR -> "NETWORK_5G"
//                    else -> {
//                        val subtypeName = info.subtypeName
//                        if (subtypeName.equals("TD-SCDMA", ignoreCase = true)
//                            || subtypeName.equals("WCDMA", ignoreCase = true)
//                            || subtypeName.equals("CDMA2000", ignoreCase = true)
//                        ) {
//                            "NETWORK_3G"
//                        } else {
//                            "NETWORK_UNKNOWN"
//                        }
//                    }
//                }
//            }
//
//            else -> {
//                "NETWORK_UNKNOWN"
//            }
//        }
//    } else "NETWORK_NO"
//}
//
//
//@SuppressLint("MissingPermission")
//private fun DeviceUtil.isEthernet(): Boolean {
//    val cm = activity.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
//        ?: return false
//    val info = cm.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET) ?: return false
//    val state = info.state ?: return false
//    return state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING
//}


/**
 * 获取DNS
 *
 * @return
 */
private fun getLocalDNS(): String? {
    var cmdProcess: Process? = null
    var reader: BufferedReader? = null
    var dnsIP: String? = ""
    return try {
        cmdProcess = Runtime.getRuntime().exec("getprop net.dns1")
        reader = BufferedReader(InputStreamReader(cmdProcess.inputStream))
        dnsIP = reader.readLine()
        dnsIP
    } catch (e: IOException) {
        null
    } finally {
        try {
            reader!!.close()
        } catch (_: IOException) {
        }
        cmdProcess!!.destroy()
    }
}