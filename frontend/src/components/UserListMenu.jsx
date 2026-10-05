import {Avatar, Box, Button, Card, Dialog, DialogContent, DialogTitle, Typography} from "@mui/material";
import axios from "axios";

export default function UserListDialog({open, onClose, title, users, authUser, setAuthUser}) {
    function isFollowing(username) {
        return authUser?.following?.some((followedUser) => followedUser.username === username);
    }

    function handleFollowToggle(targetUser) {
        if(isFollowing(targetUser.username)) {
            axios.post("/user/unfollow", {username: targetUser.username}).then(() => {
                setAuthUser((currentAuthUser) => {
                    const updatedFollowing = currentAuthUser.following.filter((followedUser) => followedUser.username !== targetUser.username);
                    const updatedAuthUser = {...currentAuthUser, following: updatedFollowing};
                    localStorage.setItem("user", JSON.stringify(updatedAuthUser));
                    setAuthUser(updatedAuthUser);
                    return updatedAuthUser;
                });
            })
        }
        else{
            axios.post("/user/follow", {username: targetUser.username}).then(() => {
                setAuthUser((currentAuthUser) => {
                    const updatedFollowing = [...currentAuthUser.following, targetUser];
                    const updatedAuthUser = {...currentAuthUser, following: updatedFollowing};
                    localStorage.setItem("user", JSON.stringify(updatedAuthUser));
                    setAuthUser(updatedAuthUser);
                    return updatedAuthUser;
                });
            })
        }
    }

    function openProfile(username) {
        window.location.replace(`/${username}`);
    }

    return (
        <Dialog open={open} onClose={onClose} fullWidth maxWidth="sm">
            <DialogTitle>{title}</DialogTitle>
            <DialogContent>
                {users.length === 0 ? (
                    <Typography>No users to show.</Typography>
                ) : (
                    users.map((user) => (
                        <Card key={user.username} sx={{display: "flex", flexDirection: "row", alignItems: "center", marginBottom: "2%", padding: "2%", boxShadow: "2px 2px 1px #a7a7a7"}}>
                            <Avatar src={user.profilePicture} onClick={() => openProfile(user.username)} sx={{cursor: "pointer"}}/>
                            <Typography marginLeft="2%" onClick={() => openProfile(user.username)} sx={{cursor: "pointer"}}>
                                {user.username}
                            </Typography>
                            <Box flexGrow="1"/>
                            {authUser && authUser.username !== user.username && (
                                <Button variant={isFollowing(user.username) ? "outlined" : "contained"} onClick={() => handleFollowToggle(user)}>
                                    {isFollowing(user.username) ? "Unfollow" : "Follow"}
                                </Button>
                            )}
                        </Card>
                    ))
                )}
            </DialogContent>
        </Dialog>
    );
}
