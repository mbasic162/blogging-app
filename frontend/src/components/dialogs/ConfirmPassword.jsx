import { useState } from "react"
import { Box, Button, Dialog, TextField, Typography } from "@mui/material"
import { ErrorMessage, Field, useFormikContext} from 'formik'

export default function ConfirmPassword({message, buttonText, buttonColor = "primary"}) {
    const [dialogShown, setDialogShown] = useState(false)
    const { submitForm, setFieldValue } = useFormikContext()

    async function handleConfirm() {
        try {
            await submitForm()
            await setFieldValue('currentPassword', '')
            setDialogShown(false)
        } catch {}
    }

    return(
        <>
            <Button type="button" variant="contained" color={buttonColor} onClick={() => setDialogShown(true)}>
                {buttonText}
            </Button>

            <Dialog open={dialogShown} fullWidth onClose={() => setDialogShown(false)}>
                <Box padding="5%" display="flex" flexDirection="column" alignItems="center">
                    <Typography textAlign="center" variant="h5" marginBottom="5%">
                        {message}
                    </Typography>

                    <Box display="flex" flexDirection="column" alignItems="center" justifyContent="space-between" marginBottom="5%">
                        <Field as={TextField} type="password" name="currentPassword" label="Password" variant="outlined" fullWidth/>
                        <ErrorMessage name="currentPassword"/>
                    </Box>

                    <Box sx={{display: "flex", flexDirection: "row", justifyContent: "space-between", width: "100%"}}>
                        <Button type="button" variant="outlined" color="primary" onClick={() => setDialogShown(false)}>
                            Cancel
                        </Button>

                        <Button type="button" variant="contained" color={buttonColor} onClick={handleConfirm}>
                            Confirm
                        </Button>
                    </Box>
                </Box>
            </Dialog>
        </>
    )
}