package com.qc.deviceinfo

import android.os.Bundle
import android.util.Log
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
import com.google.gson.Gson
import com.qc.device.DataCenter
import com.qc.deviceinfo.ui.theme.DeviceInfoTheme

class MainActivity : ComponentActivity() {

    private val TAG = "MainActivity"
    var dataCenter = DataCenter(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)





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


                        TextButton("获取安装应用列表") {
                            val apps = Gson().toJson(dataCenter.getApps())
                            Log.d(TAG, apps)
                        }

                        TextButton("获取设备信息") {
                            dataCenter.getDevice {
                                Log.d(TAG, "onCreate: $it")
                            }
                        }

                        TextButton("获取设备信息") {
                            dataCenter.getDevice {
                                val toJson = Gson().toJson(it)
                                Log.d(TAG, "onCreate: $toJson")
                            }
                        }

                        TextButton("获取设备信息") {
                            dataCenter.getDevice {
                                Log.d(TAG, "onCreate: $it")
                            }
                        }


                        TextButton("获取设备信息") {
                            dataCenter.getDevice {
                                Log.d(TAG, "onCreate: $it")
                            }
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

