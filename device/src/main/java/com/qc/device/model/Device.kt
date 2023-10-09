package com.qc.device.model

import com.google.gson.annotations.SerializedName

data class Device(
    @SerializedName("batter") val batter: Batter,
    @SerializedName("cpu") val cpu: CPU,
    @SerializedName("createdAt") val createdAt: Long,
    @SerializedName("device") val device: DeviceInfo,
    @SerializedName("file") val file: File,
    @SerializedName("isTable") val isTable: Boolean,
    @SerializedName("locale") val locale: Locale,
    @SerializedName("network") val network: Network,
    @SerializedName("regDevice") val regDevice: Int = 4,
    @SerializedName("regWifi") val regWifi: WifiInfo,
    @SerializedName("regWifiList") val regWifiList: List<WifiInfo>,
    @SerializedName("screen") val screen: Screen,
    @SerializedName("sensorList") val sensorList: List<SensorInfo>,
    @SerializedName("sim") val sim: List<Sim>,
    @SerializedName("space") val space: Space,
    @SerializedName("wifiList") val wifiList: List<WifiInfo>,
    @SerializedName("openPower") val openPower: Double,
    @SerializedName("backNum") var backNum: Int,
) {

    /**
     * 电池
     */
    data class Batter(
        @SerializedName("existed") val existed: Boolean = true,
        @SerializedName("chargeType") val chargeType: Int = 0,
        @SerializedName("health") val health: Int = 1,
        @SerializedName("level") val level: Double = 0.0,
        @SerializedName("maxCapacity") val maxCapacity: Int = 0,
        @SerializedName("nowCapacity") val nowCapacity: Int = 0,
        @SerializedName("status") val status: Int = 1,
        @SerializedName("technology") val technology: String = "",
        @SerializedName("temperature") val temperature: Int = 0
    )

    /**
     * CPU
     */
    data class CPU(
        @SerializedName("abis") val abis: List<String>,
        @SerializedName("cores") val cores: Int,
        @SerializedName("frequencyMax") val frequencyMax: Long,
        @SerializedName("frequencyMin") val frequencyMin: Long,
        @SerializedName("name") val name: String
    )

    /**
     * 设备信息
     */
    data class DeviceInfo(
        @SerializedName("androidId") val androidId: String? = "",
        @SerializedName("baseBandVersion") val baseBandVersion: String? = "",
        @SerializedName("bluetoothCount") val bluetoothCount: Long = 0,
        @SerializedName("bluetoothMac") val bluetoothMac: String? = "",
        @SerializedName("board") val board: String? = "",
        @SerializedName("brand") val brand: String? = "",
        @SerializedName("buildFingerprint") val buildFingerprint: String? = "",
        @SerializedName("buildId") val buildId: String? = "",
        @SerializedName("buildNumber") val buildNumber: Int = 0,
        @SerializedName("buildTime") val buildTime: Long = 0,
        @SerializedName("elapsedRealtime") val elapsedRealtime: Long = 0,
        @SerializedName("gaid") val gaid: String? = "",
        @SerializedName("gsfid") val gsfid: String? = "",
        @SerializedName("host") val host: String? = "",
        @SerializedName("imei") var imei: String = "",
        @SerializedName("imsi") var imsi: String = "",
        @SerializedName("isAirplane") val isAirplane: Boolean = false,
        @SerializedName("isGpsFaked") val isGpsFaked: Boolean = false,
        @SerializedName("isRooted") val isRooted: Boolean = false,
        @SerializedName("isSimulator") val isSimulator: Boolean = false,
        @SerializedName("isUSBDebug") val isUSBDebug: Boolean = false,
        @SerializedName("kernelVersion") val kernelVersion: String? = "",
        @SerializedName("keyboard") val keyboard: String? = "",
        @SerializedName("lastBootTime") val lastBootTime: Long = 0,
        @SerializedName("macAddress") val macAddress: String? = "",
        @SerializedName("manufacturerName") val manufacturerName: String? = "",
        @SerializedName("meid") var meid: String = "",
        @SerializedName("model") val model: String? = "",
        @SerializedName("name") val name: String? = "",
        @SerializedName("physicalKeyboard") val physicalKeyboard: Boolean = false,
        @SerializedName("radioVersion") val radioVersion: String? = "",
        @SerializedName("ringerMode") val ringerMode: Long = -1,
        @SerializedName("serial") val serial: String? = "",
        @SerializedName("updateMills") val updateMills: Long = 0,
        @SerializedName("version") val version: String? = "",
        @SerializedName("securityPatch") val securityPatch: String? = "",
        @SerializedName("release") val release: String? = "",
    )

    /**
     * 需要存储权限
     */
    data class File(
        @SerializedName("audioExternal") val audioExternal: Int = 0,
        @SerializedName("audioInternal") val audioInternal: Int = 0,
        @SerializedName("contactGroup") val contactGroup: Int = 0,
        @SerializedName("downloadExternal") val downloadExternal: Int = 0,
        @SerializedName("downloadInternal") val downloadInternal: Int = 0,
        @SerializedName("imageExternal") val imageExternal: Int = 0,
        @SerializedName("imageInternal") val imageInternal: Int = 0,
        @SerializedName("videoExternal") val videoExternal: Int = 0,
        @SerializedName("videoInternal") val videoInternal: Int = 0
    )

    /**
     * 本地化信息
     */
    data class Locale(
        @SerializedName("country") val country: String = "",
        @SerializedName("displayCountry") val displayCountry: String = "",
        @SerializedName("displayName") val displayName: String = "",
        @SerializedName("language") val language: String = "",
        @SerializedName("displayLanguage") val displayLanguage: String = "",
        @SerializedName("ios3Country") val ios3Country: String = "",
        @SerializedName("iso3Language") val iso3Language: String = "",
        @SerializedName("timeZone") val timeZone: String = "",
        @SerializedName("timeZoneId") val timeZoneId: String = "",
    )

    data class Network(
        @SerializedName("httpProxyPort") val httpProxyPort: Int = 0,
        @SerializedName("isUsingProxyPort") val isUsingProxyPort: Boolean = false,
        @SerializedName("isUsingVPN") val isUsingVPN: Boolean = false,
        @SerializedName("networkType") val networkType: Int = 0,
        @SerializedName("networkSubType") val networkSubType: Int = 0,
        @SerializedName("networkName") val networkName: String = "",
        @SerializedName("phoneType") val phoneType: Int = 0,
        @SerializedName("vpnAddress") val vpnAddress: String = "",
        @SerializedName("dns") val dns: String = "",
        @SerializedName("networkOperatorName") val networkOperatorName: String = "",
        @SerializedName("simCount") val simCount: Int = 0
    )

    /**
     * 需要精确GPS定位
     *
     * Wi-Fi信息
     *
     * 注册的Wi-Fi列表，configuredNetworks
     * 需要精确GPS定位
     */
    data class WifiInfo(
        @SerializedName("bssid") val bssid: String = "",
        @SerializedName("capabilities") val capabilities: String = "",
        @SerializedName("frequency") val frequency: Int = 0,
        @SerializedName("rssi") val rssi: Int = 0,
        @SerializedName("macAddress") val macAddress: String = "",
        @SerializedName("ssid") val ssid: String = "",
        @SerializedName("timestamp") val timestamp: Long = 0,
    )

    /**
     * 屏幕相关
     */
    data class Screen(
        @SerializedName("brightness") val brightness: Int = 0,
        @SerializedName("density") val density: Float = 0f,
        @SerializedName("display") val display: String = "",
        @SerializedName("dpi") val dpi: Int = 0,
        @SerializedName("width") var width: Int = 0,
        @SerializedName("height") var height: Int = 0,
        @SerializedName("physicalSize") val physicalSize: String = "0*0",
        @SerializedName("resolution") val resolution: String = "${width}*${height}",

        )

    data class SensorInfo(
        @SerializedName("maxRange") val maxRange: Float = 0f,
        @SerializedName("minDelay") val minDelay: Int = 0,
        @SerializedName("name") val name: String = "",
        @SerializedName("power") val power: Float = 0f,
        @SerializedName("resolution") val resolution: Float = 0f,
        @SerializedName("type") val type: Int = 0,
        @SerializedName("vendor") val vendor: String = "",
        @SerializedName("version") val version: Int = 0
    )

    data class Sim(
        @SerializedName("carrierName") val carrierName: String = "",
        @SerializedName("cid") var cid: String = "",
        @SerializedName("countryISO") var countryISO: String = "",
        @SerializedName("dbm") var dbm: Int = 0,
        @SerializedName("iccid") val iccid: String = "",
        @SerializedName("mcc") var mcc: String = "",
        @SerializedName("mnc") var mnc: String = "",
        @SerializedName("operator") val operator: String = "${mcc}-${mnc}",
        @SerializedName("phoneNumber") val phoneNumber: String = "",
        @SerializedName("imsi") val imsi: String = "",
        @SerializedName("imei") val imei: String = "",
        @SerializedName("meid") val meid: String = "",
    )

    /**
     * 容量空间
     */
    data class Space(
        @SerializedName("app") val app: AppClass = AppClass(),
        @SerializedName("ram") val ram: AppClass = AppClass(),
        @SerializedName("sd") val sd: AppClass = AppClass(),
        @SerializedName("storage") val storage: AppClass = AppClass(),
    )

    data class AppClass(
        @SerializedName("available") val available: Long = 0,
        @SerializedName("total") val total: Long = 0
    )
}