import {useEffect, useState} from 'react'
import {Alert, Slide} from '@mui/material'

export default function SuccessAlert() {
    const [message, setMessage] = useState('')

    useEffect(() => {
        let timeout
        function handleSuccess(event) {
            setMessage(event.detail)
            clearTimeout(timeout)
            timeout = setTimeout(() => {
                setMessage('')
            }, 5000)
        }

        window.addEventListener('successAlert', handleSuccess)
        return () => {
            window.removeEventListener('successAlert', handleSuccess)
            clearTimeout(timeout)
        }
    }, [])

    return (
        <Slide direction="up" in={Boolean(message)} timeout={200} mountOnEnter unmountOnExit>
            <Alert severity="success" sx={{position: 'fixed', bottom: '10px', left: '10%', width: '80%', borderRadius: '10px', fontSize: '1.5rem', justifyContent: 'center', alignItems: 'center', backgroundColor: '#70b972', color: '#ffffff', zIndex: 9999}}>
                {message}
            </Alert>
        </Slide>
    )
}