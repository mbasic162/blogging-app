import {Card, Divider} from "@mui/material";
import {encode} from '/src/utils/UriSanitiser.jsx'
import PreviewHeader from "./PreviewHeader.jsx";
import PreviewBody from "./PreviewBody.jsx";
import PreviewFooter from "./PreviewFooter.jsx";

export default function Comment() {
    const id = 1
    const content = "Comment's content"
    const rating = 0
    const date = "1/1/2025"
    const username = "Username"
    const commentURI = () => {
        if (content.length > 30 && content[30] !== ' ') {
            return encode(content.substring(0, 31) + "-" + id)
        }
        else if (content.length > 30) {
            return encode(content.substring(0, 30) + "-" + id)
        }
        return encode(content + "-" + id)
    }
    function openFullView() {
        let selection = window.getSelection();
        if(selection && selection.type !== 'Range') {
            window.location.href = "/comment/" + commentURI()
        }
    }
    return (
        <Card sx={{marginTop: "5%", marginBottom: "5%", boxShadow: "2px 2px 1px #a7a7a7"}}>
            <PreviewHeader username={username}/>
            <Divider sx={{borderBottomWidth: 2}}/>
            <PreviewBody body={title} openFullView={openFullView}/>
            <Divider sx={{borderBottomWidth: 2}}/>
            <PreviewFooter rating={rating} date={date}/>
        </Card>
    );
}