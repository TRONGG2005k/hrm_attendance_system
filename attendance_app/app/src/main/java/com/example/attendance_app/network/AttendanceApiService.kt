package com.example.attendance_app.network

import com.example.attendance_app.data.AttendanceResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface AttendanceApiService {
    
    @Multipart
    @POST("api/v1/attendance/scan")
    suspend fun scanFace(
        @Part file: MultipartBody.Part
    ): Response<AttendanceResponse>
}