
# Full-Stack Application Instrumentation with Datadog RUM and APM

This document describes how to instrument a full-stack web application using Datadog's Real User Monitoring (RUM) and Application Performance Monitoring (APM). The setup includes changes for Vue.js frontend, Node.js server, and Spring Boot backend. Instructions are provided for both local and Dockerized environments.

---

## **Instrumentation for Local Development**

### **1. Vue.js Frontend**

#### **File: `vue-frontend/public/index.html`**
Add the Datadog RUM SDK using the **synchronous CDN method**:

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>Vue App</title>
    <script
        src="https://www.datadoghq-browser-agent.com/us1/v5/datadog-rum.js"
        type="text/javascript">
    </script>
    <script>
        window.DD_RUM && window.DD_RUM.init({
            clientToken: 'pub392166f801e49db7d54982758474bb51',
            applicationId: 'cbaf4743-a33e-4a25-b37b-23b11973c85b',
            site: 'datadoghq.com',
            service: 'rumsdk',
            env: 'dev',
            allowedTracingUrls: ["http://localhost:3000"],
            sessionSampleRate: 100,
            sessionReplaySampleRate: 20,
            trackUserInteractions: true,
            trackResources: true,
            trackLongTasks: true,
            defaultPrivacyLevel: 'allow',
        });
    </script>
</head>
<body>
    <div id="app"></div>
</body>
</html>
```

Rebuild the frontend:
```bash
cd vue-frontend
npm install
npm run build
cd ../node-server
cp -r ../vue-frontend/dist ./dist
npm install
```

---

### **2. Node.js Server**

#### **File: `node-server/package.json`**
Add the `dd-trace` dependency to enable Datadog APM:

```json
{
  "name": "node-server",
  "version": "1.0.0",
  "main": "server.js",
  "scripts": {
    "start": "node server.js"
  },
  "dependencies": {
    "axios": "^1.7.9",
    "dd-trace": "^5.30.0",
    "express": "^4.18.2"
  }
}
```

Install dependencies:
```bash
npm install
```

#### **File: `node-server/server.js`**
Initialize Datadog Tracing in the Node.js server:

```javascript
const tracer = require('dd-trace').init({
    service: 'node-server',
    env: process.env.NODE_ENV || 'dev',
    hostname: process.env.DD_AGENT_HOST || 'localhost',
    version: '1.2',
    logInjection: true,
});

const express = require('express');
const axios = require('axios');
const path = require('path');

const app = express();
const PORT = 3000;

// Determine the backend API base URL
const BACKEND_API = process.env.BACKEND_API || 'http://localhost:8080';
console.log(`Backend API URL: ${BACKEND_API}`);

// Proxy API requests to Spring Boot
app.use('/api', async (req, res) => {
    try {
        const response = await axios({
            method: req.method,
            url: `${BACKEND_API}${req.originalUrl}`,
            data: req.body,
            headers: req.headers,
        });
        res.status(response.status).send(response.data);
    } catch (error) {
        console.error('Error while proxying request:', error.message);
        res.status(error.response ? error.response.status : 500).send(error.message);
    }
});

// Serve Vue.js static files
app.use(express.static(path.join(__dirname, 'dist')));

// Handle SPA routing
app.get('*', (req, res) => {
    res.sendFile(path.join(__dirname, 'dist', 'index.html'));
});

app.listen(PORT, () => {
    console.log(`Node.js server is running at http://localhost:${PORT}`);
});
```

---

### **3. Spring Boot Backend**

#### **File: `spring-backend/src/main/java/com/datadoghq/pej/springbackend/controller/ApiController.java`**
Enable cross-origin requests from the frontend:
```java
package com.datadoghq.pej.springbackend.controller;

import com.datadoghq.pej.springbackend.model.ApiResponse;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api")
public class ApiController {

   @GetMapping("/data")
   public ApiResponse getData() {
      return new ApiResponse("Hello from Spring Boot API!");
   }
}
```

Build the artifact:
```bash
./gradlew build
```


---

### **4. Bootsrap the application**

Start the Datadog Agent
(Make sure to use a valid Datadog API key)
```bash
docker run -d --name dd-agent-dogfood-jmx -v /var/run/docker.sock:/var/run/docker.sock:ro -v /proc/:/host/proc/:ro -v /sys/fs/cgroup/:/host/sys/fs/cgroup:ro -p 8126:8126 -p 8125:8125/udp -e DD_API_KEY=your_api_key -e DD_APM_ENABLED=true -e DD_APM_NON_LOCAL_TRAFFIC=true -e DD_PROCESS_AGENT_ENABLED=true -e DD_DOGSTATSD_NON_LOCAL_TRAFFIC="true" -e DD_LOG_LEVEL=debug -e DD_LOGS_ENABLED=true -e DD_LOGS_CONFIG_CONTAINER_COLLECT_ALL=true -e DD_CONTAINER_EXCLUDE_LOGS="name:datadog-agent" gcr.io/datadoghq/agent:latest-jmx
```

Run the backend with Datadog APM in a terminal:

```bash
cd spring-backend
java -javaagent:dd-java-agent.jar -Ddd.service=springbackend -Ddd.env=dev -Ddd.version=1.2 -Ddd.trace.sample.rate=1 -Ddd.logs.injection=true -Ddd.profiling.enabled=true -XX:FlightRecorderOptions=stackdepth=256 -Ddd.tags=env:dev -jar build/libs/spring-backend.jar
```

Run the frontend with Datadog APM in a second terminal:
```bash
cd ../node-server
npm start
```

Run requests in a third terminal or use a browser using `http://localhost:3000` 
```bash
curl http://localhost:3000/api/data
```

