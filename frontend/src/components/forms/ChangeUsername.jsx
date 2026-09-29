import { Box, Button, TextField } from '@mui/material'
import axios from 'axios'
import { ErrorMessage, Field, Form, Formik} from 'formik'
import * as Yup from 'yup'

export default function ChangeUsername({user, setUser}) {
    return(
        <Formik
            initialValues={{username: user.username}}
            validationSchema={ Yup.object({
                username: Yup.string()
                .trim()
                .min(3, 'Must be 3 characters or more')
                .max(30, 'Must be 30 characters or less')
                .matches(/^\S*$/, 'Cannot contain spaces')
                .test({
                    test: (value) => value !== (user.username || '').trim(),
                    message: 'New username must be different from the old one'
                })
            })}
            onSubmit={async (values, {setFieldError}) => {
                axios.post("/user/changeUsername", {newUsername: values.username}).then((res) => {
                    setUser({...user, username: values.username})
                    localStorage.setItem('user', JSON.stringify({...user, username: values.username}))
                    localStorage.setItem('token', res.data)
                    window.dispatchEvent(new CustomEvent('successAlert', {detail: 'Username changed successfully'}))
                }).catch((error) => {
                    setFieldError('username', error.response?.data || 'Unable to change username')
                })
            }}
        >
            <Form>
                <Box display="flex" flexDirection="row" alignItems="center" justifyContent="space-between" marginBottom="5%">
                    <Box sx={{width: "60%", position: "relative"}}>
                        <Field as={TextField} name="username" label="New username" variant="outlined" fullWidth/>
                        <Box sx={{position: "absolute", top: "100%", left: 0}}>
                            <ErrorMessage name="username"/>
                        </Box>
                    </Box>
                    <Button type="submit" variant="contained" color="primary" sx={{width: "30%"}}>
                        Change Username
                    </Button>
                </Box>
            </Form>
        </Formik>
    )
}