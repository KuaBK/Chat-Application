import axios from 'axios';

const API = axios.create({
  baseURL: 'http://localhost:8088',
});

export default API;
