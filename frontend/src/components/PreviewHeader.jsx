import {Avatar, Box, CardContent, IconButton, Typography} from "@mui/material";
import MoreIcon from "@mui/icons-material/MoreVert";
export default function PreviewHeader({username, profilePicture, date}) {
        return (
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
                        <Avatar alt={username} src={profilePicture}/>
                        <Typography align="left" variant="h5" marginLeft="1.5%" marginBottom="0px">
                            {username}
                        </Typography>
                    </Box>
                    <Typography variant="h6" align="left">
                        {date}
                    </Typography>
                    <IconButton
                        size="large"
                        edge="end"
                        aria-label="post menu"
                    >
                        <MoreIcon fontSize="large"/>
                    </IconButton>
                </Box>
            </CardContent>
        );
}