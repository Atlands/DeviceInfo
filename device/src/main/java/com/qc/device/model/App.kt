package com.qc.device.model

import com.google.gson.annotations.SerializedName

data class App(
    @SerializedName("appName") val appName: String = "",
    @SerializedName("createdAt") val createdAt: Long = 0,
    @SerializedName("isSystem") val isSystem: Boolean = false,
    @SerializedName("packageName") val packageName: String = "",
    @SerializedName("specialPermissionList") val specialPermissionList: List<String> = emptyList(),
    @SerializedName("updatedAt") val updatedAt: Long = 0,
    @SerializedName("version") val version: String = "",
    @SerializedName("versionCode") val versionCode: Int = 0
)
