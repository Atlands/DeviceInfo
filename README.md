```kotlin
//保存上传信息
//keys = [app, call, photo, sms, calendar, contact]
//有更新时间用更新时间，否则用创建时间，否则用id
//注意日程的时间是 日程开始和结束时间，不是 创建时间，应该用id
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

