import {TextField, Button, Checkbox, Card, Container, Box} from '@mui/material'
import { ErrorMessage, Field, Form, Formik} from 'formik'
import * as Yup from 'yup'
import axios from 'axios'

export default function CreatePost() {
    return (
        <>
        <Container maxWidth="md" height="100%">
            <Card sx={{marginTop: "5%", marginBottom: "5%",boxShadow: "2px 2px 1px #a7a7a7", padding: "5%", height: "100%"}}>
                <Formik
                    initialValues={{title: '', content: '', isHidden: false}}
                    validationSchema={ Yup.object({
                        title: Yup.string()
                            .trim()
                            .min(5, 'Must be 5 characters or more')
                            .max(200, 'Must be 200 characters or less')
                            .required('This field is required'),
                        content: Yup.string()
                            .trim()
                            .min(100, 'Must be 100 characters or more')
                            .max(15000, 'Must be 15000 characters or less'),
                        isHidden: Yup.boolean()
                    })}
                    onSubmit={async (values, {setFieldError}) => {
                        let data = {
                            title: values.title,
                            content: values.content,
                            isHidden: values.isHidden
                        }
                        axios.post('/post/create', data,
                        {
                            headers: {
                                'Content-Type': 'application/json'
                            }
                        })
                        .then((response) => {
                            if(response?.status===201) {
                                window.location.href = `/`;
                            }
                        })
                        .catch((error) => {
                            if(error?.response?.data){
                                setFieldError('general',error.response.data);
                            }
                            else{
                                console.error(error)
                            }
                        })
                    }}
                >
                    <Form style={{display: "flex", flexDirection: "column", alignItems: "center", width: "100%"}}>
                        <Field as={TextField} label="Title" name="title" variant="outlined" margin="none" sx={{ mt: "5%", width: "100%"}}/>
                        <ErrorMessage name="title"/>
                        <Field as={TextField} label="Content" name="content" variant="outlined" margin="none" multiline minRows="5" maxRows="200" sx={{ mt: "5%", width: "100%"}}/>
                        <ErrorMessage name="content"/>
                        <Box sx={{display: "flex", alignItems: "center", width: "220px", marginTop: "5%", marginBottom: "2%"}}>
                            <Field name="isHidden" type="checkbox" as={Checkbox}/>
                            <label htmlFor="isHidden">Hide this post</label>
                            <ErrorMessage name="isHidden"/>
                        </Box>
                        <ErrorMessage name="general"/>
                        <Button type="submit" variant="outlined" color="primary" sx={{marginTop: "2%", backgroundColor: "#4F4F4F", color: "white"}}>
                            Post
                        </Button>
                    </Form>
                </Formik>
            </Card>
        </Container>
        </>
    )
}