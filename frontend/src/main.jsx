import {StrictMode} from 'react'
import {createRoot} from 'react-dom/client'
import {CssBaseline, ThemeProvider} from '@mui/material'
import App from './App.jsx'
import './index.css'
import NavBar from './components/NavBar.jsx'
import theme from './theme.jsx'
import axios from 'axios'
import UnauthenticatedAlert from './components/alerts/UnauthenticatedAlert.jsx'
import SuccessAlert from './components/alerts/SuccessAlert.jsx'

axios.defaults.baseURL = 'http://localhost:8080'
axios.defaults.headers.post['Content-Type'] = 'application/x-www-form-urlencoded'
axios.interceptors.request.use((config) => {
    const token = localStorage.getItem('token')
    if (token) {
        config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
})
axios.interceptors.response.use(
    (response) => {
        return response
    },
    (error) => {
        if (error?.response?.status === 401) {
            localStorage.removeItem('token')
            localStorage.removeItem('user')
            window.dispatchEvent(new Event('unauthenticatedAlert'));
        }
        return Promise.reject(error)
    }   
)

createRoot(document.getElementById('root')).render(
    <StrictMode>
        <ThemeProvider theme={theme}>
            <CssBaseline/>
            <NavBar/>
            <App/>
            <UnauthenticatedAlert/>
            <SuccessAlert/>
        </ThemeProvider>
    </StrictMode>
)