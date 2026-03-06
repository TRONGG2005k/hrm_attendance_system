package com.example.attendance_app.data

import com.google.gson.annotations.SerializedName

data class AttendanceResponse(
    @SerializedName("employeeCode")
    val employeeCode: String?,
    
    @SerializedName("employeeName")
    val employeeName: String?,
    
    @SerializedName("time")
    val time: String?,
    
    @SerializedName("status")
    val status: String?,
    
    @SerializedName("message")
    val message: String?
)