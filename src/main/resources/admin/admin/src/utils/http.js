import axios from 'axios'
import router from '@/router/router-static'
import storage from '@/utils/storage'

const http = axios.create({
    timeout: 1000 * 86400,
    withCredentials: true,
    baseURL: '/springboot142f7',
    headers: {
        'Content-Type': 'application/json; charset=utf-8'
    }
})

// 请求拦截
http.interceptors.request.use(config => {
    const token = storage.get('Token');
    console.log('=== 前端Token调试 ===');
    console.log('请求URL:', config.url);
    console.log('从storage获取的Token:', token);

    if (token) {
        config.headers['Token'] = token;
        console.log('设置请求头Token:', token);
    } else {
        console.log('Token为空，未设置请求头');
    }

    return config;
}, error => {
    return Promise.reject(error);
})

// 响应拦截
http.interceptors.response.use(response => {
    if (response.data && response.data.code === 401) { // 401, token失效
        console.log('Token失效，跳转登录页');
        router.push({ name: 'login' });
    }
    return response;
}, error => {
    console.log('HTTP请求错误:', error);
    return Promise.reject(error);
})

export default http;