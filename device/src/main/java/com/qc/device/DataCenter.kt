package com.qc.device

import android.content.Context
import android.content.SharedPreferences
import androidx.activity.ComponentActivity
import androidx.core.content.edit
import com.qc.device.model.App
import com.qc.device.model.Calendar
import com.qc.device.model.CallLogInfo
import com.qc.device.model.Contact
import com.qc.device.model.Device
import com.qc.device.model.Message
import com.qc.device.model.Photo
import com.qc.device.model.Position
import com.qc.device.model.Referrer
import com.qc.device.model.Result
import com.qc.device.model.ResultError
import com.qc.device.utils.CalendarUtil
import com.qc.device.utils.CallLogUtil
import com.qc.device.utils.ContactUtil
import com.qc.device.utils.DeviceUtil
import com.qc.device.utils.MessageUtil
import com.qc.device.utils.PackageUtil
import com.qc.device.utils.PhotoUtil
import com.qc.device.utils.PositionUtil
import com.qc.device.utils.ReferrerUtil
import com.qc.device.utils.dateFormat
import com.qc.device.utils.toDate
import java.util.UUID

object PreferencesKey {
    const val device_ID = "device_id"
    const val Contact_Timestamp = "contact_timestamp"
    const val Calendar_ID = "calendar_id"
    const val Sms_Timestamp = "sms_timestamp"
    const val Photo_Timestamp = "photo_timestamp"
    const val Call_Timestamp = "call_log_timestamp"
    const val App_Timestamp = "app_timestamp"
}

class DataCenter(activity: ComponentActivity) {
    private val contactUtil: ContactUtil = ContactUtil(activity)
    private val packageUtil = PackageUtil(activity)
    private val deviceUtil = DeviceUtil(activity)
    private val calendarUtil = CalendarUtil(activity)
    private val messageUtil = MessageUtil(activity)
    private val photoUtil = PhotoUtil(activity)
    private val positionUtil = PositionUtil(activity)
    private val callLogUtil = CallLogUtil(activity)
    private val referrerUtil = ReferrerUtil(activity)

    private val preferences: SharedPreferences by lazy {
        activity.getSharedPreferences("FlutterSharedPreferences", Context.MODE_PRIVATE)
    }

    fun getDevice(onResult: (Result<Device>) -> Unit) {
        deviceUtil.getDevice(onResult)
    }

    fun getReferrer(onResult: (Result<Referrer?>) -> Unit) {
        referrerUtil.getReferrerDetails(onResult)
    }

    fun savePreferences(maps: Map<String, Any>) {
        preferences.edit {
            maps.forEach { item ->
                when (item.key) {
                    "app" ->
                        putLong(PreferencesKey.App_Timestamp, item.value as Long)

                    "call" ->
                        putLong(PreferencesKey.Call_Timestamp, valueToLong(item.value))

                    "photo" ->
                        putLong(PreferencesKey.Photo_Timestamp, valueToLong(item.value))

                    "sms" ->
                        putLong(PreferencesKey.Sms_Timestamp, valueToLong(item.value))

                    "calendar" ->
                        putLong(PreferencesKey.Calendar_ID, valueToLong(item.value))

                    "contact" ->
                        putLong(PreferencesKey.Contact_Timestamp, valueToLong(item.value))
                }
            }
        }
    }

    fun cleanPreferences() {
        preferences.edit {
            remove(PreferencesKey.App_Timestamp)
            remove(PreferencesKey.Call_Timestamp)
            remove(PreferencesKey.Photo_Timestamp)
            remove(PreferencesKey.Sms_Timestamp)
            remove(PreferencesKey.Calendar_ID)
            remove(PreferencesKey.Contact_Timestamp)
        }
    }

    private fun valueToLong(value: Any): Long = if (value is Long) {
        value
    } else {
        (value as String).toDate().time
    }


    fun getContacts(onResult: (Result<List<Contact>>) -> Unit) {
        contactUtil.getContacts { result ->
            if (result.code == ResultError.RESULT_OK) {
                val timestamp = preferences.getLong(PreferencesKey.Contact_Timestamp, 0)
                val data = result.data.filter {
                    try {
                        (it.updatedAt.let { it1 -> dateFormat.parse(it1)?.time } ?: 0) > timestamp
                    } catch (_: Exception) {
                        true
                    }
                }
                onResult(result.copy(data = data))
            } else {
                onResult(result)
            }
        }
    }

    fun getCalendars(onResult: (Result<List<Calendar>>) -> Unit) {
        val id = preferences.getLong(PreferencesKey.Calendar_ID, 0)
        calendarUtil.getCalendars(id, onResult)
    }

    fun getMessages(onResult: (Result<List<Message>>) -> Unit) {
        contactUtil.getContacts {
            val timestamp = preferences.getLong(PreferencesKey.Sms_Timestamp, 0)
            messageUtil.getMessages(timestamp, it.data, onResult)
        }
    }

    suspend fun getApps(): List<App> {
        val timestamp = preferences.getLong(PreferencesKey.App_Timestamp, 0)
        return packageUtil.allPackages(timestamp)
    }

    fun getPhotos(onResult: (Result<List<Photo>>) -> Unit) {
        val timestamp = preferences.getLong(PreferencesKey.Photo_Timestamp, 0)
        photoUtil.getPhotos(timestamp, onResult)
    }

    fun getCallLogs(onResult: (Result<List<CallLogInfo>>) -> Unit) {
        val timestamp = preferences.getLong(PreferencesKey.Call_Timestamp, 0)
        callLogUtil.getCallLogs(timestamp, onResult)
    }

    fun getPosition(onResult: (Result<Position?>) -> Unit) {
        positionUtil.getPosition(onResult)
    }


    ///通过id进行增量去重
//    private fun <T : DataID> deduplicationByID(allData: List<T>, preferencesKey: String): List<T> {
//        val oldIds = preferences.getStringSet(preferencesKey, setOf()) ?: setOf()
//        val newIds = allData.map { it.id }.toSet()
//        return if (oldIds.isEmpty()) {
//            allData
//        } else {
//            val difference = newIds - oldIds
//            allData.filter { it.id in difference }
//        }
//    }
//
//    //通过时间进行增量去重
//    private fun <T : DataDate> deduplicationByDate(
//        allData: List<T>,
//        preferencesKey: String
//    ): List<T> {
//        val oldTimestamp = preferences.getLong(preferencesKey, 0)
//        return allData.filter {
//            it.createdAt > oldTimestamp
//        }
//    }

    /**
     * 获取本包信息
     */
    fun getPackageInfo(): App = packageUtil.getPackageInfo()

    /**
     * 获取设备唯一标识符
     */
    fun getDeviceId(): String = deviceUtil.getAndroidID().ifBlank {
        var id = preferences.getString(PreferencesKey.device_ID, "") ?: ""
        if (id.isBlank()) {
            id = UUID.randomUUID().toString()
            preferences.edit {
                putString(PreferencesKey.device_ID, id)
            }
        }
        id
    }
}