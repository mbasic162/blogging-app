import {useState} from "react";
import {useLoaderData} from "react-router-dom";
import {Avatar, Container, Typography, Box, Grid, CssBaseline, Tab, Divider, Alert, Slide} from "@mui/material"
import {TabContext, TabList, TabPanel} from "@mui/lab"
import PreviewContainer from "/src/components/PreviewContainer"
import PostPreview from "/src/components/PostPreview"
import Comment from "/src/components/Comment";

export default function Profile() {
    const user = useLoaderData();
    const [tabContextValue, setTabContextValue] = useState("Posts");
    const username = user.username;
    const profilePicture = user.profilePicture;
    const description = user.description;
    const posts = user.posts;
    const comments = user.comments;
    const followers = user.followers;
    const following = user.following;
    const numberOfFollowers = followers ? followers.length : 0;
    const numberOfFollowing = following ? following.length : 0;
    const [unauthenticatedErrorShown, setUnauthenticatedErrorShown] = useState(false);

    function handleUnauthenticatedError(){
        setUnauthenticatedErrorShown(true);
        setTimeout(() => {
            setUnauthenticatedErrorShown(false);
        }, 5000);
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
                justifyContent: "center"
            }}>
                <Avatar alt="profile picture" src={profilePicture} sx={{height: "135px", width: "135px", maxHeight: "33vh", maxWidth: "33vh"}}/>
                <Box flexBasis="100%" paddingBottom="5%"/>
                <Typography variant="h4">
                    {username}
                </Typography>
                <Box flexBasis="100%" paddingBottom="5%"/>
                <Grid container spacing={10}>
                    <Grid size="8">
                        <Typography variant="h5">
                            Followers:
                        </Typography>
                        <Typography variant="h5">
                            {numberOfFollowers}
                        </Typography>
                    </Grid>
                    <Grid size="8">
                        <Typography variant="h5">
                            Following:
                        </Typography>
                        <Typography variant="h5">
                            {numberOfFollowing}
                        </Typography>
                    </Grid>
                </Grid>
                <Box flexBasis="100%" paddingBottom="10%"/>
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
                                    <Comment key={comment.id} id={comment.id} content={comment.content} ratingConst={comment.rating} date={comment.date} username={comment.username} profilePicture={comment.profilePicture} userLikedConst={comment.userLiked} userDislikedConst={comment.userDisliked} handleUnauthenticatedError={handleUnauthenticatedError}/>
                                ))}
                            </PreviewContainer>
                        </TabPanel>
                    </TabContext>
                </Box>
            </Container>
            <Slide direction="up" in={unauthenticatedErrorShown} timeout={200} mountOnEnter unmountOnExit>
                <Alert severity="warning"  sx={{position: 'fixed', bottom: '10px', left: '10%', width: '80%', borderRadius: '10px', fontSize: '1.5rem' , justifyContent: 'center', alignItems: 'center', backgroundColor: '#f8c379', color: '#693d00'}}>
                    Please <a href="/login">log in</a> or <a href="/register">create an account</a>
                </Alert>
            </Slide>
        </>
    )
}