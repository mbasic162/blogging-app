import {Card, Divider, Typography, Toolbar, IconButton, Box, Button} from "@mui/material";
import {encode} from '/src/utils/UriSanitiser.jsx'
import PreviewHeader from "/src/components/preview/PreviewHeader.jsx";
import PreviewBody from "/src/components/preview/PreviewBody.jsx";
import CommentReply from "/src/components/CommentReply.jsx";
import ThumbUpOffAltIcon from '@mui/icons-material/ThumbUpOffAlt';
import ThumbUpAltIcon from '@mui/icons-material/ThumbUpAlt';
import ThumbDownOffAltIcon from '@mui/icons-material/ThumbDownOffAlt';
import ThumbDownAltIcon from '@mui/icons-material/ThumbDownAlt'
import ShareIcon from '@mui/icons-material/Share';
import CommentOutlinedIcon from '@mui/icons-material/CommentOutlined';
import { useState } from "react";
import axios from "axios";

export default function Comment({id, content, ratingConst, comments = [],date, username, profilePicture, userLikedConst, userDislikedConst, depth = 0}) {
    const formattedDate = new Date(date).toLocaleDateString();
    const visualDepth = Math.min(depth, 5);
    const [userLiked, setUserLiked] = useState(userLikedConst);
    const [userDisliked, setUserDisliked] = useState(userDislikedConst);
    const [rating, setRating] = useState(ratingConst);
    const [replyOpen, setReplyOpen] = useState(false);

    const commentURI = () => {
        if (content.length > 30 && content[30] !== ' ') {
            return encode(content.substring(0, 31) + "-" + id)
        }
        else if (content.length > 30) {
            return encode(content.substring(0, 30) + "-" + id)
        }
        return encode(content + "-" + id)
    }
    function handleLikeClick() {
        if(!localStorage.getItem('token')) {
            window.dispatchEvent(new Event('unauthenticatedAlert'));
            return;
        }
        if(userLiked) {
                axios.post('/comment/removeLike', {commentURI: commentURI()})
                .then(() => {
                    setUserLiked(false);
                    setRating(rating - 1);
                })
        }
        else {
            axios.post('/comment/like', {commentURI: commentURI()})
                .then(() =>{
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
            axios.post('/comment/removeDislike', {commentURI: commentURI()})
                .then((response) => {
                    if(response?.status!==200) {
                    }
                    setUserDisliked(false);
                    setRating(rating + 1);
                })
        }
        else {
            axios.post('/comment/dislike', {commentURI: commentURI()})
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

    return (
        <>
            <Box sx={{ ml: visualDepth === 0 ? 0 : { xs: 1, sm: 3 }, pl: visualDepth === 0 ? 0 : { xs: 1, sm: 2 }, borderLeft: visualDepth === 0 ? "none" : "3px solid", borderColor: "#6f6f6f"}}>
                <Card sx={{marginTop: "5%", marginBottom: "20px", boxShadow: "2px 2px 1px #a7a7a7"}}>
                    <PreviewHeader username={username} profilePicture={profilePicture} date={formattedDate}/>
                    <Divider sx={{borderBottomWidth: 2}}/>
                    <PreviewBody body={content}/>
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
                        <Button variant="outlined" color="primary" size="large" sx={{fontSize: "1.5rem", paddingRight: "40px", paddingLeft: "40px"}} onClick={() => setReplyOpen(!replyOpen)}>
                            <CommentOutlinedIcon color="primary" fontSize="large" sx={{ pr: "5px"}}/>
                            Reply
                        </Button>
                        <Box flexGrow="1"/>
                        <IconButton
                            size="large"
                            aria-label="share"
                        >
                            <ShareIcon fontSize="large"/>
                        </IconButton>
                    </Toolbar>
                </Card>
                {replyOpen && <CommentReply parentCommentId={id}/>}
                {comments.map((childComment) => (
                    <Comment
                        key={childComment.id}
                        id={childComment.id}
                        content={childComment.content}
                        comments={childComment.comments}
                        ratingConst={childComment.rating}
                        date={childComment.date}
                        username={childComment.username}
                        profilePicture={childComment.profilePicture}
                        userLikedConst={childComment.userLiked}
                        userDislikedConst={childComment.userDisliked}
                        depth={depth + 1}/>
                ))}
            </Box>
        </>
    );
}