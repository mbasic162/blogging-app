import {Card, Divider, Typography, Toolbar, IconButton, Box} from "@mui/material";
import {encode} from '/../utils/UriSanitiser.jsx'
import PreviewHeader from "./PreviewHeader.jsx";
import PreviewBody from "./PreviewBody.jsx";
import ThumbUpOffAltIcon from '@mui/icons-material/ThumbUpOffAlt';
import ThumbUpAltIcon from '@mui/icons-material/ThumbUpAlt';
import ThumbDownOffAltIcon from '@mui/icons-material/ThumbDownOffAlt';
import ThumbDownAltIcon from '@mui/icons-material/ThumbDownAlt'
import ShareIcon from '@mui/icons-material/Share';
import { useState } from "react";
import axios from "axios";

export default function Comment({id, content, ratingConst, date, username, profilePicture, userLikedConst, userDislikedConst, handleUnauthenticatedError}) {
    const formattedDate = new Date(date).toLocaleDateString();
    const [userLiked, setUserLiked] = useState(userLikedConst);
    const [userDisliked, setUserDisliked] = useState(userDislikedConst);
    const [rating, setRating] = useState(ratingConst);


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
            handleUnauthenticatedError();
            return;
        }
        if(userLiked) {
                axios.post('http://localhost:8080/comment/removeLike', {commentURI: commentURI()})
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
            axios.post('http://localhost:8080/comment/like', {commentURI: commentURI()})
                .then(() =>{
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
            axios.post('http://localhost:8080/comment/removeDislike', {commentURI: commentURI()})
                .then((response) => {
                    if(response?.status!==200) {
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
            axios.post('http://localhost:8080/comment/dislike', {commentURI: commentURI()})
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
        <Card sx={{marginTop: "5%", marginBottom: "5%", boxShadow: "2px 2px 1px #a7a7a7"}}>
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
                <IconButton
                    size="large"
                    aria-label="share"
                >
                    <ShareIcon fontSize="large"/>
                </IconButton>
            </Toolbar>
        </Card>
    );
}