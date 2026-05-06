package com.example.paryavaran

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.paryavaran.ui.theme.ParyavaranKavaluTheme
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            // Handle permission results
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Request permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.CAMERA
            ))
        }

        setContent {
            ParyavaranKavaluTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    var currentScreen by remember { mutableStateOf("Map") }

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
                "Map" -> MapScreen()
                "Report" -> ReportScreen()
            }
        }
    }
}

@Composable
fun MapScreen() {
    val bangalore = LatLng(12.9716, 77.5946)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(bangalore, 12f)
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        // Mocking pins
        Marker(
            state = MarkerState(position = LatLng(12.97, 77.59)),
            title = "Plastic Waste",
            snippet = "Pending"
        )
    }
}

@Composable
fun ReportScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = { /* Handle camera capture */ }) {
            Text("Capture Photo")
        }
        Spacer(modifier = Modifier.height(16.dp))
        var wasteType by remember { mutableStateOf("Plastic") }
        // Simple mock selection
        Row {
            Button(onClick = { wasteType = "Plastic" }) { Text("Plastic") }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { wasteType = "Organic" }) { Text("Organic") }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("Selected: $wasteType")
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = { /* Submit report */ }) {
            Text("Submit Report")
        }
    }
}
