import axios from "axios";

const api = axios.create({
    baseURL: "http://localhost:8082/api/", 
});

//làm một hành động gì đó khi call api
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