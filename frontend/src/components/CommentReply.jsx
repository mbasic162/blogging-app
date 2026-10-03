import { Form, Formik, Field, ErrorMessage } from "formik";
import * as Yup from "yup";
import axios from "axios";
import { TextField, Button, Checkbox, Box } from "@mui/material";

export default function CommentReply({parentCommentId, parentPostId, editCommentURI, initialContent = '', initialIsHidden = false}) {
    const isEditing = Boolean(editCommentURI);
    return (
        <Formik
            initialValues={{content: initialContent, isHidden: initialIsHidden}}
            validationSchema={ Yup.object({
                content: Yup.string()
                    .trim()
                    .min(1, 'Comment cannot be empty')
                    .max(1000, 'Must be 1000 characters or less')
                    .required('Comment cannot be empty'),
                isHidden: Yup.boolean()
            })}
            onSubmit={async (values, {setFieldError}) => {
                if (isEditing) {
                    if (values.content !== initialContent) {
                        axios.post('/comment/changeContent', {
                            commentURI: editCommentURI,
                            content: values.content
                        }).catch((error) => {
                            setFieldError('content', error?.response?.data || 'Something went wrong. Please try again later.');
                        })
                    }
                    if (values.isHidden !== initialIsHidden) {
                        if(values.isHidden) {
                            axios.post('/comment/hide', {commentURI: editCommentURI}).catch((error) => {
                                setFieldError('content', error?.response?.data || 'Something went wrong. Please try again later.');
                            })
                        }
                        else {
                            axios.post('/comment/unhide', {commentURI: editCommentURI}).catch((error) => {
                                setFieldError('content', error?.response?.data || 'Something went wrong. Please try again later.');
                            })
                        }
                    }
                    window.location.reload();
                    return;
                }
                let data = {
                    parentPostId: parentPostId,
                    parentCommentId: parentCommentId,
                    content: values.content
                }
                axios.post('/comment/create', data,
                {
                    headers: {
                        'Content-Type': 'application/json'
                    }
                })
                .then(() => {
                    window.location.reload();
                })
                .catch((error) => {
                    if(error?.response?.data){
                        setFieldError('content',"Something went wrong. Please try again later.");
                    }
                    else{
                        console.error(error)
                    }
                })
            }}
        >
            <Form style={{display: "flex", flexDirection: "column", alignItems: "center", width: "100%"}}>
                <Field as={TextField} label={isEditing ? "Edit comment" : "Comment"} name="content" variant="outlined" margin="none" multiline sx={{ width: "100%", backgroundColor: "white"}}/>
                <ErrorMessage name="content"/>
                {isEditing && <Box sx={{display: "flex", alignItems: "center", marginTop: "2%"}}>
                    <Field name="isHidden" type="checkbox" as={Checkbox}/>
                    <label htmlFor="isHidden">Hide this comment</label>
                </Box>}
                <Button type="submit" variant="outlined" color="primary" sx={{marginTop: "2%", backgroundColor: "#4F4F4F", color: "white"}}>
                    {isEditing ? "Save Comment" : "Send Comment"}
                </Button>
            </Form>
        </Formik>
    )
}