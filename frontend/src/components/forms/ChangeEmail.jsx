import { Box, TextField } from '@mui/material'
import axios from 'axios'
import { ErrorMessage, Field, Form, Formik} from 'formik'
import * as Yup from 'yup'
import ConfirmPassword from '../dialogs/ConfirmPassword'

export default function ChangeEmail({user, setUser}) {
    return(
        <Formik
            initialValues={{email: user.email, currentPassword: ''}}
            validationSchema={ Yup.object({
                email: Yup.string()
                .trim()
                .min(3, 'Must be 3 characters or more')
                .max(50, 'Must be 50 characters or less')
                .email('Invalid email format')
                .test({
                    test: (value) => value !== (user.email || '').trim(),
                    message: 'New email must be different from the old one'
                }),
                currentPassword: Yup.string().required('Password is required')
            })}
            onSubmit={async (values, {setFieldError}) => {
                    return axios.post("/user/changeEmail", {newEmail: values.email, password: values.currentPassword},{
                        headers: {
                            'Content-Type': 'application/json'
                        }
                    })
                    .then(() => {
                        setUser({...user, email: values.email})
                        window.dispatchEvent(new CustomEvent('successAlert', {detail: 'Email changed successfully'}))
                    })
                    .catch((error) => {
                        setFieldError('currentPassword', error.response?.data || 'Unable to confirm password')
                        throw error
                    })
            }}
        >
            <Form>
                <Box display="flex" flexDirection="row" alignItems="center" justifyContent="space-between" marginBottom="5%">
                    <Box sx={{width: "60%", position: "relative"}}>
                        <Field as={TextField} name="email" label="New email" variant="outlined" fullWidth/>
                        <Box sx={{position: "absolute", top: "100%", left: 0}}>
                            <ErrorMessage name="email"/>
                        </Box>
                    </Box>
                    <ConfirmPassword
                        message="Enter your password"
                        buttonText="Change Email"
                    />
                </Box>
            </Form>
        </Formik>
    )
}