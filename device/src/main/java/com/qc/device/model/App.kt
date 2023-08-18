package com.qc.device.model

import androidx.annotation.Keep

@Keep
data class App(
    /**
     * 应用名称
     */
    val appName: String = "",

    /**
     * 安装时间
     */
    val createdAt: Long = 0,

    /**
     * 是否系统应用
     */
    val isSystem: Boolean = false,

    /**
     * 包名
     */
    val packageName: String = "",

    /**
     * app特殊权限项
     */
    val specialPermissionList: List<String> = emptyList(),

    /**
     * 更新时间
     */
    val updatedAt: Long = 0,

    /**
     * 版本名称，1.0.1
     */
    val version: String = "",

    /**
     * 版本号，1
     */
    val versionCode: Int = 0
)
