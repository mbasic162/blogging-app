import {useState} from "react";
import {useLoaderData} from "react-router-dom";
import {Avatar, Button, Container, Typography, Box, Grid, CssBaseline, Tab, Divider, IconButton, Menu, MenuItem} from "@mui/material"
import {TabContext, TabList, TabPanel} from "@mui/lab"
import MoreIcon from "@mui/icons-material/MoreVert";
import axios from "axios";
import PreviewContainer from "/src/components/PreviewContainer"
import PostPreview from "/src/components/PostPreview"
import Comment from "/src/components/Comment";
import UserListMenu from "/src/components/UserListMenu";

export default function Profile() {
    const user = useLoaderData();
    const [tabContextValue, setTabContextValue] = useState("Posts");
    const username = user.username;
    const profilePicture = user.profilePicture;
    const description = user.description;
    const posts = user.posts;
    const comments = user.comments;
    const [followers, setFollowers] = useState(user.followers || []);
    const following = user.following || [];
    const [numberOfFollowers, setNumberOfFollowers] = useState(followers ? followers.length : 0);
    const numberOfFollowing = following ? following.length : 0;
    const [isUserBlocked, setIsUserBlocked] = useState(user.isUserBlocked);
    const [anchorEl, setAnchorEl] = useState(null);
    const [userListType, setUserListType] = useState(null);
    const [authUser, setAuthUser] = useState(() => {
        const storedUser = localStorage.getItem('user');
        return storedUser ? JSON.parse(storedUser) : null;
    });
    const [isFollowing, setIsFollowing] = useState(() =>
        authUser?.following?.some((followedUser) => followedUser.username === username)
    );

    function openMenu(event) {
        setAnchorEl(event.currentTarget);
    }

    function closeMenu() {
        setAnchorEl(null);
    }

    function handleShare() {
        closeMenu();
        navigator.clipboard.writeText(window.location.href).then(() => {
            window.dispatchEvent(new CustomEvent('infoAlert', {detail: 'Link copied to clipboard'}));
        });
    }

    function handleBlockToggle() {
        closeMenu();
        if(isUserBlocked){
            axios.post("/user/unblock", {username: username})
                .then(() => {
                    setIsUserBlocked(false);
                    sessionStorage.setItem('pendingSuccessAlert', 'User unblocked successfully');
                    window.location.reload();
                });
        }
        else{
            axios.post("/user/block", {username: username})
                .then(() => {
                    setIsUserBlocked(true);
                    setAuthUser((prevAuthUser) => {
                        const updatedFollowing = prevAuthUser.following.filter((followedUser) => followedUser.username !== username);
                        localStorage.setItem('user', JSON.stringify({...prevAuthUser, following: updatedFollowing}));
                        return {...prevAuthUser, following: updatedFollowing};
                    });
                    sessionStorage.setItem('pendingSuccessAlert', 'User blocked successfully');
                    window.location.reload();
                });
        }
    }

    function handleFollowToggle() {
        if(isFollowing) {
            axios.post("/user/unfollow", {username: username}).then(() => {
                const updatedFollowing = (authUser.following || []).filter((followedUser) => followedUser.username !== username);
                setFollowers((currentFollowers) => currentFollowers.filter((follower) => follower.username !== authUser.username));
                setNumberOfFollowers((currentFollowers) => Math.max(currentFollowers - 1, 0));
                setIsFollowing(false);
                setAuthUser((currentAuthUser) => {
                    const updatedAuthUser = {...currentAuthUser, following: updatedFollowing};
                    localStorage.setItem('user', JSON.stringify(updatedAuthUser));
                    return updatedAuthUser;
                });
                window.dispatchEvent(new CustomEvent('successAlert', {detail: 'User unfollowed successfully'}));
            })
        }
        else{
            axios.post("/user/follow", {username: username}).then(() => {
                const updatedFollowing = [...(authUser.following || []), {
                    username: username,
                    profilePicture: profilePicture
                }];
                setFollowers((currentFollowers) => [...currentFollowers, {username: authUser.username, profilePicture: authUser.profilePicture}]);
                setNumberOfFollowers((currentFollowers) => currentFollowers + 1);
                setIsFollowing(true);
                setAuthUser((currentAuthUser) => {
                    const updatedAuthUser = {...currentAuthUser, following: updatedFollowing};
                    localStorage.setItem('user', JSON.stringify(updatedAuthUser));
                    return updatedAuthUser;
                });
                window.dispatchEvent(new CustomEvent('successAlert', {detail: 'User followed successfully'}));
            })
        }
    }
    function openUserList(type) {
        setUserListType(type);
    }

    function closeUserList() {
        setUserListType(null);
    }

    const handleTabPanelChange = (event, newValue) => {
        setTabContextValue(newValue);
    };
    return (
        <>
            <CssBaseline/>
            <Box paddingBottom="30px"/>
            <Container maxWidth="md" sx={{
                marginBottom: '10px',
                marginTop: '2%',
                wordBreak: "break-word",
                display: "flex",
                flexWrap: "wrap",
                justifyContent: "center",
                position: "relative"
            }}>
                <Avatar alt="profile picture" src={profilePicture} sx={{height: "135px", width: "135px", maxHeight: "33vh", maxWidth: "33vh"}}/>
                <IconButton size="large" aria-label="menu" onClick={openMenu} sx={{position: "absolute", top: 0, right: 0}}>
                    <MoreIcon fontSize="large"/>
                </IconButton>
                <Box flexBasis="100%" paddingBottom="5%"/>
                <Box display="flex" flexDirection="column" alignItems="center" justifyContent="center" width="100%">
                    <Typography variant="h4" sx={{marginBottom: "3%"}}>
                        {username}
                    </Typography>
                    {authUser && authUser.username !== username && (
                        <Button
                            size="large"
                            variant={isFollowing ? "outlined" : "contained"}
                            onClick={handleFollowToggle}
                            disabled={isUserBlocked}
                        >
                            {isUserBlocked ? "Unblock user to follow" : isFollowing ? "Unfollow" : "Follow"}
                        </Button>
                    )}
                </Box>
                <Menu anchorEl={anchorEl} open={Boolean(anchorEl)} onClose={closeMenu}>
                    <MenuItem onClick={handleShare}>Share profile</MenuItem>
                    <MenuItem onClick={handleBlockToggle}>
                        {isUserBlocked ? "Unblock user" : "Block user"}
                    </MenuItem>
                </Menu>
                <Box flexBasis="100%" paddingBottom="5%"/>
                <Grid container spacing={10}>
                    <Grid size="8" border = "1px solid black" borderRadius="5px" padding="10px" justifyContent="center" alignItems="center">
                        <Button variant="text" onClick={() => openUserList("followers")} sx={{textTransform: "none", color: "inherit", display: "flex", flexDirection: "column"}}>
                            <Typography variant="h5">Followers:</Typography>
                            <Typography variant="h5">{numberOfFollowers}</Typography>
                        </Button>
                    </Grid>
                    <Grid size="8" border = "1px solid black" borderRadius="5px" padding="10px" justifyContent="center" alignItems="center">
                        <Button variant="text" onClick={() => openUserList("following")} sx={{textTransform: "none", color: "inherit", display: "flex", flexDirection: "column"}}>
                            <Typography variant="h5">Following:</Typography>
                            <Typography variant="h5">{numberOfFollowing}</Typography>
                        </Button>
                    </Grid>
                </Grid>
                <UserListMenu
                    open={userListType !== null}
                    onClose={closeUserList}
                    title={userListType === "followers" ? "Followers" : "Following"}
                    users={userListType === "followers" ? followers : following}
                    authUser={authUser}
                    setAuthUser={setAuthUser}
                />
                <Box flexBasis="100%" pt="5%" pb="5%" display="flex" alignItems="center" justifyContent="center" flexDirection="column">
                    {description && <Typography variant="h5"> {description} </Typography>}
                </Box>
                <Box width="100%">
                    <TabContext value={tabContextValue}>
                        <TabList onChange={handleTabPanelChange} aria-label="Post or comment selector" centered>
                            <Tab label="Posts" value="Posts"/>
                            <Tab label="Comments" value="Comments"/>
                        </TabList>
                        <Divider/>
                        <TabPanel value="Posts">
                            <PreviewContainer>
                                {posts.map((post) => (
                                    <PostPreview key={post.id} id={post.id} title={post.title} rating={post.rating} date={post.date} username={post.username} profilePicture={post.profilePicture}/>
                                ))}
                            </PreviewContainer>
                        </TabPanel>
                        <TabPanel value="Comments">
                            <PreviewContainer>
                                {comments.map((comment) => (
                                    <Comment key={comment.id} id={comment.id} content={comment.content} comments={comment.comments} ratingConst={comment.rating} date={comment.date} username={comment.username} profilePicture={comment.profilePicture} userLikedConst={comment.userLiked} userDislikedConst={comment.userDisliked} isHiddenConst={comment.isHidden} depth={0}/>
                                ))}
                            </PreviewContainer>
                        </TabPanel>
                    </TabContext>
                </Box>
            </Container>
        </>
    )
}