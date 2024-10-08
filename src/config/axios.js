import axios from "axios";

const api = axios.create({
    baseURL: "", 
});

const handleBefore = (config) => {
    const token = localStorage.getItem("token");
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
}

const handleError = (err) => {
    console.log(err);
}

api.interceptors.request.use(handleBefore, handleError);

export default api;