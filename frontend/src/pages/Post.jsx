import {useParams, useLoaderData} from "react-router-dom"
import {Typography, Container, CssBaseline, Toolbar, Divider, Avatar, Box, IconButton, Button} from "@mui/material"
import ThumbUpOffAltIcon from '@mui/icons-material/ThumbUpOffAlt';
import ThumbUpAltIcon from '@mui/icons-material/ThumbUpAlt';
import ThumbDownOffAltIcon from '@mui/icons-material/ThumbDownOffAlt';
import ThumbDownAltIcon from '@mui/icons-material/ThumbDownAlt';
import ShareIcon from '@mui/icons-material/Share';
import CommentOutlinedIcon from '@mui/icons-material/CommentOutlined';
import axios from "axios";
import {useState} from "react";
import PreviewContainer from "/src/components/PreviewContainer";
import Comment from "/src/components/Comment";
import CommentReply from "/src/components/CommentReply";

export default function Post() {
    const post = useLoaderData();
    const {postURI} = useParams();
    const [userLiked, setUserLiked] = useState(post.userLiked);
    const [userDisliked, setUserDisliked] = useState(post.userDisliked);
    const [rating, setRating] = useState(post.rating);
    const [replyOpen, setReplyOpen] = useState(false);


    function handleLikeClick() {
        if(!localStorage.getItem('token')) {
            window.dispatchEvent(new Event('unauthenticatedAlert'));
            return;
        }
        if(userLiked) {
                axios.post('/post/removeLike', {postURI: postURI})
                .then(() => {
                    setUserLiked(false);
                    setRating(rating - 1);
                })
        }
        else {
            axios.post('/post/like', {postURI: postURI})
                .then(() => {
                    setUserLiked(true);
                    setRating(rating + 1);
                    if(userDisliked) {
                        setUserDisliked(false);
                        setRating(rating + 2);
                    }
                })
        }
    }
    function handleDislikeClick() {
        if(!localStorage.getItem('token')) {
            window.dispatchEvent(new Event('unauthenticatedAlert'));
            return;
        }
        if(userDisliked) {
            axios.post('/post/removeDislike', {postURI: postURI})
                .then((response) => {
                    if(response?.status!==200) {
                        return;
                    }
                    setUserDisliked(false);
                    setRating(rating + 1);
                })
        }
        else {
            axios.post('/post/dislike', {postURI: postURI})
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
        }   
    }

    function openProfile() {
        window.location.href = `/${post.username}`;
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
                    <Avatar alt={post.username} src={post.profilePicture} onClick={openProfile} sx={{cursor: "pointer"}}/>
                    <Typography marginLeft="1.5%" variant="h5" onClick={openProfile} sx={{cursor: "pointer"}}>
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
                <Toolbar alignItems="center" sx={{justifyContent: "center", marginTop: "2%", marginBottom: "2%"}}>
                    <Button variant="outlined" color="primary" size="large" sx={{fontSize: "1.5rem", paddingRight: "40px", paddingLeft: "40px", backgroundColor: "white"}} onClick={() => setReplyOpen(!replyOpen)}>
                        <CommentOutlinedIcon color="primary" fontSize="large" sx={{ pr: "5px"}}/>
                        Leave a comment
                    </Button>
                </Toolbar>
                {replyOpen ? <CommentReply parentPostId={post.id}/> : null}
                <PreviewContainer>
                    {post.comments.map((comment) => (
                        <Comment key={comment.id} id={comment.id} content={comment.content} ratingConst={comment.rating} date={comment.date} username={comment.username} profilePicture={comment.profilePicture} userLikedConst={comment.userLiked} userDislikedConst={comment.userDisliked}/>
                    ))}
                </PreviewContainer>
            </Container>
        </>
    )
}