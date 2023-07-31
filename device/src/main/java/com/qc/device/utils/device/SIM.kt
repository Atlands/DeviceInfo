package com.qc.device.utils.device

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.telephony.SubscriptionInfo
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import android.telephony.gsm.GsmCellLocation
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.qc.device.model.Device
import com.qc.device.utils.DeviceUtil


//private const val TAG = "SIM"

@SuppressLint("MissingPermission")
fun DeviceUtil.getSimList(): List<Device.Sim> {
    if (ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.READ_PHONE_STATE
        ) != PackageManager.PERMISSION_GRANTED
    ) return emptyList()
    val simList = mutableListOf<Device.Sim>()
    //1.版本超过5.1，调用系统方法
    val mSubscriptionManager =
        activity.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager?
            ?: return emptyList()
    val activeSubscriptionInfoList: List<SubscriptionInfo?> =
        try {
            mSubscriptionManager.activeSubscriptionInfoList
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } ?: return emptyList()

    val dbmList = getDbmList()

    //1.1.1 有使用的卡，就遍历所有卡
    for (i in activeSubscriptionInfoList.indices) {
        val subscriptionInfo: SubscriptionInfo? = activeSubscriptionInfoList[i]
        simList.add(
            Device.Sim(
                carrierName = subscriptionInfo?.carrierName?.toString(),
                iccid = subscriptionInfo?.iccId ?: "",
                countryISO = subscriptionInfo?.countryIso ?: "",
                phoneNumber = subscriptionInfo?.number ?: "",
                imsi = subscriptionInfo?.subscriptionId?.toString(),
                imei = getIMEI(i),
                meid = getMeid(i)
            ).apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    mcc = subscriptionInfo?.mccString
                    mnc = subscriptionInfo?.mncString
                } else {
                    mcc = subscriptionInfo?.mcc?.toString()
                    mnc = subscriptionInfo?.mnc?.toString()
                }
                if (dbmList.size > i) try {
                    dbm = dbmList[i]
                } catch (_: Exception) {
                }
                if (i == 0) cid = getCidNumbers()
                subscriptionInfo?.subscriptionId
            }
        )
    }
//    Log.d(TAG, "getSIM: $simList")
    return simList
}


@SuppressLint("MissingPermission")
fun DeviceUtil.getIMEI(slotIndex: Int): String? {
    if (ActivityCompat.checkSelfPermission(
            activity,
            "android.permission.READ_PRIVILEGED_PHONE_STATE"
        )
        != PackageManager.PERMISSION_GRANTED
    ) return null
    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            telephonyManager?.getImei(slotIndex)
        } else {
            val method =
                telephonyManager?.javaClass?.getMethod("getImei", Int::class.javaPrimitiveType)
            //参数为卡槽Id，它的值为 0、1；
            method?.invoke(telephonyManager, slotIndex) as String
        }
    } catch (e: Exception) {
        null
    }
}

@SuppressLint("HardwareIds")
fun DeviceUtil.getMeid(slotIndex: Int): String? {
    if (ActivityCompat.checkSelfPermission(
            activity,
            "android.permission.READ_PRIVILEGED_PHONE_STATE"
        )
        != PackageManager.PERMISSION_GRANTED
    ) return null
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        telephonyManager?.getMeid(slotIndex)
    } else {
        telephonyManager?.deviceId
    }
}


//@SuppressLint("MissingPermission", "HardwareIds")
//fun DeviceUtil.getIMSI(): String {
//    if (activity.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
//        return ""
//    }
//    val manager = activity.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
////    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//    return try {
//        manager?.subscriberId ?: ""
//    } catch (e: SecurityException) {
//        e.printStackTrace()
//        ""
//    }
////    }
////    return manager?.subscriberId ?: ""
//}


//@SuppressLint("MissingPermission", "HardwareIds")
//fun DeviceUtil.getMEID(): String {
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//        return ""
//    }
//    val manager = activity.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
//    val count = manager?.phoneCount
//    val mSubscriptionManager = SubscriptionManager.from(activity)
//    val simNumberCard = mSubscriptionManager.activeSubscriptionInfoCount //获取当前sim卡数量
////    Log.d(TAG, "getMEID:  $simNumberCard")
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && simNumberCard > 0) {
//        return getMinOne(manager?.getMeid(0), manager?.getMeid(1))
//    } else {
//        val deviceId: String? = manager?.deviceId
//        if (deviceId != null && deviceId.length == 14) {
//            return deviceId
//        }
//    }
//    return ""
//}
//
//private fun getMinOne(s0: String?, s1: String?): String {
//    val empty0 = s0.isNullOrEmpty()
//    val empty1 = s1.isNullOrEmpty()
//    if (empty0 && empty1) return ""
//    if (!empty0 && !empty1) {
//        return if (s0!! <= s1!!) {
//            s0
//        } else {
//            s1
//        }
//    }
//    return if (!empty0) s0 ?: "" else s1 ?: ""
//}

///**
// * @return 获取当前SIM卡数量
// */
//@SuppressLint("MissingPermission")
//fun DeviceUtil.getJudgeSIMCount(): Int {
//    if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE)
//        != PackageManager.PERMISSION_GRANTED
//    ) {
//        return 0
//    }
//    var count = 0
//    count = SubscriptionManager.from(activity).activeSubscriptionInfoCount
//    return count
//}

//private fun DeviceUtil.telephonyManager(): TelephonyManager? {
//    return activity.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
//}

val DeviceUtil.telephonyManager: TelephonyManager?
    get() {
        return activity.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
    }

///**
// * @return 获取当前SIM卡槽数量
// */
//fun DeviceUtil.getPhoneSimCount(): Int {
//    val manager = activity.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
//    return manager?.phoneCount ?: 0
//}


//fun DeviceUtil.getMcc(): String {
//    val networkOperator = telephonyManager?.networkOperator
//    return if (!TextUtils.isEmpty(networkOperator)) {
//        networkOperator?.substring(0, 3) ?: ""
//    } else {
//        ""
//    }
//}

//fun DeviceUtil.getMnc(): String {
//    val networkOperator = telephonyManager?.networkOperator
//    return if (!TextUtils.isEmpty(networkOperator)) {
//        networkOperator?.substring(3) ?: ""
//    } else {
//        ""
//    }
//}

//fun DeviceUtil.getNetworkOperatorName(): String {
//    return telephonyManager?.networkOperatorName ?: ""
//}

/**
 * 基站编号
 *
 * @return
 */
fun DeviceUtil.getCidNumbers(): String {
    if (telephonyManager
            ?.phoneType == TelephonyManager.PHONE_TYPE_GSM
    ) {
        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return ""
        }
        val location = telephonyManager?.cellLocation as GsmCellLocation
        return location.cid.toString()
    }
    return ""
}

@SuppressLint("NewApi")
fun DeviceUtil.getDbmList(): List<Int> {
    val telephonyManager: TelephonyManager = telephonyManager ?: return emptyList()
    if (ActivityCompat.checkSelfPermission(
            activity,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        != PackageManager.PERMISSION_GRANTED
    ) return emptyList()
    val cellInfoList = telephonyManager.allCellInfo ?: return emptyList()
    return cellInfoList.map {
        it.cellIdentity.operatorAlphaShort
        it.cellSignalStrength.dbm
    }
}


