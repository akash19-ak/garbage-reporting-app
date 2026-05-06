# Paryavaran-Kavalu Backend Mock API

This is a simple Ktor backend simulating the remote server for the Paryavaran-Kavalu application.

## Endpoints

### 1. Get All Reports
- **URL:** `/reports`
- **Method:** `GET`
- **Response:**
```json
[
  {
    "id": 1,
    "latitude": 12.9716,
    "longitude": 77.5946,
    "wasteType": "Plastic",
    "status": "Pending",
    "imageUrl": "..."
  }
]
```

### 2. Submit a New Report
- **URL:** `/reports`
- **Method:** `POST`
- **Content-Type:** `application/json`
- **Body:**
```json
{
  "latitude": 12.9716,
  "longitude": 77.5946,
  "wasteType": "Plastic",
  "imageUrl": "base64_or_url"
}
```
- **Response:** Returns the created report with ID and "Pending" status.

### 3. Update Report Status (Simulate Cleaned)
- **URL:** `/reports/{id}/status`
- **Method:** `PUT`
- **Content-Type:** `application/json`
- **Body:**
```json
{
  "status": "Cleaned"
}
```
- **Response:** Returns the updated report.