You should receive this message:
```bash
TBD
```

### **5. Checking the results**

TBD

---

## **Instrumentation for Dockerized Setup**

### **Docker Compose**

Update the `docker-compose.yml` file to include Datadog Agent, Node.js server, and Spring Boot backend:

```yaml
version: '3.8'

services:
  dd-agent:
    container_name: dd-agent
    image: gcr.io/datadoghq/agent:latest-jmx
    environment:
      - DD_API_KEY=your_api_key
      - DD_APM_ENABLED=true
      - DD_APM_NON_LOCAL_TRAFFIC=true
      - DD_PROCESS_AGENT_ENABLED=true
      - DD_DOGSTATSD_NON_LOCAL_TRAFFIC="true"
      - DD_LOG_LEVEL=debug
      - DD_LOGS_ENABLED=true
      - DD_LOGS_CONFIG_CONTAINER_COLLECT_ALL=true
      - DD_CONTAINER_EXCLUDE_LOGS="name:datadog-agent"
      - SD_JMX_ENABLE=true
    ports:
      - "8125:8125"
      - "8126:8126"
    volumes:
      - /proc/:/host/proc/:ro
      - /sys/fs/cgroup/:/host/sys/fs/cgroup:ro
      - /var/run/docker.sock:/var/run/docker.sock:ro
    networks:
      - app

  spring-backend:
    build:
      context: .
      dockerfile: spring-backend/Dockerfile
    container_name: spring-backend
    environment:
      - JAVA_TOOL_OPTIONS=-javaagent:/root/dd-java-agent.jar -Ddd.agent.host=dd-agent -Ddd.service=springbackend -Ddd.env=dev -Ddd.version=1.2 -Ddd.trace.sample.rate=1 -Ddd.logs.injection=true -Ddd.profiling.enabled=true -XX:FlightRecorderOptions=stackdepth=256 -Ddd.tags=env:dev
    ports:
      - "8080:8080"
    volumes:
      - ./spring-backend/dd-java-agent.jar:/root/dd-java-agent.jar
    networks:
      - app

  node-server:
    build:
      context: .
      dockerfile: node-server/Dockerfile
    container_name: node-server
    ports:
      - "3000:3000"
    depends_on:
      - spring-backend
    environment:
      - BACKEND_API=http://spring-backend:8080
      - DD_AGENT_HOST=dd-agent
    networks:
      - app

networks:
  app:
    driver: bridge
    name: app
```

---

### **Node.js Dockerfile**

Ensure the `node-server` Dockerfile includes the `dd-trace` dependency (=> `node-server/package.json` file):

```dockerfile
# Stage 1: Build the Vue.js frontend
FROM node:22 AS vue-builder

# Set working directory for Vue.js build
WORKDIR /app

# Copy the Vue.js source code into the build context
COPY vue-frontend/package*.json ./vue-frontend/
RUN cd vue-frontend && npm install

COPY vue-frontend/ ./vue-frontend/

# Build Vue.js static files with the environment variable
RUN cd vue-frontend && npm run build

# Stage 2: Set up the Node.js server
FROM node:22

# Set working directory for Node.js server
WORKDIR /app

# Copy Node.js server dependencies (The package.json file include dd-trace)
COPY node-server/package*.json ./
RUN npm install

# Copy the Node.js server code
COPY node-server/ ./

# Copy the Vue.js build output from the previous stage
COPY --from=vue-builder /app/vue-frontend/dist ./dist

# Expose the Node.js server port
EXPOSE 3000

# Start the Node.js server
CMD ["npm", "start"]
```


### **Spring Boot Dockerfile**

Nothing to do at the Dockerfile level as the instrumentation details are defined in the `docker-compose.yml` file. 

---

### **Testing the Setup**

1. Start the entire stack:
   ```bash
   docker-compose up --build -d
   ```
2. Test the endpoint:
   ```bash
   curl http://localhost:3000/api/data
   ```

