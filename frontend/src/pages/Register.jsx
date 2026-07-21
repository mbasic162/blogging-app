import {TextField, Button, Checkbox, Card, Container, Box} from '@mui/material'
import InputFileUpload from '../components/InputFileUpload.jsx'
import { ErrorMessage, Field, Form, Formik} from 'formik'
import * as Yup from 'yup'
import axios from 'axios'

export default function Register() {
    return(
        <>
        <Container maxWidth="md" height="100%">
            <Card sx={{marginTop: "5%", marginBottom: "5%",boxShadow: "2px 2px 1px #a7a7a7", padding: "5%", height: "100%"}}>
                <Formik
                    initialValues={{username: '', email: '', password: '', description: '', profilePicture: null, isPrivate: false}}
                    validationSchema={ Yup.object({
                        username: Yup.string()
                            .trim()
                            .min(3, 'Must be 3 characters or more')
                            .max(30, 'Must be 30 characters or less')
                            .matches(/^\S*$/, 'Cannot contain spaces')
                            .required('This field is required'),
                        email: Yup.string()
                            .trim()
                            .min(3, 'Must be 3 characters or more')
                            .max(50, 'Must be 50 characters or less')
                            .email('Invalid email format')
                            .required('This field is required'),
                        password: Yup.string()
                            .min(6, 'Must be 6 characters or more')
                            .max(100, 'Must be 100 characters or less')
                            .required('This field is required'),
                        description: Yup.string()
                            .trim()
                            .max(200, 'Must be 200 characters or less'),
                        profilePicture: Yup.mixed()
                            .nullable()
                            .test({
                                test: (file) => {
                                    if(file) {
                                        const allowedExtensions = ['jpg', 'jpeg', 'png'];
                                        return allowedExtensions.includes(file.name.split('.').pop().toLowerCase());
                                    }
                                    return true;
                                },
                                message: 'Invalid file type'
                            })
                            .test({
                                test: (file) => {
                                    if(file) {
                                        return file.size < 5 * 1024 * 1024;
                                    }
                                    return true;
                                },
                                message: 'Profile picture must be less than 5MB'
                            }),
                        isPrivate: Yup.boolean()
                    })}
                    onSubmit={async (values, {setFieldError}) => {
                        const formData = new FormData();
                        formData.append('username', values.username);
                        formData.append('email', values.email);
                        formData.append('password', values.password);
                        formData.append('description', values.description);
                        if(values.profilePicture instanceof File) {
                            formData.append('profilePicture', values.profilePicture);
                        }
                        formData.append('isPrivate', values.isPrivate);
                        axios.post('http://localhost:8080/auth/register', formData, {
                            headers: {
                                'Content-Type': 'multipart/form-data'
                            }
                        })
                        .then((response => {
                            localStorage.setItem('token', response.data.token);
                            localStorage.setItem('user', JSON.stringify({
                                username: response.data.username,
                                following: response.data.following,
                                profilePicture: response.data.profilePicture
                            }));
                            window.location.href = '/'
                        }))
                        .catch((error) => {
                            if(error.response && error.response.data && error.response.data.field && error.response.data.message) {
                                setFieldError(error.response.data.field, error.response.data.message);
                            } else if(error.response && error.response.data){
                                setFieldError('password',error.response.data);
                            }
                            else{
                                console.error(error)
                            }
                        })
                    }}
                >
                    {({ values, setFieldValue }) => (
                        <Form style={{display: "flex", flexDirection: "column", alignItems: "center", width: "100%"}}>
                            <Field as={TextField} label="Username" name="username" variant="outlined" margin="none" sx={{ mt: "5%"}}/>
                            <ErrorMessage name="username"/>
                            <Field as={TextField} label="Email" name="email" variant="outlined" margin="none" sx={{ mt: "5%"}}/>
                            <ErrorMessage name="email"/>
                            <Field as={TextField} label="Password" name="password" type="password" variant="outlined" margin="none" sx={{ mt: "5%"}}/>
                            <ErrorMessage name="password"/>
                            <Field as={TextField} label="Description (optional)" name="description" variant="outlined" margin="none" sx={{ mt: "5%"}}/>
                            <ErrorMessage name="description"/>
                            <Box sx={{display: "flex", flexDirection: "column", alignItems: "center", width: "100%", marginTop: "5%"}}>
                                <Field as={InputFileUpload} name="profilePicture" text={values.profilePicture ? values.profilePicture.name : "Upload profile picture (optional)"} fileTypes=".jpg,.jpeg,.png" onChange={(e) => setFieldValue("profilePicture",e.target.files[0])}/>
                                <ErrorMessage name="profilePicture"/>
                            </Box>
                            <Box sx={{display: "flex", justifyContent: "space-between", alignItems: "center", width: "220px", marginTop: "5%", marginBottom: "2%"}}>
                                <Field name="isPrivate" type="checkbox" as={Checkbox}/>
                                <label htmlFor="isPrivate">Make my profile private</label>
                                <ErrorMessage name="isPrivate"/>
                            </Box>
                            <Button type="submit" variant="outlined" color="primary" sx={{marginTop: "2%", backgroundColor: "#4F4F4F", color: "white"}}>
                                Register
                            </Button>
                        </Form>
                    )}
                </Formik>
            </Card>
        </Container>
        </>
    )
}