import {useEffect, useState} from 'react'
import {Alert, Slide} from '@mui/material'

export default function InfoAlert() {
    const [message, setMessage] = useState('')

    useEffect(() => {
        let timeout
        function handleInfo(event) {
            setMessage(event.detail)
            clearTimeout(timeout)
            timeout = setTimeout(() => {
                setMessage('')
            }, 5000)
        }

        window.addEventListener('infoAlert', handleInfo)
        return () => {
            window.removeEventListener('infoAlert', handleInfo)
            clearTimeout(timeout)
        }
    }, [])

    return (
        <Slide direction="up" in={Boolean(message)} timeout={200} mountOnEnter unmountOnExit>
            <Alert severity="info" sx={{position: 'fixed', bottom: '10px', left: '10%', width: '80%', borderRadius: '10px', fontSize: '1.5rem', justifyContent: 'center', alignItems: 'center', backgroundColor: "#00adcc", color: "white", zIndex: 9999}}>
                {message}
            </Alert>
        </Slide>
    )
}
