import {CardContent, Typography} from "@mui/material";
export default function PreviewFooter({body, openFullView}) {
    return(
        <CardContent onClick={openFullView} sx={{ cursor: "pointer"}}>
            <Typography align="left" variant="h5">
                {body}
            </Typography>
        </CardContent>
    )
}
