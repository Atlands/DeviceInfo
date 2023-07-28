package com.qc.device.utils.device

import android.content.Context
import android.hardware.input.InputManager
import android.view.InputDevice
import com.qc.device.utils.DeviceUtil
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader


/**
 * @Author Ben
 * @Date 2023/7/26 16:49
 * @desc:
 */

fun getBBVersion(): String {
    var Version = ""
    try {
        val cl = Class.forName("android.os.SystemProperties")
        val invoker = cl.newInstance()
        val m = cl.getMethod(
            "get", *arrayOf<Class<*>>(
                String::class.java,
                String::class.java
            )
        )
        val result = m.invoke(invoker, *arrayOf<Any>("gsm.version.baseband", "no message"))
        Version = result as String
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return Version
}


fun getKernelVersion(): String {

    var process: Process? = null
    var kernelVersion = ""
    try {
        process = Runtime.getRuntime().exec("cat/proc/version")
    } catch (e: IOException) {
        e.printStackTrace()
    }

    if (process == null) {
        return ""
    }


    val outs = process?.inputStream
    val isrout = InputStreamReader(outs)
    val brout = BufferedReader(isrout, 8 * 1024)
    var result = ""
    var line: String
    try {
        while (brout.readLine().also { line = it } != null) {
            result += line
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
    try {
        if (result !== "") {
            val Keyword = "version "
            var index = result.indexOf(Keyword)
            line = result.substring(index + Keyword.length)
            index = line.indexOf(" ")
            kernelVersion = line.substring(0, index)
        }
    } catch (e: IndexOutOfBoundsException) {
        e.printStackTrace()
    }
    return kernelVersion
}


fun DeviceUtil.getKeyboard(): String {

    var keyboardName: String? = null
    val inputManager = activity.getSystemService(Context.INPUT_SERVICE) as InputManager?
    if (inputManager != null) {
        val inputDeviceIds: IntArray = inputManager.inputDeviceIds
        val list = arrayListOf<String>()
        for (deviceId in inputDeviceIds) {
            val inputDevice: InputDevice = inputManager.getInputDevice(deviceId)
            val sources: Int = inputDevice.sources
            // 检查设备是否是键盘
            if (sources and InputDevice.SOURCE_KEYBOARD == InputDevice.SOURCE_KEYBOARD) {
                list.add(inputDevice.name)
            }
        }
        keyboardName = list.toString()
    }
    return keyboardName ?: ""
}
