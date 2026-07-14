import PreviewContainer from '/src/components/PreviewContainer'
import PostPreview from '/src/components/PostPreview'
import {useState, useEffect} from "react";
import axios from "axios";


export default function Home() {
    const [posts, setPosts] = useState([]);
    useEffect(() => {
        axios.get('http://localhost:8080/post/')
            .then(response => {
                setPosts(response.data);
            }
        )
    }, []);
    return (
        <>
            <PreviewContainer>
                {posts.map((post) => (
                    <PostPreview key={post.id} id={post.id} title={post.title} rating={post.rating} date={post.date} username={post.username} profilePicture={post.profilePicture}/>
                ))}
            </PreviewContainer>
        </>
    )
}