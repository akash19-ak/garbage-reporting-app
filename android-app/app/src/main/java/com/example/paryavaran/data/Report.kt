package com.example.paryavaran.data

data class Report(
    val id: Int? = null,
    val latitude: Double,
    val longitude: Double,
    val wasteType: String,
    val status: String = "Pending",
    val imageUrl: String = ""
)
