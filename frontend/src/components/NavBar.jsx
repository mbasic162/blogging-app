import {useState, useEffect} from 'react'
import AppBar from '@mui/material/AppBar'
import Box from '@mui/material/Box'
import Toolbar from '@mui/material/Toolbar'
import IconButton from '@mui/material/IconButton'
import MenuItem from '@mui/material/MenuItem'
import Menu from '@mui/material/Menu'
import SearchIcon from '@mui/icons-material/Search'
import { Avatar, TextField } from '@mui/material'
import logo from '../assets/logo.svg'

export default function NavBar() {
    const [user, setUser] = useState(null)
    const [anchorEl, setAnchorEl] = useState(null)

    useEffect(() => {
        const storedUser = localStorage.getItem('user')
        if (storedUser) setUser(JSON.parse(storedUser))
    }, [])

    useEffect(() => {
        const handleLogout = () => setUser(null)
        window.addEventListener('unauthenticatedAlert', handleLogout)
        return () => window.removeEventListener('unauthenticatedAlert', handleLogout)
    }, [])

    useEffect(() => {
        const handleUserUpdate = (event) => setUser(event.detail)
        window.addEventListener('userUpdated', handleUserUpdate)
        return () => window.removeEventListener('userUpdated', handleUserUpdate)
    }, [])

    const openMenu = (e) => setAnchorEl(e.currentTarget)
    const closeMenu = () => setAnchorEl(null)

    function handleOpenProfile() {
        closeMenu()
        window.location.href = `/${user.username}`
    }

    function handleLogout() {
        closeMenu()
        localStorage.removeItem('token')
        localStorage.removeItem('user')
        setUser(null)
        window.location.href = '/'
    }

    function handleEditProfile() {
        closeMenu()
        window.location.href = '/edit-profile'
    }

    return (
        <Box>
            <AppBar position="fixed" sx={{backgroundColor: 'white', color: 'black', borderBottom: '1px solid #4F4F4F', boxShadow: 'none'}}>
                <Toolbar>
                    <Box sx={{width: 130, height: 60, cursor: 'pointer'}} onClick={() => {window.location.href = '/'}}>
                        <Box component="img" src={logo} alt="Logo" sx={{width: '100%', height: '100%', objectFit: 'fill', display: 'block'}} />
                    </Box>
                    <Box sx={{flexGrow: 1}} />
                    <Box sx={{display: 'flex', alignItems: 'center', gap: 1, width: '70%', maxWidth: 500, padding: '8px', borderRadius: '4px'}}>
                        <SearchIcon sx={{color: '#999'}} />
                        <TextField variant="outlined" placeholder="Search..." size="small" sx={{flex: 1}} />
                    </Box>
                    <Box sx={{flexGrow: 1}} />
                    <IconButton size="large" edge="end" onClick={openMenu} color="inherit">
                        <Avatar src={user?.profilePicture} />
                    </IconButton>
                </Toolbar>
            </AppBar>
            <Toolbar />
            <Menu anchorEl={anchorEl} open={Boolean(anchorEl)} onClose={closeMenu}>
                {
                    user &&
                    [
                        <MenuItem key="0" onClick={handleOpenProfile}>Profile</MenuItem>,
                        <MenuItem key="1" onClick={handleEditProfile}>Edit profile</MenuItem>,
                        <MenuItem key="2" onClick={handleLogout}>Logout</MenuItem>
                    ]
                }
                {
                    !user &&
                    [
                        <MenuItem key="3" onClick={() => {closeMenu(); window.location.href = '/login'}}>Login</MenuItem>,
                    ]
                }
            </Menu>
        </Box>
    )
}