package com.qc.device.utils.device

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Build
import android.telephony.CellInfoCdma
import android.telephony.CellInfoGsm
import android.telephony.CellInfoLte
import android.telephony.CellInfoWcdma
import android.telephony.SubscriptionInfo
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import android.telephony.gsm.GsmCellLocation
import android.text.TextUtils
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.qc.device.model.Device
import com.qc.device.utils.DeviceUtil
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader


private const val TAG = "SIM"

@RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
fun DeviceUtil.getSIM(): List<Device.Sim> {
    if (ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.READ_PHONE_STATE
        ) != PackageManager.PERMISSION_GRANTED
    ) return emptyList()
    val simList = mutableListOf<Device.Sim>()
    //1.版本超过5.1，调用系统方法
    val mSubscriptionManager =
        activity.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
    var activeSubscriptionInfoList: List<SubscriptionInfo?>? = null
    try {
        activeSubscriptionInfoList = mSubscriptionManager.activeSubscriptionInfoList
    } catch (e: Exception) {
        e.printStackTrace()
    }

    if (!activeSubscriptionInfoList.isNullOrEmpty()) {
        //1.1.1 有使用的卡，就遍历所有卡
        for (i in activeSubscriptionInfoList.indices) {
            val subscriptionInfo: SubscriptionInfo? = activeSubscriptionInfoList[i]
            simList.add(
                Device.Sim(
                    carrierName = getNetworkOperatorName(),
                    iccid = subscriptionInfo?.iccId ?: "",
                    mcc = getMcc(),
                    mnc = getMnc(),
                    operator = "${getMcc()}+${getMnc()}",
                    networkType = getNetworkType() ?: "",
                    dbm = getMobileDbm() ?: "",
                    dns = getLocalDNS() ?: "",
                    countryISO = subscriptionInfo?.countryIso ?: "",
                    cid = getCidNumbers() ?: "",
                    serialNumber = subscriptionInfo?.iccId ?: "",
                    phoneNumber = subscriptionInfo?.number ?: ""

                )
            )


        }
    }
    Log.d(TAG, "getSIM: $simList")
    return simList
}


fun DeviceUtil.getIMEI(): String {
    val manager = activity.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
    return try {
        val method = manager?.javaClass?.getMethod("getImei", Int::class.javaPrimitiveType)
//       参数为卡槽Id，它的值为 0、1；
        method?.invoke(manager, 0) as String
    } catch (e: Exception) {
        ""
    }
}


@RequiresApi(Build.VERSION_CODES.M)
fun DeviceUtil.getIMSI(): String {
    if (activity.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
        return ""
    }
    val manager = activity.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        try {
            return manager?.getSubscriberId() ?: ""
        } catch (e: SecurityException) {
            e.printStackTrace()
            return ""
        }
    }
    return manager?.getSubscriberId() ?: ""
}


@RequiresApi(Build.VERSION_CODES.M)
fun DeviceUtil.getMEID(): String {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        return ""
    }
    val manager = activity.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
    val count = manager?.phoneCount
    val mSubscriptionManager = SubscriptionManager.from(activity)
    val simNumberCard = mSubscriptionManager.activeSubscriptionInfoCount //获取当前sim卡数量
    Log.d(TAG, "getMEID:  $simNumberCard")
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && simNumberCard > 0) {
        return getMinOne(manager?.getMeid(0), manager?.getMeid(1))
    } else {
        val deviceId: String? = manager?.getDeviceId()
        if (deviceId != null && deviceId.length == 14) {
            return deviceId
        }
    }
    return ""
}

private fun getMinOne(s0: String?, s1: String?): String {
    val empty0 = s0.isNullOrEmpty()
    val empty1 = s1.isNullOrEmpty()
    if (empty0 && empty1) return ""
    if (!empty0 && !empty1) {
        return if (s0!! <= s1!!) {
            s0
        } else {
            s1
        }
    }
    return if (!empty0) s0 ?: "" else s1 ?: ""
}


fun DeviceUtil.getGSFID(): String {
    val URI = Uri.parse("content://com.google.android.gsf.gservices")
    val ID_KEY = "android_id"
    val params = arrayOf(ID_KEY)
    val c = activity.contentResolver.query(URI, null, null, params, null)
    return if (!c!!.moveToFirst() || c.columnCount < 2) "" else try {
        java.lang.Long.toHexString(c.getString(1).toLong())
    } catch (e: NumberFormatException) {
        e.printStackTrace()
        ""
    }

}


/**
 * @return 获取当前SIM卡数量
 */
@SuppressLint("MissingPermission")
fun DeviceUtil.getJudgeSIMCount(): Int {
    if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE)
        != PackageManager.PERMISSION_GRANTED
    ) {
        return 0
    }
    var count = 0
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
        count = SubscriptionManager.from(activity).activeSubscriptionInfoCount
        return count
    }
    return count
}
private fun DeviceUtil.getTelephonyManager(): TelephonyManager? {
    return activity.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
}


/**
 * @return 获取当前SIM卡槽数量
 */
fun DeviceUtil.getPhoneSimCount(): Int {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val manager = activity.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
        manager?.phoneCount ?: 0
    } else 0
}


