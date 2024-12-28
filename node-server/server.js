const express = require('express');
const axios = require('axios');
const path = require('path');

const app = express();
const PORT = 3000;

// Determine the backend API base URL
const BACKEND_API = process.env.BACKEND_API || 'http://localhost:8080'; // Default to localhost for local dev

console.log(`Backend API URL: ${BACKEND_API}`);

// Proxy API requests to Spring Boot
app.use('/api', async (req, res) => {
    try {
        // Forward the request to the Spring Boot backend
        const response = await axios({
            method: req.method,
            url: `${BACKEND_API}${req.originalUrl}`, // Append the original API path
            data: req.body,
            headers: req.headers,
        });
        res.status(response.status).send(response.data);
    } catch (error) {
        console.error('Error while proxying request:', error.message);
        res.status(error.response ? error.response.status : 500).send(error.message);
    }
});

// Serve Vue.js static files from the "dist" directory
app.use(express.static(path.join(__dirname, 'dist')));

// Handle SPA routing for the frontend
app.get('*', (req, res) => {
    res.sendFile(path.join(__dirname, 'dist', 'index.html'));
});

// Start the server
app.listen(PORT, () => {
    console.log(`Node.js server is running at http://localhost:${PORT}`);
});

