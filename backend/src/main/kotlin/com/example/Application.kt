package com.example

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

@Serializable
data class Report(
    val id: Int,
    val latitude: Double,
    val longitude: Double,
    val wasteType: String,
    var status: String = "Pending",
    val imageUrl: String = ""
)

val reports = mutableListOf<Report>()
var currentId = 1

fun Application.module() {
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
        })
    }

    routing {
        get("/") {
            call.respondText("Paryavaran-Kavalu API")
        }

        get("/reports") {
            call.respond(reports)
        }

        post("/reports") {
            try {
                // Simplified mock for multipart or json. We will accept JSON for simplicity in this mock backend.
                val reportReq = call.receive<ReportRequest>()
                val newReport = Report(
                    id = currentId++,
                    latitude = reportReq.latitude,
                    longitude = reportReq.longitude,
                    wasteType = reportReq.wasteType,
                    status = "Pending",
                    imageUrl = reportReq.imageUrl ?: ""
                )
                reports.add(newReport)
                call.respond(HttpStatusCode.Created, newReport)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Invalid request: ${e.message}")
            }
        }

        put("/reports/{id}/status") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid ID")
                return@put
            }
            
            val req = call.receive<StatusUpdateRequest>()
            val report = reports.find { it.id == id }
            if (report != null) {
                report.status = req.status
                call.respond(HttpStatusCode.OK, report)
            } else {
                call.respond(HttpStatusCode.NotFound, "Report not found")
            }
        }
    }
}

@Serializable
data class ReportRequest(
    val latitude: Double,
    val longitude: Double,
    val wasteType: String,
    val imageUrl: String? = null
)

@Serializable
data class StatusUpdateRequest(
    val status: String
)
