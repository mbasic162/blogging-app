import {useState} from "react";
import {useParams} from "react-router-dom";
import {Avatar, Container, Typography, Box, Grid, CssBaseline, Tab, Divider} from "@mui/material"
import {TabContext, TabList, TabPanel} from "@mui/lab"
import ContentContainer from "/src/components/ContentContainer"
import PostPreview from "/src/components/PostPreview"
import Comment from "/src/components/Comment";

export default function Profile() {
    const [value, setValue] = useState("Posts");
    const {username} = useParams();
    const numberOfFollowers = 0;
    const numberOfFollowing = 0;

    const handleChange = (event, newValue) => {
        setValue(newValue);
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
                <Avatar alt="profile picture"
                        sx={{height: "135px", width: "135px", maxHeight: "33vh", maxWidth: "33vh"}}/>
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
                <Box flexBasis="100%" paddingBottom="20%"/>
                <Box width="100%">
                    <TabContext value={value}>
                        <TabList onChange={handleChange} aria-label="Post or comment selector" centered>
                            <Tab label="Posts" value="Posts"/>
                            <Tab label="Comments" value="Comments"/>
                        </TabList>
                        <Divider/>
                        <TabPanel value="Posts">
                            <ContentContainer>
                                <PostPreview/>
                                <PostPreview/>
                                <PostPreview/>
                                <PostPreview/>
                                <PostPreview/>
                                <PostPreview/>
                            </ContentContainer>
                        </TabPanel>
                        <TabPanel value="Comments">
                            <ContentContainer>
                                <Comment/>
                                <Comment/>
                                <Comment/>
                                <Comment/>
                                <Comment/>
                                <Comment/>
                            </ContentContainer>
                        </TabPanel>
                    </TabContext>
                </Box>
            </Container>

        </>
    )
}