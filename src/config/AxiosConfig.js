import axios from 'axios';


const token = 
const axiosInstance = axios.create({
  baseURL: 'https://your-api-url.com',
  headers: {
    'Authorization': `Bearer ${yourToken}`,
    'Content-Type': 'application/json'
  }
});

export default axiosInstance;

