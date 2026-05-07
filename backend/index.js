const express = require('express');
const cors = require('cors');

const app = express();
const port = 8080;

app.use(cors());
app.use(express.json());

let reports = [];
let currentId = 1;

// GET /
app.get('/', (req, res) => {
    res.send('Paryavaran-Kavalu API');
});

// GET /reports
app.get('/reports', (req, res) => {
    res.json(reports);
});

// POST /reports
app.post('/reports', (req, res) => {
    try {
        const { latitude, longitude, wasteType, imageUrl } = req.body;

        if (latitude === undefined || longitude === undefined || !wasteType) {
            return res.status(400).send('Invalid request: Missing required fields');
        }

        const newReport = {
            id: currentId++,
            latitude,
            longitude,
            wasteType,
            status: "Pending",
            imageUrl: imageUrl || ""
        };

        reports.push(newReport);
        res.status(201).json(newReport);
    } catch (e) {
        res.status(400).send(`Invalid request: ${e.message}`);
    }
});

// PUT /reports/:id/status
app.put('/reports/:id/status', (req, res) => {
    const id = parseInt(req.params.id);
    const { status } = req.body;

    if (isNaN(id)) {
        return res.status(400).send('Invalid ID');
    }

    if (!status) {
        return res.status(400).send('Missing status');
    }

    const report = reports.find(r => r.id === id);
    if (report) {
        report.status = status;
        res.json(report);
    } else {
        res.status(404).send('Report not found');
    }
});

app.listen(port, '0.0.0.0', () => {
    console.log(`Server running at http://0.0.0.0:${port}`);
});
