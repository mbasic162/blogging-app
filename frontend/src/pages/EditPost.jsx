import {useLoaderData, useParams} from "react-router-dom";
import {TextField, Button, Checkbox, Card, Container, Box} from '@mui/material';
import {ErrorMessage, Field, Form, Formik} from 'formik';
import * as Yup from 'yup';
import axios from 'axios';
import {encode} from '/src/utils/UriSanitiser.jsx';

function getPostURI(title, id) {
    if (title.length > 30 && title[30] !== ' ') {
        return encode(title.substring(0, 31) + "-" + id);
    }
    if (title.length > 30) {
        return encode(title.substring(0, 30) + "-" + id);
    }
    return encode(title + "-" + id);
}

export default function EditPost() {
    const post = useLoaderData();
    const {postURI} = useParams();

    return (
        <Container maxWidth="md" height="100%">
            <Card sx={{marginTop: "5%", marginBottom: "5%", boxShadow: "2px 2px 1px #a7a7a7", padding: "5%", height: "100%"}}>
                <Formik
                    initialValues={{title: post.title, content: post.content, isHidden: post.isHidden || false}}
                    validationSchema={Yup.object({
                        title: Yup.string().trim().min(5, 'Must be 5 characters or more').max(200, 'Must be 200 characters or less').required('This field is required'),
                        content: Yup.string().trim().min(100, 'Must be 100 characters or more').max(15000, 'Must be 15000 characters or less'),
                        isHidden: Yup.boolean()
                    })}
                    onSubmit={async (values, {setFieldError}) => {
                        if (values.title !== post.title) {
                            axios.post('/post/changeTitle', {postURI: postURI, newTitle: values.title}).catch((error) => {
                                setFieldError('title', error?.response?.data || 'Something went wrong. Please try again later.');
                            })
                        }
                        if (values.content !== post.content) {
                            axios.post('/post/changeContent', {postURI: postURI, newContent: values.content}).catch((error) => {
                                setFieldError('content', error?.response?.data || 'Something went wrong. Please try again later.');
                            })
                        }
                        if (values.isHidden !== Boolean(post.isHidden)) {
                            if(values.isHidden) {
                                axios.post('/post/hide', {postURI: postURI}).catch((error) => {
                                    setFieldError('general', error?.response?.data || 'Something went wrong. Please try again later.');
                                })
                            }
                            else {
                                axios.post('/post/unhide', {postURI: postURI}).catch((error) => {
                                    setFieldError('general', error?.response?.data || 'Something went wrong. Please try again later.');
                                })
                            }
                        }
                        window.location.href = `/post/${getPostURI(values.title, post.id)}`;
                    }}
                >
                    <Form style={{display: "flex", flexDirection: "column", alignItems: "center", width: "100%"}}>
                        <Field as={TextField} label="Title" name="title" variant="outlined" sx={{mt: "5%", width: "100%"}}/>
                        <ErrorMessage name="title"/>
                        <Field as={TextField} label="Content" name="content" variant="outlined" multiline minRows="5" maxRows="200" sx={{mt: "5%", width: "100%"}}/>
                        <ErrorMessage name="content"/>
                        <Box sx={{display: "flex", alignItems: "center", width: "220px", marginTop: "5%", marginBottom: "2%"}}>
                            <Field name="isHidden" type="checkbox" as={Checkbox}/>
                            <label htmlFor="isHidden">Hide this post</label>
                        </Box>
                        <ErrorMessage name="general"/>
                        <Button type="submit" variant="outlined" color="primary" sx={{marginTop: "2%", backgroundColor: "#4F4F4F", color: "white"}}>
                            Save
                        </Button>
                    </Form>
                </Formik>
            </Card>
        </Container>
    );
}