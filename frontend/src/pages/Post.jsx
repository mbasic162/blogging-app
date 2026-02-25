import {useParams} from "react-router-dom"
import {Typography, Container, CssBaseline, Toolbar, Divider, Avatar, Box, IconButton} from "@mui/material"
import ThumbUpOffAltIcon from '@mui/icons-material/ThumbUpOffAlt';
import ThumbUpAltIcon from '@mui/icons-material/ThumbUpAlt';
import ThumbDownOffAltIcon from '@mui/icons-material/ThumbDownOffAlt';
import ThumbDownIcon from '@mui/icons-material/ThumbDown';
import ShareIcon from '@mui/icons-material/Share'

export default function Post() {
    const {postURI} = useParams();
    const title = "This is post's title"
    const content = "This is post's content"
    const rating = 0
    const date = "1/1/2025"
    const username = "Username"
    return (
        <>
            <CssBaseline/>
            <Container maxWidth="md" sx={{marginBottom: '10px', marginTop: '2%', wordBreak: "break-word"}}>
                <Typography variant="h2">
                    {title}
                </Typography>
                <Divider sx={{borderBottomWidth: 2}}/>
                <Toolbar>
                    <Avatar alt="Creator's profile picture"/>
                    <Typography marginLeft="1.5%" variant="h5">
                        {username}
                    </Typography>
                    <Box flexGrow="1"/>
                    <Typography variant="h5">
                        {date}
                    </Typography>
                </Toolbar>
                <Divider sx={{borderBottomWidth: 2}}/>
                <Typography marginTop="2%" variant="h6">
                    {content}
                </Typography>
                <Divider sx={{borderBottomWidth: 2}}/>
                <Toolbar>
                    <IconButton
                        size="large"
                        aria-label="like"
                    >
                        <ThumbUpOffAltIcon fontSize="large"/>
                    </IconButton>
                    <Typography variant="h4">
                        {rating}
                    </Typography>
                    <IconButton
                        size="large"
                        aria-label="dislike"
                    >
                        <ThumbDownOffAltIcon fontSize="large"/>
                    </IconButton>
                    <Box flexGrow="1"/>
                    <IconButton
                        size="large"
                        aria-label="share"
                    >
                        <ShareIcon fontSize="large"/>
                    </IconButton>
                </Toolbar>
            </Container>
        </>
    )
}