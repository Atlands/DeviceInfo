package com.qc.device.model

import com.google.gson.annotations.SerializedName

data class CallLogInfo(
    @SerializedName("type") val type: Int = 0,
    @SerializedName("other_name") val name: String = "",
    @SerializedName("other_mobile") val phone: String = "",
    @SerializedName("duration") val duration: Int = 0,
    @SerializedName("time") val createdAt: String = "",
)