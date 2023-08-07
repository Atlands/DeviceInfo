package com.qc.device.model

import com.google.gson.annotations.SerializedName

data class Position(
    @SerializedName("position_x") val latitude: Double = 0.0,
    @SerializedName("position_y") val longitude: Double = 0.0,
    @SerializedName("location") var address: String = "",
    @SerializedName("geo_time") var geo_time: String = "",
    @SerializedName("gps_address_province") var gps_address_province: String = "",
    @SerializedName("gps_address_city") var gps_address_city: String = "",
    @SerializedName("gps_address_street") var gps_address_street: String = "",
)
