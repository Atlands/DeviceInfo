package com.qc.device.utils

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import com.qc.device.model.App
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PackageUtil(private val context: Context) {
    /**
     * 查询已安装应用列表
     */
    suspend fun allPackages(timestamp: Long): List<App> = withContext(Dispatchers.IO) {
        val manager = context.packageManager
        val packages = try {
            manager.getInstalledPackages(0)
        } catch (_: Exception) {
            emptyList<PackageInfo>()
        }

        packages.filter {
            it.lastUpdateTime > timestamp
        }.map {
            val isSystem = (it.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) == 1
            App(
                appName = try {
                    it.applicationInfo.loadLabel(manager).toString()
                } catch (_: Exception) {
                    ""
                },
                packageName = it.packageName ?: "",
                version = it.versionName ?: "",
                versionCode = try {
                    it.versionCode
                } catch (_: Exception) {
                    0
                },
                isSystem = isSystem,
                createdAt = it.firstInstallTime,
                updatedAt = it.lastUpdateTime,
                specialPermissionList = try {
                    it.requestedPermissions?.toList() ?: emptyList()
                } catch (_: Exception) {
                    emptyList()
                }
            )
        }.sortedBy { it.updatedAt }
    }

    fun getPackageInfo(): App {
        val packageManager = context.packageManager
        val info = packageManager.getPackageInfo(context.packageName, 0)
        return App(
            appName = try {
                context.applicationInfo.loadLabel(packageManager).toString()
            } catch (e: java.lang.Exception) {
                ""
            },
            packageName = info.packageName ?: "",
            version = info.versionName ?: "",
            versionCode = info.versionCode,
        )
    }
}