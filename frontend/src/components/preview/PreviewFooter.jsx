import {Box, CardActionArea, CardContent, IconButton, Toolbar, Typography} from "@mui/material";
import ThumbsUpDownIcon from '@mui/icons-material/ThumbsUpDown';
import ShareIcon from '@mui/icons-material/Share';
export default function PreviewFooter({rating}) {
    return(
        <>
            <CardContent sx={{padding: 0}}>
                <Toolbar>
                    <ThumbsUpDownIcon fontSize="large" sx={{marginRight: "20px"}}/>
                    <Typography align="left" variant="h4">
                        {rating}
                    </Typography>
                    <Box flexGrow="1"/>
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
        </>
    );
}