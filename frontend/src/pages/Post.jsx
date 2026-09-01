import {useParams, useLoaderData} from "react-router-dom"
import {Typography, Container, CssBaseline, Toolbar, Divider, Avatar, Box, IconButton, Alert, Slide} from "@mui/material"
import ThumbUpOffAltIcon from '@mui/icons-material/ThumbUpOffAlt';
import ThumbUpAltIcon from '@mui/icons-material/ThumbUpAlt';
import ThumbDownOffAltIcon from '@mui/icons-material/ThumbDownOffAlt';
import ThumbDownAltIcon from '@mui/icons-material/ThumbDownAlt';
import ShareIcon from '@mui/icons-material/Share';
import axios from "axios";
import {useState} from "react";
import PreviewContainer from "/src/components/PreviewContainer";
import Comment from "/src/components/Comment";

export default function Post() {
    const post = useLoaderData();
    const {postURI} = useParams();
    const [userLiked, setUserLiked] = useState(post.userLiked);
    const [userDisliked, setUserDisliked] = useState(post.userDisliked);
    const [rating, setRating] = useState(post.rating);
    const [unauthenticatedErrorShown, setUnauthenticatedErrorShown] = useState(false);


    function handleUnauthenticatedError(){
        setUnauthenticatedErrorShown(true);
        setTimeout(() => {
            setUnauthenticatedErrorShown(false);
        }, 5000);
    }

    function handleLikeClick() {
        if(!localStorage.getItem('token')) {
            handleUnauthenticatedError();
            return;
        }
        if(userLiked) {
                axios.post('http://localhost:8080/post/removeLike', {postURI: postURI})
                .then(() => {
                    setUserLiked(false);
                    setRating(rating - 1);
                })
                .catch((error) => {
                    if(error?.response?.status===401){
                        handleUnauthenticatedError();
                    }
                })
        }
        else {
            axios.post('http://localhost:8080/post/like', {postURI: postURI})
                .then(() => {
                    setUserLiked(true);
                    setRating(rating + 1);
                    if(userDisliked) {
                        setUserDisliked(false);
                        setRating(rating + 2);
                    }
                })
                .catch((error) => {
                    if(error?.response?.status===401){
                        handleUnauthenticatedError();
                    }
                })
        }
    }
    function handleDislikeClick() {
        if(!localStorage.getItem('token')) {
            handleUnauthenticatedError();
            return;
        }
        if(userDisliked) {
            axios.post('http://localhost:8080/post/removeDislike', {postURI: postURI})
                .then((response) => {
                    if(response?.status!==200) {
                        return;
                    }
                    setUserDisliked(false);
                    setRating(rating + 1);
                })
                .catch((error) => {
                    if(error?.response?.status===401){
                        handleUnauthenticatedError();
                    }
                })
        }
        else {
            axios.post('http://localhost:8080/post/dislike', {postURI: postURI})
                .then((response) => {
                    if(response?.status!==200) {
                        return;
                    }
                    setUserDisliked(true);
                    setRating(rating - 1);
                    if(userLiked) {
                        setUserLiked(false);
                        setRating(rating - 2);
                    }
                })
                .catch((error) => {
                    if(error?.response?.status===401){
                        handleUnauthenticatedError();
                    }
                })
        }   
    }

    return (
        <>
            <CssBaseline/>
            <Container maxWidth="md" sx={{marginBottom: '10px', marginTop: '2%', wordBreak: "break-word"}}>
                <Typography variant="h2">
                    {post.title}
                </Typography>
                <Divider sx={{borderBottomWidth: 2}}/>
                <Toolbar>
                    <Avatar alt={post.username} src={post.profilePicture}/>
                    <Typography marginLeft="1.5%" variant="h5">
                        {post.username}
                    </Typography>
                    <Box flexGrow="1"/>
                    <Typography variant="h5">
                        {post.date}
                    </Typography>
                </Toolbar>
                <Divider sx={{borderBottomWidth: 2}}/>
                <Typography marginTop="2%" variant="h6">
                    {post.content}
                </Typography>
                <Divider sx={{borderBottomWidth: 2}}/>
                <Toolbar>
                    <IconButton
                        size="large"
                        aria-label="like"
                        onClick={handleLikeClick}
                    >
                        {userLiked ? <ThumbUpAltIcon fontSize="large"/> : <ThumbUpOffAltIcon fontSize="large"/>}
                    </IconButton>
                    <Typography variant="h4">
                        {rating}
                    </Typography>
                    <IconButton
                        size="large"
                        aria-label="dislike"
                        onClick={handleDislikeClick}
                    >
                        {userDisliked ? <ThumbDownAltIcon fontSize="large"/> : <ThumbDownOffAltIcon fontSize="large"/>}
                    </IconButton>
                    <Box flexGrow="1"/>
                    <IconButton
                        size="large"
                        aria-label="share"
                    >
                        <ShareIcon fontSize="large"/>
                    </IconButton>
                </Toolbar>
                <Divider sx={{borderBottomWidth: 2}}/>
                <Typography marginTop="2%" variant="h4" textAlign="left">
                    Comments:
                </Typography>
                <PreviewContainer>
                    {post.comments.map((comment) => (
                        <Comment key={comment.id} id={comment.id} content={comment.content} ratingConst={comment.rating} date={comment.date} username={comment.username} profilePicture={comment.profilePicture} userLikedConst={comment.userLiked} userDislikedConst={comment.userDisliked} handleUnauthenticatedError={handleUnauthenticatedError}/>
                    ))}
                </PreviewContainer>
            </Container>
            <Slide direction="up" in={unauthenticatedErrorShown} timeout={200} mountOnEnter unmountOnExit>
                <Alert severity="warning"  sx={{position: 'fixed', bottom: '10px', left: '10%', width: '80%', borderRadius: '10px', fontSize: '1.5rem' , justifyContent: 'center', alignItems: 'center', backgroundColor: '#f8c379', color: '#693d00'}}>
                    Please <a href="/login">log in</a> or <a href="/register">create an account</a>
                </Alert>
            </Slide>
        </>
    )
}