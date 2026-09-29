import { useState } from "react"
import { Box, Button, Dialog, Typography } from "@mui/material"
import axios from "axios"
import { ErrorMessage, Form, Formik } from "formik"

export default function DeleteAccount() {
    const [dialogShown, setDialogShown] = useState(false)

    return(
        <>
            <Button type="button" variant="outlined" color="error" sx={{marginTop: "5%"}} onClick={() => setDialogShown(true)}>
                Delete Account
            </Button>

            <Dialog open={dialogShown} fullWidth onClose={() => setDialogShown(false)}>
                <Box padding="5%" display="flex" flexDirection="column" alignItems="center">
                    <Formik
                        initialValues={{}}
                        onSubmit={(_, {setFieldError}) => axios.post("/user/delete")
                            .then(() => {
                                localStorage.removeItem('token')
                                localStorage.removeItem('user')
                                window.location.href = "/"
                            })
                            .catch((error) => {
                                setFieldError('delete', error.response?.data || 'Unable to delete account')
                            })}
                    >
                        <Form>
                            <Typography textAlign="center" variant="h5" marginBottom="5%">
                                Account will remain deleted until you log in again
                            </Typography>

                            <Typography color="error" textAlign="center" marginBottom="5%">
                                <ErrorMessage name="delete" />
                            </Typography>

                            <Box sx={{display: "flex", flexDirection: "row", justifyContent: "space-between", width: "100%"}}>
                                <Button type="button" variant="outlined" color="primary" onClick={() => setDialogShown(false)}>
                                    Cancel
                                </Button>

                                <Button type="submit" variant="outlined" color="error">
                                    Delete Account
                                </Button>
                            </Box>
                        </Form>
                    </Formik>
                </Box>
            </Dialog>
        </>
    )
}