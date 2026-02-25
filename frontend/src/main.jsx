import {StrictMode} from 'react'
import {createRoot} from 'react-dom/client'
import {CssBaseline, ThemeProvider} from '@mui/material'
import App from './App.jsx'
import './index.css'
import NavBar from './components/NavBar.jsx'
import theme from './theme.jsx'

createRoot(document.getElementById('root')).render(
    <StrictMode>
        <ThemeProvider theme={theme}>
            <CssBaseline/>
            <NavBar/>
            <App/>
        </ThemeProvider>
    </StrictMode>
)