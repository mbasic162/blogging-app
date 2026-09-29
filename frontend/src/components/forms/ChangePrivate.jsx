import { Box, Checkbox, FormControlLabel } from "@mui/material";
import axios from "axios";
export default function ChangePrivate({user, setUser}) {
    function handleChange() {
        if(user.isPrivate) {
            axios.post("/user/goPublic").then(() => {
                setUser({...user, isPrivate: false})
                window.dispatchEvent(new CustomEvent('successAlert', {detail: 'Account made public successfully'}))
            })
        } else {
            axios.post("/user/goPrivate").then(() => {
                setUser({...user, isPrivate: true})
                window.dispatchEvent(new CustomEvent('successAlert', {detail: 'Account made private successfully'}))
            })
        }
    }

    return(
        <Box sx={{display: "flex", alignItems: "center", width: "60%", marginBottom: "5%"}}>
            <FormControlLabel
                control={
                    <Checkbox
                        checked={user.isPrivate}
                        onChange={handleChange}/>
                }
                label="Private Account"
            />
        </Box>
    )
}