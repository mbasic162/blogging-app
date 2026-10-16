import { Form, Formik, Field, ErrorMessage } from "formik";
import * as Yup from "yup";
import axios from "axios";
import { TextField, Button, Checkbox, Box } from "@mui/material";

export default function CommentReply({parentCommentId, parentPostId, editCommentURI, initialContent = '', initialIsHidden = false, initialIsDeleted = false, onPermanentlyDelete}) {
    const isEditing = Boolean(editCommentURI);
    return (
        <Formik
            initialValues={{content: initialContent, isHidden: initialIsHidden, isDeleted: initialIsDeleted}}
            validationSchema={ Yup.object({
                content: Yup.string()
                    .trim()
                    .min(1, 'Comment cannot be empty')
                    .max(1000, 'Must be 1000 characters or less')
                    .required('Comment cannot be empty'),
                isHidden: Yup.boolean(),
                isDeleted: Yup.boolean()
            })}
            onSubmit={async (values, {setFieldError}) => {
                if (isEditing) {
                    try {
                        if (values.isHidden !== initialIsHidden) {
                            await axios.post(
                                values.isHidden ? '/comment/hide' : '/comment/unhide',
                                null,
                                {params: {commentURI: editCommentURI}}
                            );
                        }
                        if (values.isDeleted !== initialIsDeleted) {
                            await axios.post(
                                values.isDeleted ? '/comment/delete' : '/comment/undelete',
                                null,
                                {params: {commentURI: editCommentURI}}
                            );
                        }
                        if (values.content !== initialContent) {
                            await axios.post('/comment/changeContent', null, {
                                params: {
                                    commentURI: editCommentURI,
                                    newContent: values.content
                                }
                            });
                        }
                        window.location.reload();
                    } catch (error) {
                        setFieldError('content', error?.response?.data || 'Something went wrong. Please try again later.');
                    }
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
                <Field as={TextField} label={isEditing ? "Edit comment" : "Comment"} name="content" variant="outlined" margin="none" multiline slotProps={{inputLabel: {shrink: true}}} sx={{ width: "100%", backgroundColor: "white"}}/>
                <ErrorMessage name="content"/>
                {isEditing && <Box sx={{display: "flex", flexDirection: "column", alignItems: "center", marginTop: "2%"}}>
                    <Box sx={{display: "grid", gridTemplateColumns: "32px 1fr", alignItems: "center", width: "240px"}}>
                        <Field name="isHidden">
                            {({field}) => <Checkbox {...field} checked={Boolean(field.value)} />}
                        </Field>
                        <label htmlFor="isHidden">Hide this comment</label>
                    </Box>
                    <Box sx={{display: "grid", gridTemplateColumns: "32px 1fr", alignItems: "center", width: "240px"}}>
                        <Field name="isDeleted">
                            {({field}) => <Checkbox {...field} checked={Boolean(field.value)} />}
                        </Field>
                        <label htmlFor="isDeleted">Delete this comment</label>
                    </Box>
                </Box>}
                {isEditing && initialIsDeleted && <Button type="button" variant="contained" color="error" onClick={onPermanentlyDelete} sx={{marginTop: "2%"}}>
                        Permanently delete
                </Button>}
                <Button type="submit" variant="outlined" color="primary" sx={{marginTop: "2%", backgroundColor: "#4F4F4F", color: "white"}}>
                    {isEditing ? "Save Comment" : "Send Comment"}
                </Button>
            </Form>
        </Formik>
    )
}