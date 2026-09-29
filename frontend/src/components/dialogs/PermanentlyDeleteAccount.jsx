import axios from "axios"
import { Formik } from "formik"
import * as Yup from "yup"
import ConfirmPassword from "./ConfirmPassword";

export default function PermanentlyDeleteAccount() {
    return(
        <Formik
            initialValues={{currentPassword: ''}}
            validationSchema={Yup.object({
                currentPassword: Yup.string().required('Password is required')
            })}
            onSubmit={async (values, {setFieldError}) => {
                    return axios.post("/user/permanentlyDelete", null, {params: {password: values.currentPassword}})
                    .then(() => {
                    localStorage.removeItem('token')
                    localStorage.removeItem('user')
                    window.location.href = "/"
                    })
                    .catch((error) => {
                        setFieldError('currentPassword', error.response?.data || 'Unable to confirm password')
                        throw error
                    })
            }}
        >
            <ConfirmPassword
                message="Enter your password, this action is irreversible!"
                buttonText="Permanently Delete Account"
                buttonColor="error"
            />
        </Formik>
    )
}