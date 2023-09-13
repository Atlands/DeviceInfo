package com.qc.device.utils.device

import android.os.Build
import com.qc.device.model.Device
import com.qc.device.utils.DeviceUtil
import java.io.File

fun DeviceUtil.getCPU(): Device.CPU {
    val cpuFile = File("/proc/cpuinfo")
    val cpuName: String? =
        if (cpuFile.exists()) cpuFile.readLines().find { it.startsWith("Hardware") }
            ?.substringAfter(":")
            ?.trim() else null
    val coreFile = File("/sys/devices/system/cpu/")
    val cores: Int =
        if (coreFile.exists()) coreFile.listFiles { file -> file.name.matches(Regex("cpu\\d+")) }?.size
            ?: 1 else 1
    val minFile = File("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq")
    val frequencyMin: Long? =
        if (minFile.exists()) minFile.readText().toLongOrNull() else null
    val maxFile = File("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq")
    val frequencyMax: Long? =
        if (maxFile.exists()) maxFile.readText().toLongOrNull() else null
//    val architecture: String? =
//        File("/proc/cpuinfo").readLines().find { it.startsWith("Processor") }?.substringAfter(":")
//            ?.trim()
    val abis = Build.SUPPORTED_ABIS.toList()
    return Device.CPU(
        name = cpuName ?: "",
        cores = cores,
        frequencyMin = frequencyMin ?: 0,
        frequencyMax = frequencyMax ?: 0,
//        architecture = architecture,
        abis = abis
    )
}


