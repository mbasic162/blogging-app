import {Box, Card, CardContent, CardActionArea, Typography, Avatar, Toolbar, IconButton, Divider} from "@mui/material";
import MoreIcon from "@mui/icons-material/MoreVert"
import ThumbsUpDownIcon from '@mui/icons-material/ThumbsUpDown';
import ShareIcon from '@mui/icons-material/Share';
import {encode} from '/src/utils/UriSanitiser.jsx'

export default function PostPreview() {
    const id = 1
    const title = "This is post's title"
    const rating = 0
    const date = "1/1/2025"
    const username = "Username"
    const postURI = () => {
        if (title.length > 30) {
            return encode(title.substring(0, 30) + "-" + id)
        }
        return encode(title + "-" + id)
    }
    return (
        <Card sx={{marginTop: "5%", marginBottom: "5%", boxShadow: "2px 2px 1px #a7a7a7"}}>
            <CardContent sx={{padding: 0}}>
                <Box sx={{display: 'flex', alignItems: 'center', justifyContent: 'space-between', px: 2, py: 1}}>
                    <Box sx={{
                        width: "calc(100% - 59px)",
                        wordBreak: "break-word",
                        display: "flex",
                        flexWrap: "wrap",
                        alignItems: "center",
                        height: "100%",
                        flexFlow: 1
                    }}>
                        <Avatar alt="Creator's profile picture"/>
                        <Typography align="left" variant="h5" marginLeft="1.5%" marginBottom="0px">
                            {username}
                        </Typography>
                    </Box>
                    <IconButton
                        size="large"
                        edge="end"
                        aria-label="post menu"
                    >
                        <MoreIcon fontSize="large"/>
                    </IconButton>
                </Box>
            </CardContent>
            <Divider sx={{borderBottomWidth: 2}}/>
            <CardContent>
                <Typography align="left" variant="h5">
                    {title}
                </Typography>
            </CardContent>
            <Divider sx={{borderBottomWidth: 2}}/>
            <CardContent sx={{padding: 0}}>
                <Toolbar>
                    <ThumbsUpDownIcon fontSize="large" sx={{marginRight: "20px"}}/>
                    <Typography align="left" variant="h4">
                        {rating}
                    </Typography>
                    <Box flexGrow="1"/>
                    <Typography variant="h6" align="left">
                        {date}
                    </Typography>
                    <IconButton
                        size="large"
                        edge="end"
                        aria-label="share"
                    >
                        <ShareIcon fontSize="large" sx={{marginRight: "2%"}}/>
                    </IconButton>
                </Toolbar>
            </CardContent>
            <CardActionArea sx={{padding: 0}}/>
        </Card>
    );
}