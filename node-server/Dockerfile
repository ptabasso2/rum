# Stage 1: Build the Vue.js frontend
FROM node:22 AS vue-builder

# Set working directory for Vue.js build
WORKDIR /app

# Copy the Vue.js source code into the build context
COPY vue-frontend/package*.json ./vue-frontend/
RUN cd vue-frontend && npm install

COPY vue-frontend/ ./vue-frontend/

# Accept build argument for VUE_APP_API_URL
ARG VUE_APP_API_URL

# Build Vue.js static files with the environment variable
RUN cd vue-frontend && npm run build

# Stage 2: Set up the Node.js server
FROM node:22

# Set working directory for Node.js server
WORKDIR /app

# Copy Node.js server dependencies
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
