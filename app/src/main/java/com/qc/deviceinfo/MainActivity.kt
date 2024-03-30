package com.qc.deviceinfo

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.qc.device.DataCenter
import com.qc.deviceinfo.ui.theme.DeviceInfoTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val TAG = "MainActivity"
    lateinit var dataCenter: DataCenter
    private val gson = GsonBuilder().setPrettyPrinting().create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataCenter = DataCenter(this)
        Log.d(TAG, "MainActivity onCreate")

        setContent {
            DeviceInfoTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {


                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {

                        TextButton(name = "Referrer") {
                            dataCenter.getReferrer {
                                Log.d(TAG, "onCreate: ${Gson().toJson(it)}")
                            }
                        }

                        TextButton("获取安装应用列表") {
                            lifecycleScope.launch(Dispatchers.Main) {
                                val apps = Gson().toJson(dataCenter.getApps())
                                Log.d(TAG, apps)
                            }
                        }

                        TextButton("获取位置信息") {
                            dataCenter.getPosition {
                                Log.d(TAG, "onCreate: $it")
                            }
                        }
                        TextButton("获取短信") {
                            dataCenter.getMessages {
                                Log.d(TAG, "onCreate: $it")
                            }
                        }

                        TextButton("获取设备信息") {
                            dataCenter.getDevice {
                                val toJson = Gson().toJson(it)
                                Log.d(TAG, "onCreate: $toJson")
                            }
                        }

                        TextButton("获取包信息") {
                            lifecycleScope
                            var pack = dataCenter.getPackageInfo()
                            Toast.makeText(baseContext, gson.toJson(pack), Toast.LENGTH_LONG).show()
                        }


                        TextButton("保存上传状态") {
                            val ar = """{"sms":"2019-06-14 13:39:08"}"""

                            val map =
                                Gson().fromJson<Map<String, Any>>(ar, Map::class.java)
                            dataCenter.savePreferences(map)
                        }

                    }

                }
            }
        }
    }
}


@Composable
fun TextButton(name: String, onClick: () -> Unit) {

    Text(
        text = name,
        modifier = Modifier
            .padding(10.dp)
            .clickable {
                onClick.invoke()
            },
        fontSize = 18.sp,
    )

}

