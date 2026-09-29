import { Avatar, Box, Button} from '@mui/material'
import axios from 'axios'
import { ErrorMessage, Field, Form, Formik} from 'formik'
import InputFileUpload from '../InputFileUpload.jsx'
import * as Yup from 'yup'
import { useState } from 'react'

export default function ChangeProfilePicture({user, setUser}) {
    const [preview, setPreview] = useState(user.profilePicture)

    return(
        <Formik
            initialValues={{profilePicture: user.profilePicture}}
            validationSchema={ Yup.object({
                profilePicture: Yup.mixed()
                    .nonNullable({
                        message: 'Please select a profile picture'
                    })
                    .test({
                        test: (file) => {
                            if(file instanceof File) {
                                const allowedExtensions = ['jpg', 'jpeg', 'png'];
                                return allowedExtensions.includes(file.name.split('.').pop().toLowerCase());
                            }
                            return true;
                        },
                        message: 'Invalid file type'
                    })
                    .test({
                        test: (file) => {
                            if(file instanceof File) {
                                return file.size < 5 * 1024 * 1024;
                            }
                            return true;
                        },
                        message: 'Profile picture must be less than 5MB'
                    })
                    .test({
                        test: (file) => {
                            if(!(file instanceof File) || !user.profilePicture) {
                                return true
                            }
                            return new Promise((resolve) => {
                                const reader = new FileReader()
                                reader.onload = (event) => {
                                    const newPicture = event.target.result.replace(/^data:[^;]+;base64,/, '').replace(/\s/g, '')
                                    const oldPicture = user.profilePicture.replace(/^data:[^;]+;base64,/, '').replace(/\s/g, '')
                                    resolve(newPicture !== oldPicture)
                                }
                                reader.onerror = () => resolve(true)
                                reader.readAsDataURL(file)
                            })
                        },
                        message: 'Please select a different profile picture'
                    })
            })}
            onSubmit={async (values, {setFieldError}) => {
                if(!(values.profilePicture instanceof File)) {
                    setFieldError('profilePicture', 'Please select a profile picture')
                    return
                }
                const formData = new FormData()
                formData.append('newProfilePicture', values.profilePicture)
                axios.post("/user/changeProfilePicture", formData).then((response) => {
                    const profilePicture = response.data?.profilePicture || preview
                    const updatedUser = {...user, profilePicture}
                    setUser(updatedUser)
                    localStorage.setItem('user', JSON.stringify(updatedUser))
                    window.dispatchEvent(new CustomEvent('userUpdated', {detail: updatedUser}))
                    window.dispatchEvent(new CustomEvent('successAlert', {detail: 'Profile picture changed successfully'}))
                }).catch((error) => {
                    setFieldError('profilePicture', error.response?.data || 'Unable to change profile picture')
                })
            }}
        >
            {({ setFieldValue }) => (
                <Form>
                    <Box display="flex" flexDirection="row" alignItems="center" justifyContent="space-between" marginBottom="5%">
                        <Box display="flex" flexDirection="column" justifyContent="center" alignItems="center" sx={{width: "60%", position: "relative"}}>
                            <Avatar src={preview} sx={{width: "100px", height: "100px", marginBottom: "5%"}}/>
                            <Field as={InputFileUpload} name="profilePicture" text={"Change profile picture"} fileTypes=".jpg,.jpeg,.png" onChange={(e) => {
                                const file = e.target.files[0]
                                if(file) {
                                    const reader = new FileReader()
                                    reader.onload = (event) => setPreview(event.target.result)
                                    reader.readAsDataURL(file)
                                }
                                setFieldValue("profilePicture", file)
                            }}/>
                            <ErrorMessage name="profilePicture"/>
                        </Box>
                        <Button type="submit" variant="contained" color="primary" sx={{width: "30%"}}>
                            Confirm profile picture change
                        </Button>
                    </Box>
                </Form>
            )}
        </Formik>
    )
}