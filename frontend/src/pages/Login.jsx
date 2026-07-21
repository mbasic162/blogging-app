import {TextField, Button, Card, Container} from '@mui/material'
import { ErrorMessage, Field, Form, Formik} from 'formik'
import * as Yup from 'yup'
import axios from 'axios'

export default function Register() {
    return(
        <>
        <Container maxWidth="md" height="100%">
            <Card sx={{marginTop: "5%", marginBottom: "5%",boxShadow: "2px 2px 1px #a7a7a7", padding: "5%", height: "100%"}}>
                <Formik
                    initialValues={{username: '',password: ''}}
                    validationSchema={ Yup.object({
                        username: Yup.string()
                            .trim()
                            .required('This field is required'),
                        password: Yup.string()
                            .required('This field is required'),
                    })}
                    onSubmit={async (values, {setFieldError}) => {
                        let data = {
                            username: values.username,
                            password: values.password
                        }
                        axios.post('http://localhost:8080/auth/login', data, {
                            headers: {
                                'Content-Type': 'application/json'
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
                                setFieldError(error.response.data.field, error.response.data.message)
                            }
                            else if(error.response && error.response.data) {
                                setFieldError('password', error.response.data)
                            }
                            else {
                                console.error(error);
                            }
                        })
                    }}
                >
                    <Form style={{display: "flex", flexDirection: "column", alignItems: "center", width: "100%"}}>
                        <Field as={TextField} label="Username" name="username" variant="outlined" margin="none" sx={{ mt: "5%"}}/>
                        <ErrorMessage name="username"/>
                        <Field as={TextField} label="Password" name="password" type="password" variant="outlined" margin="none" sx={{ mt: "5%"}}/>
                        <ErrorMessage name="password"/>
                        <Button type="submit" variant="outlined" color="primary" sx={{marginTop: "2%", backgroundColor: "#4F4F4F", color: "white"}}>
                            Login
                        </Button>
                    </Form>
                </Formik>
            </Card>
        </Container>
        </>
    )
}