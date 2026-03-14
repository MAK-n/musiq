import axios from 'axios';

const musiqClient = axios.create({
    baseURL: import.meta.env.VITE_API_URL,
});

// console.log('API URL:', import.meta.env.VITE_API_URL);

musiqClient.interceptors.request.use((config) => {
    const token = localStorage.getItem('jwt');
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});

export default musiqClient;