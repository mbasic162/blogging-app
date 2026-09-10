import { Form, Formik, Field, ErrorMessage } from "formik";
import * as Yup from "yup";
import axios from "axios";
import { TextField, Button } from "@mui/material";

export default function CommentReply({parentCommentId}) {
    return (
        <Formik
            initialValues={{content: ''}}
            validationSchema={ Yup.object({
                content: Yup.string()
                    .trim()
                    .min(1, 'Reply cannot be empty')
                    .max(1000, 'Must be 1000 characters or less')
                    .required('Reply cannot be empty')
            })}
            onSubmit={async (values, {setFieldError}) => {
                let data = {
                    parentCommentId: parentCommentId,
                    content: values.content
                }
                axios.post('/comment/create', data,
                {
                    headers: {
                        'Content-Type': 'application/json'
                    }
                })
                .then((response) => {
                    if(response?.status===201) {
                        window.location.reload();
                    }
                })
                .catch((error) => {
                    if(error?.status===401) {
                        setFieldError('content',"Please log in");
                    }
                    else if(error?.response?.data){
                        setFieldError('content',"Something went wrong. Please try again later.");
                    }
                    else{
                        console.error(error)
                    }
                })
            }}
        >
            <Form style={{display: "flex", flexDirection: "column", alignItems: "center", width: "100%"}}>
                <Field as={TextField} label="Reply" name="content" variant="outlined" margin="none" multiline sx={{ width: "100%", backgroundColor: "white"}}/>
                <ErrorMessage name="content"/>
                <Button type="submit" variant="outlined" color="primary" sx={{marginTop: "2%", backgroundColor: "#4F4F4F", color: "white"}}>
                    Send Reply
                </Button>
            </Form>
        </Formik>
    )
}