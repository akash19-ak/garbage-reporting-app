package com.example.paryavaran.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

// Models are now in Report.kt and separate classes here

// Matches Backend ReportRequest
data class ReportRequest(
    val latitude: Double,
    val longitude: Double,
    val wasteType: String,
    val imageUrl: String? = null
)

// Matches Backend StatusUpdateRequest
data class StatusUpdateRequest(
    val status: String
)

interface ApiService {
    @GET("reports")
    suspend fun getReports(): List<Report>

    @POST("reports")
    suspend fun submitReport(@Body request: ReportRequest): Report

    @PUT("reports/{id}/status")
    suspend fun updateStatus(
        @Path("id") id: Int,
        @Body request: StatusUpdateRequest
    ): Report
}

object RetrofitClient {
    // 10.0.2.2 is the special IP for Android Emulator to access the host machine's localhost
    private const val BASE_URL = "http://10.0.2.2:8080/"

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
