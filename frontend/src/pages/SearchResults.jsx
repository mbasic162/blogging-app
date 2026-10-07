import {useEffect, useState} from 'react'
import {Avatar, Card, Typography} from '@mui/material'
import axios from 'axios'
import PreviewContainer from '/src/components/PreviewContainer'
import PostPreview from '/src/components/PostPreview'
import Comment from '/src/components/Comment'


export default function SearchResults() {
    const params = new URLSearchParams(window.location.search)
    const query = params.get('q') || ''
    const type = params.get('type') || 'posts'
    const [results, setResults] = useState([])
    const [error, setError] = useState('')

    useEffect(() => {
        if(type === 'posts') {
            axios.get('/post/search', {params: {s: query}})
                .then((response) => setResults(response.data))
                .catch(() => setError('Search failed. Please try again.'))
            return
        }
        if(type === 'users') {
            axios.get('/user/search', {params: {s: query}})
                .then((response) => setResults(response.data))
                .catch(() => setError('Search failed. Please try again.'))
            return
        }
        if(type === 'comments') {
            axios.get('/comment/search', {params: {s: query}})
                .then((response) => setResults(response.data))
                .catch(() => setError('Search failed. Please try again.'))
            return
        }
    }, [query])

    function openProfile(username) {
        window.location.assign(`/${username}`)
    }

    function renderResults() {
        if (error) {
            return <Typography color="error">{error}</Typography>
        }
        if (results.length === 0) {
            return <Typography variant="h4" marginTop="2%">No {type} found</Typography>
        }
        if (type === 'users') {
            return results.map((user) => (
                <Card key={user.username} sx={{display: 'flex', alignItems: 'center', marginTop: '1%', marginBottom: '1%', padding: '2%', boxShadow: '2px 2px 1px #a7a7a7'}}>
                    <Avatar src={user.profilePicture} onClick={() => openProfile(user.username)} sx={{cursor: 'pointer'}} />
                    <Typography marginLeft="2%" onClick={() => openProfile(user.username)} sx={{cursor: 'pointer'}}>
                        {user.username}
                    </Typography>
                </Card>
            ))
        }
        if (type === 'comments') {
            return results.map((comment) => (
                <Comment key={comment.id} id={comment.id} content={comment.content} comments={comment.comments} ratingConst={comment.rating} date={comment.date} username={comment.username} profilePicture={comment.profilePicture} userLikedConst={comment.userLiked} userDislikedConst={comment.userDisliked} isHiddenConst={comment.isHidden} />
            ))
        }
        return results.map((post) => (
            <PostPreview key={post.id} id={post.id} title={post.title} rating={post.rating} date={post.date} username={post.username} profilePicture={post.profilePicture} />
        ))
    }

    return <PreviewContainer>{renderResults()}</PreviewContainer>
}
