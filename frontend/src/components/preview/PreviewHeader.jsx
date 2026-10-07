import {useState} from "react";
import {Avatar, Box, CardContent, IconButton, Menu, MenuItem, Typography} from "@mui/material";
import MoreIcon from "@mui/icons-material/MoreVert";
import axios from "axios";

export default function PreviewHeader({username, profilePicture, date, onEdit}) {
    const [anchorEl, setAnchorEl] = useState(null);
    const currentUser = JSON.parse(localStorage.getItem('user') || 'null');
    const canEdit = onEdit && currentUser?.username === username;

    function openProfile() {
        window.location.assign(`/${username}`);
    }

    function openMenu(event) {
        setAnchorEl(event.currentTarget);
    }

    function closeMenu() {
        setAnchorEl(null);
    }

    function handleEdit() {
        closeMenu();
        onEdit();
    }

    function handleBlock() {
        closeMenu();
        axios.post("/user/block", {username: username})
        .then(() => {
            window.dispatchEvent(new CustomEvent('successAlert', {detail: 'User blocked successfully'}))
        });
    }

    return (
        <CardContent sx={{padding: 0}}>
            <Box sx={{display: 'flex', alignItems: 'center', justifyContent: 'space-between', px: 2, py: 1}}>
                <Box
                    sx={{
                    width: "calc(100% - 59px)",
                    wordBreak: "break-word",
                    display: "flex",
                    flexWrap: "wrap",
                    alignItems: "center",
                    height: "100%",
                    flexFlow: 1
                }}>
                    <Avatar alt={username} src={profilePicture} onClick={openProfile} sx={{cursor: "pointer"}}/>
                    <Typography align="left" variant="h5" marginLeft="1.5%" marginBottom="0px" onClick={openProfile} sx={{cursor: "pointer"}}>
                        {username}
                    </Typography>
                </Box>
                <Typography variant="h6" align="left" minWidth="fit-content">
                    {date}
                </Typography>
                <IconButton
                    size="large"
                    edge="end"
                    aria-label="menu"
                    onClick={openMenu}
                >
                    <MoreIcon fontSize="large"/>
                </IconButton>
            </Box>
            <Menu anchorEl={anchorEl} open={Boolean(anchorEl)} onClose={closeMenu}>
                {canEdit && <MenuItem onClick={handleEdit}>Edit</MenuItem>}
                <MenuItem onClick={handleBlock}>Block user</MenuItem>
            </Menu>
        </CardContent>
    );
}