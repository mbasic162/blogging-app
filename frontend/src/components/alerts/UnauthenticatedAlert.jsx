import {useEffect, useState} from 'react'
import {Alert, Slide} from '@mui/material'

export default function UnauthenticatedAlert() {
    const [shown, setShown] = useState(false)

    useEffect(() => {
        let timeout
        function handleUnauthenticated() {
            setShown(true)
            clearTimeout(timeout)
            timeout = setTimeout(() => {
                setShown(false)
            }, 5000)
        }
        window.addEventListener('unauthenticatedAlert', handleUnauthenticated)
        return () => {
            window.removeEventListener(
                'unauthenticated',
                handleUnauthenticated
            )
            clearTimeout(timeout)
        }
    }, [])

    return (
            <Slide direction="up" in={shown} timeout={200} mountOnEnter unmountOnExit>
                <Alert severity="warning"  sx={{position: 'fixed', bottom: '10px', left: '10%', width: '80%', borderRadius: '10px', fontSize: '1.5rem' , justifyContent: 'center', alignItems: 'center', backgroundColor: '#f8c379', color: '#693d00'}}>
                    Please <a href="/login">log in</a> or <a href="/register">create an account</a>
                </Alert>
            </Slide>
    )
}