fun DeviceUtil.getMcc(): String {
    val networkOperator = getTelephonyManager()?.networkOperator
    return if (!TextUtils.isEmpty(networkOperator)) {
        networkOperator?.substring(0, 3) ?: ""
    } else {
        ""
    }
}

fun DeviceUtil.getMnc(): String {
    val networkOperator = getTelephonyManager()?.networkOperator
    return if (!TextUtils.isEmpty(networkOperator)) {
        networkOperator?.substring(3) ?: ""
    } else {
        ""
    }
}
fun DeviceUtil.getNetworkOperatorName(): String {
    return getTelephonyManager()?.networkOperatorName ?: ""
}
/**
 * 基站编号
 *
 * @return
 */
fun DeviceUtil.getCidNumbers(): String? {
    if (getTelephonyManager()
            ?.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM
    ) {
        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return ""
        }
        val location = getTelephonyManager()?.getCellLocation() as GsmCellLocation
        return location.cid.toString()
    }
    return ""
}


/**
 * 获取DNS
 *
 * @return
 */
fun DeviceUtil.getLocalDNS(): String? {
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
        } catch (e: IOException) {
        }
        cmdProcess!!.destroy()
    }
}


@SuppressLint("NewApi")
fun DeviceUtil.getMobileDbm(): String? {
    var dbm = ""
    val tm: TelephonyManager? = getTelephonyManager()
    if (ActivityCompat.checkSelfPermission(
            activity,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        != PackageManager.PERMISSION_GRANTED
    ) {
        return dbm
    }
    val cellInfoList = tm?.allCellInfo
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
        if (null != cellInfoList) {
            for (cellInfo in cellInfoList) {
                if (cellInfo is CellInfoGsm) {
                    val cellSignalStrengthGsm = cellInfo.cellSignalStrength
                    dbm = cellSignalStrengthGsm.dbm.toString()
                } else if (cellInfo is CellInfoCdma) {
                    val cellSignalStrengthCdma = cellInfo.cellSignalStrength
                    dbm = cellSignalStrengthCdma.dbm.toString()
                } else if (cellInfo is CellInfoWcdma) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                        val cellSignalStrengthWcdma = cellInfo.cellSignalStrength
                        dbm = cellSignalStrengthWcdma.dbm.toString()
                    }
                } else if (cellInfo is CellInfoLte) {
                    val cellSignalStrengthLte = cellInfo.cellSignalStrength
                    dbm = cellSignalStrengthLte.dbm.toString()
                }
            }
        }
    }
    return dbm
}

fun DeviceUtil.getNetworkType(): String? {

    if (ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.ACCESS_NETWORK_STATE
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        return "NETWORK_NO"
    }
    if (isEthernet()) {
        return "NETWORK_ETHERNET"
    }

    val cm = activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        ?: return null
    val info = cm.activeNetworkInfo
    return if (info != null && info.isAvailable) {
        if (info.type == ConnectivityManager.TYPE_WIFI) {
            "NETWORK_WIFI"
        } else if (info.type == ConnectivityManager.TYPE_MOBILE) {
            when (info.subtype) {
                TelephonyManager.NETWORK_TYPE_GSM, TelephonyManager.NETWORK_TYPE_GPRS, TelephonyManager.NETWORK_TYPE_CDMA, TelephonyManager.NETWORK_TYPE_EDGE, TelephonyManager.NETWORK_TYPE_1xRTT, TelephonyManager.NETWORK_TYPE_IDEN -> "NETWORK_2G"
                TelephonyManager.NETWORK_TYPE_TD_SCDMA, TelephonyManager.NETWORK_TYPE_EVDO_A, TelephonyManager.NETWORK_TYPE_UMTS, TelephonyManager.NETWORK_TYPE_EVDO_0, TelephonyManager.NETWORK_TYPE_HSDPA, TelephonyManager.NETWORK_TYPE_HSUPA, TelephonyManager.NETWORK_TYPE_HSPA, TelephonyManager.NETWORK_TYPE_EVDO_B, TelephonyManager.NETWORK_TYPE_EHRPD, TelephonyManager.NETWORK_TYPE_HSPAP -> "NETWORK_3G"
                TelephonyManager.NETWORK_TYPE_IWLAN, TelephonyManager.NETWORK_TYPE_LTE -> "NETWORK_4G"
                TelephonyManager.NETWORK_TYPE_NR -> "NETWORK_5G"
                else -> {
                    val subtypeName = info.subtypeName
                    if (subtypeName.equals("TD-SCDMA", ignoreCase = true)
                        || subtypeName.equals("WCDMA", ignoreCase = true)
                        || subtypeName.equals("CDMA2000", ignoreCase = true)
                    ) {
                        "NETWORK_3G"
                    } else {
                        "NETWORK_UNKNOWN"
                    }
                }
            }
        } else {
            "NETWORK_UNKNOWN"
        }
    } else "NETWORK_NO"
}


private fun DeviceUtil.isEthernet(): Boolean {
    val cm = activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        ?: return false
    val info = cm.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET) ?: return false
    val state = info.state ?: return false
    return state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING
}



