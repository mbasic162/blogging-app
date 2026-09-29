import { Avatar, Box, Button, Card, Container, Typography} from '@mui/material'
import { useEffect, useState } from 'react'
import axios from 'axios'
import ChangeUsername from '../components/forms/ChangeUsername'
import ChangeEmail from '../components/forms/ChangeEmail'
import ChangeDescription from '../components/forms/ChangeDescription'
import ChangePassword from '../components/forms/ChangePassword'
import ChangePrivate from '../components/forms/ChangePrivate'
import DeleteAccount from '../components/dialogs/DeleteAccount'
import ChangeProfilePicture from '../components/forms/ChangeProfilePicture'
import PermanentlyDeleteAccount from '../components/dialogs/PermanentlyDeleteAccount'
export default function Settings() {
    let [profile, setProfile] = useState(null)
    let [blockedUsers, setBlockedUsers] = useState([])
    useEffect(() => {
        if(localStorage.getItem('token')){
            axios.get("/user/editProfileDto").then((res) => {
                setProfile(res.data)
                setBlockedUsers(res.data.blockedUsers)
            })
        }
    }, [])

    function handleUnblock(username) {
        axios.post("/user/unblock", {username: username}).then(() => {
            setBlockedUsers(blockedUsers.filter((user) => user.username !== username))
        })
    }

    return(
        <Container maxWidth="md" height="100%">
            <Card sx={{display: "flex", flexDirection: "column", marginTop: "5%", marginBottom: "5%",boxShadow: "2px 2px 1px #a7a7a7", padding: "5%", height: "100%"}}>
                {profile &&
                <>
                    <Typography textAlign="left" variant="h4" marginBottom="5%">
                        Current Profile:
                    </Typography>
                    { blockedUsers.length > 0 &&
                        <Typography textAlign="left" variant="h5" marginBottom="5%">
                            Blocked Users:
                        </Typography>
                    }
                    {blockedUsers.map((blockedUser) => (
                        <Card key={blockedUser.id}  sx={{display: "flex", flexDirection: "row", alignItems: "center", marginBottom: "2%", padding: "2%", boxShadow: "2px 2px 1px #a7a7a7"}}>
                            <Avatar src={blockedUser.profilePicture} />
                            <Typography textAlign="left" variant="h4" marginLeft="2%">
                                {blockedUser.username}
                            </Typography>
                            <Box flexGrow={1} />
                            <Button variant="contained" color="error" onClick={() => handleUnblock(blockedUser.username)}>
                                Unblock
                            </Button>
                        </Card>
                    ))}
                    <ChangeProfilePicture user={profile} setUser={setProfile}/>
                    <ChangeDescription user={profile} setUser={setProfile}/>
                    <ChangePrivate user={profile} setUser={setProfile}/>
                    <ChangeUsername user={profile} setUser={setProfile}/>
                    <ChangeEmail user={profile} setUser={setProfile}/>
                    <ChangePassword/>
                    <DeleteAccount/>
                    <Box sx={{marginTop: "20px"}}/>
                    <PermanentlyDeleteAccount/>
                </>
                }
            </Card>
        </Container>
    )
}