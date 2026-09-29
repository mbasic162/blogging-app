import { Box, Button, TextField } from '@mui/material'
import axios from 'axios'
import { ErrorMessage, Field, Form, Formik} from 'formik'
import * as Yup from 'yup'

export default function ChangeDescription({user, setUser}) {
    return(
        <Formik
            initialValues={{description: user.description || ''}}
            validationSchema={ Yup.object({
                description: Yup.string()
                .trim()
                .max(200, 'Must be 200 characters or less')
                .test({
                    test: (value) => value === '' || value !== (user.description || '').trim(),
                    message: 'New description must be different from the old one'
                })
            })}
            onSubmit={async (values, {setFieldError}) => {
                axios.post("/user/changeDescription", {newDescription: values.description}).then(() => {
                    setUser({...user, description: values.description})
                    localStorage.setItem('user', JSON.stringify({...user, description: values.description}))
                    window.dispatchEvent(new CustomEvent('successAlert', {detail: 'Description changed successfully'}))
                }).catch((error) => {
                    setFieldError('description', error.response?.data || 'Unable to change description')
                })
            }}
        >
            <Form>
                <Box display="flex" flexDirection="row" alignItems="center" justifyContent="space-between" marginBottom="5%">
                    <Box sx={{width: "60%", position: "relative"}}>
                        <Field as={TextField} name="description" label="New description" variant="outlined" minRows="5" multiline fullWidth/>
                        <Box sx={{position: "absolute", top: "100%", left: 0}}>
                            <ErrorMessage name="description"/>
                        </Box>
                    </Box>
                    <Button type="submit" variant="contained" color="primary" sx={{width: "30%"}}>
                        Change Description
                    </Button>
                </Box>
            </Form>
        </Formik>
    )
}