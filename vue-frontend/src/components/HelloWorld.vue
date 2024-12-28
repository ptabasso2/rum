<template>
    <div>
        <h1>{{ message }}</h1>
        <button @click="fetchData">Fetch Data from API</button>
    </div>
</template>

<script>
import axios from 'axios';

export default {
    data() {
        return {
            message: 'Hello from Vue.js!',
        };
    },
    methods: {
        async fetchData() {
            try {
              // Base URL to connect to Spring Boot Backend
              const baseURL =
                  process.env.VUE_APP_API_URL || 'http://localhost:8080';
                const response = await axios.get(`${baseURL}/api/data`);
                this.message = response.data.message;
            } catch (error) {
                this.message = 'Error fetching data';
            }
        },
    },
};
</script>
