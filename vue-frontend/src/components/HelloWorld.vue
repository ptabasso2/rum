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
                const response = await axios.get('http://localhost:8080/api/data');
                this.message = response.data.message;
            } catch (error) {
                this.message = 'Error fetching data';
            }
        },
    },
};
</script>
