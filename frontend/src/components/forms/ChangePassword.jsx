import { Box, TextField } from '@mui/material'
import axios from 'axios'
import { ErrorMessage, Field, Form, Formik} from 'formik'
import * as Yup from 'yup'
import ConfirmPassword from '../dialogs/ConfirmPassword'

export default function ChangePassword() {
    return(
        <Formik
            initialValues={{password: '', currentPassword: ''}}
            validationSchema={ Yup.object({
                password: Yup.string()
                .min(6, 'Must be 6 characters or more')
                .max(100, 'Must be 100 characters or less'),
                currentPassword: Yup.string().required('Password is required')
            })}
            onSubmit={async (values, {setFieldError}) => {
                return axios.post("/user/changePassword", {
                    password: values.currentPassword,
                    newPassword: values.password
                }, {
                    headers: {
                        'Content-Type': 'application/json'
                    }
                }).then(() => {
                    window.dispatchEvent(new CustomEvent('successAlert', {detail: 'Password changed successfully'}))
                }).catch((error) => {
                    setFieldError('currentPassword', error.response?.data || 'Unable to confirm password')
                    throw error
                })
            }}
        >
            <Form>
                <Box display="flex" flexDirection="row" alignItems="center" justifyContent="space-between" marginBottom="5%">
                    <Box sx={{width: "60%", position: "relative"}}>
                        <Field as={TextField} type="password" name="password" label="New password" variant="outlined" fullWidth/>
                        <Box sx={{position: "absolute", top: "100%", left: 0}}>
                            <ErrorMessage name="password"/>
                        </Box>
                    </Box>
                    <ConfirmPassword
                        message="Enter your current password"
                        buttonText="Change Password"
                    />
                </Box>
            </Form>
        </Formik>
    )
}