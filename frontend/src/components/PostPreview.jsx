import {Card, Divider} from "@mui/material";
import {encode} from '/src/utils/UriSanitiser.jsx'
import PreviewHeader from "./PreviewHeader.jsx";
import PreviewBody from "./PreviewBody.jsx";
import PreviewFooter from "./PreviewFooter.jsx";


export default function PostPreview({id, title, rating, date, username, profilePicture}) {
    const postURI = () => {
        if (title.length > 30 && title[30] !== ' ') {
            return encode(title.substring(0, 31) + "-" + id)
        }
        else if (title.length > 30) {
            return encode(title.substring(0, 30) + "-" + id)
        }
        return encode(title + "-" + id)
    }
    function openFullView() {
        let selection = window.getSelection();
        if(selection && selection.type != 'Range') {
            window.location.href = "/post/" + postURI()
        }
    }
    var formattedDate = new Date(date).toLocaleDateString();
    return (
        <Card sx={{marginTop: "5%", marginBottom: "5%", boxShadow: "2px 2px 1px #a7a7a7"}}>
            <PreviewHeader username={username} profilePicture={profilePicture} date={formattedDate}/>
            <Divider sx={{borderBottomWidth: 2}}/>
            <PreviewBody body={title} openFullView={openFullView}/>
            <Divider sx={{borderBottomWidth: 2}}/>
            <PreviewFooter rating={rating}/>
        </Card>
    );
}