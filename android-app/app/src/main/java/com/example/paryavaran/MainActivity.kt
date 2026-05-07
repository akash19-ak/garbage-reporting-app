package com.example.paryavaran

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.paryavaran.data.*
import com.example.paryavaran.ui.theme.ParyavaranKavaluTheme
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { _ -> }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.CAMERA
            ))
        }

        setContent {
            ParyavaranKavaluTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    AppNavigation(
                        onSubmit = { request ->
                            lifecycleScope.launch {
                                try {
                                    RetrofitClient.instance.submitReport(request)
                                    Toast.makeText(this@MainActivity, "Report Submitted!", Toast.LENGTH_SHORT).show()
                                } catch (e: Exception) {
                                    Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun AppNavigation(onSubmit: (ReportRequest) -> Unit) {
    var currentScreen by remember { mutableStateOf("Map") }
    var reports by remember { mutableStateOf<List<Report>>(emptyList()) }
    val scope = rememberCoroutineScope()

    // Fetch reports when switching to Map
    LaunchedEffect(currentScreen) {
        if (currentScreen == "Map") {
            try {
                reports = RetrofitClient.instance.getReports()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Text("🗺️") },
                    label = { Text("Map") },
                    selected = currentScreen == "Map",
                    onClick = { currentScreen = "Map" }
                )
                NavigationBarItem(
                    icon = { Text("📷") },
                    label = { Text("Report") },
                    selected = currentScreen == "Report",
                    onClick = { currentScreen = "Report" }
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (currentScreen) {
                "Map" -> MapScreen(reports)
                "Report" -> ReportScreen(onReportSubmitted = {
                    onSubmit(it)
                    currentScreen = "Map"
                })
            }
        }
    }
}

@Composable
fun MapScreen(reports: List<Report>) {
    val bangalore = LatLng(12.9716, 77.5946)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(bangalore, 12f)
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        reports.forEach { report ->
            Marker(
                state = MarkerState(position = LatLng(report.latitude, report.longitude)),
                title = report.wasteType,
                snippet = "Status: ${report.status}"
            )
        }
    }
}

@Composable
fun ReportScreen(onReportSubmitted: (ReportRequest) -> Unit) {
    var wasteType by remember { mutableStateOf("Plastic") }
    
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Report New Waste", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))
        
        Text("Select Waste Type:")
        Row {
            FilterChip(
                selected = wasteType == "Plastic",
                onClick = { wasteType = "Plastic" },
                label = { Text("Plastic") }
            )
            Spacer(modifier = Modifier.width(8.dp))
            FilterChip(
                selected = wasteType == "Organic",
                onClick = { wasteType = "Organic" },
                label = { Text("Organic") }
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = {
                // Mocking current location for now
                onReportSubmitted(
                    ReportRequest(
                        latitude = 12.97 + (Math.random() - 0.5) / 10,
                        longitude = 77.59 + (Math.random() - 0.5) / 10,
                        wasteType = wasteType,
                        imageUrl = "https://example.com/mock.jpg"
                    )
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Submit Report to Backend")
        }
    }
}
