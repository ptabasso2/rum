
# Full stack project: Vue.js Frontend, Node.js Server, and Spring Boot Backend

This guide provides detailed instructions to set up a full-stack project with:
- **Vue.js** frontend served using a **Node.js** server.
- **Spring Boot** backend providing the API.

---

## **1. Prerequisites**

### **1.1 Install Node.js and npm**
- **For Linux**:
   ```bash
   curl -fsSL https://deb.nodesource.com/setup_18.x | bash -
   apt-get install -y nodejs
   ```
- **For macOS** (via Homebrew):
   ```bash
   brew install node
   ```
- **For Windows**:
   - Download the installer from [https://nodejs.org/](https://nodejs.org/) and follow the setup wizard.

Verify installation:
```bash
node -v
npm -v
```

---

### **1.2 Install Vue CLI**
Install Vue CLI globally to set up Vue.js projects:
```bash
npm install -g @vue/cli
```
Verify the installation:
```bash
vue --version
```

---

## **2. Project Structure**

The final directory structure will look like this:

```
rum/
├── vue-frontend/        # Vue.js Frontend
├── node-server/         # Node.js Server to serve Vue.js and proxy requests
└── spring-backend/      # Spring Boot Backend
```

---

## **3. Vue.js Frontend Setup**

### **Directory Structure**
```
vue-frontend/
├── dist/                     # Production build folder
├── node_modules/
├── package.json
├── public/
│   └── index.html
└── src/
    ├── App.vue
    ├── components/
    │   └── HelloWorld.vue
    └── main.js
```

### **Steps to Set Up Vue.js Frontend**
1. Navigate to the `vue-frontend` directory:
   ```bash
   cd rum/vue-frontend
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

3. Run the development server (optional):
   ```bash
   npm run serve
   ```

4. Build the production files:
   ```bash
   npm run build
   ```

The build output will be in the `dist/` folder.

---

## **4. Node.js Server Setup**

### **Directory Structure**
```
node-server/
├── dist/                    # Copy Vue.js build output here
├── node_modules/
├── package.json
├── package-lock.json
└── server.js
```

### **Steps to Set Up Node.js Server**
1. Navigate to the `node-server` directory:
   ```bash
   cd rum/node-server
   ```

2. Copy the Vue.js `dist` folder:
   ```bash
   cp -r ../vue-frontend/dist ./dist
   ```

3. Install dependencies:
   ```bash
   npm install
   ```

4. Start the server:
   ```bash
   npm start
   ```

5. Verify the server is running:
   - Open `http://localhost:3000` to see the Vue.js app.
   - The `/api/data` endpoint proxies requests to the Spring Boot backend.

---

## **5. Spring Boot Backend Setup**

### **Directory Structure**
```
spring-backend/
├── build.gradle.kts
├── settings.gradle.kts
└── src/
    ├── main/
    │   ├── java/com/datadoghq/pej/springbackend/
    │   │   ├── BackendApplication.java
    │   │   ├── controller/ApiController.java
    │   │   └── model/ApiResponse.java
    │   └── resources/
    │       └── application.properties
```

### **Steps to Set Up Spring Boot Backend**

1. Navigate to the `spring-backend` directory:
   ```bash
   cd rum/spring-backend
   ```

2. Build the fat JAR:
   ```bash
   ./gradlew bootJar
   ```

3. Run the application:
   ```bash
   java -jar build/libs/spring-backend.jar
   ```

4. Verify the backend:
   - Open `http://localhost:8080/api/data` to ensure the API returns:
     ```json
     {"message": "Hello from Spring Boot API!"}
     ```

---

## **6. Testing the Complete Setup**

1. **Start Spring Boot Backend**:
   - Run the backend JAR:
     ```bash
     java -jar build/libs/spring-backend.jar
     ```

2. **Start Node.js Server**:
   - Start the Node.js service:
     ```bash
     npm start
     ```

3. **Verify the Vue.js Frontend**:
   - Open `http://localhost:3000` in the browser to see the Vue.js app.

4. **Test the API Proxy**:
   - Click the "Fetch Data" button on the Vue.js app.
   - Verify that the data is fetched from the Spring Boot backend and displayed:
     ```
     Hello from Spring Boot API!
     ```

5. **Verify with `curl`**:
   Run the following command to hit the Node.js service:
   ```bash
   curl http://localhost:3000/api/data
   ```
   You should get:
   ```json
   {"message": "Hello from Spring Boot API!"}
   ```

---

## **7. Common Issues and Fixes**

### **1. Missing Modules (e.g., `axios` or `express`)**
If you encounter errors like:
```
Error: Cannot find module 'axios'
```
Run the following command in the respective directory:
```bash
npm install axios
```
This ensures `axios` is installed locally even if it is already in the `package.json`.

### **2. Missing `node_modules`**
If the `node_modules` directory is missing:
```bash
npm install
```

### **3. Port Conflicts**
If any service fails to start because of port conflicts:
- **Spring Boot**: Change the port in `application.properties`:
   ```properties
   server.port=8081
   ```
- **Node.js**: Change the port in `server.js`:
   ```javascript
   const PORT = 3001;
   ```

---

## **8. Summary of Commands**

### **Vue.js Frontend**:
- Install: `npm install`
- Build: `npm run build`
- Serve Dev: `npm run serve`

### **Node.js Server**:
- Install: `npm install`
- Start: `npm start`

### **Spring Boot Backend**:
- Build: `./gradlew bootJar`
- Run: `java -jar build/libs/spring-backend.jar`

---

