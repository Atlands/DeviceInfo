package com.qc.device.model

import com.google.gson.annotations.SerializedName

data class Photo(
    @SerializedName("name") val name: String = "",
    @SerializedName("author") val author: String = "",
    @SerializedName("addTime") val createdAt: String = "",
    @SerializedName("updateTime") val updatedAt: String = "",
    @SerializedName("model") val model: String = "",
    @SerializedName("width") val width: Int = 0,
    @SerializedName("height") val height: Int = 0,
    @SerializedName("longitude") var longitude: Double = 0.0,
    @SerializedName("latitude") var latitude: Double = 0.0,
    @SerializedName("orientation") val orientation: String = "",
    @SerializedName("x_resolution") val resolutionX: String = "",
    @SerializedName("y_resolution") val resolutionY: String = "",
    @SerializedName("altitude") val altitude: Double = 0.0,
    @SerializedName("gps_processing_method") val gpsProcessingMethod: String = "",
    @SerializedName("lens_make") val lensMake: String = "",
    @SerializedName("lens_model") val lensModel: String = "",
    @SerializedName("focal_length") val focalLength: String = "",
    @SerializedName("flash") val flash: String = "",
    @SerializedName("software") val software: String = "",
    @SerializedName("latitude_ref") val latitudeRef: String = "",
    @SerializedName("longitude_ref") val longitudeRef: String = "",
)
