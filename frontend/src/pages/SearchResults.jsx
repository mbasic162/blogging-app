import {useCallback, useEffect, useRef, useState} from 'react'
import {Avatar, Card, Typography} from '@mui/material'
import axios from 'axios'
import {useLocation} from 'react-router-dom'
import PreviewContainer from '/src/components/PreviewContainer'
import PostPreview from '/src/components/PostPreview'
import Comment from '/src/components/Comment'

const PAGE_SIZE = 10
const USER_PAGE_SIZE = 20


export default function SearchResults() {
    const location = useLocation()
    const params = new URLSearchParams(location.search)
    const query = params.get('q') || ''
    const type = params.get('type') || 'posts'
    const [results, setResults] = useState([])
    const [error, setError] = useState(() => query.trim() ? '' : 'Search query cannot be empty.')
    const [loading, setLoading] = useState(Boolean(query.trim()))
    const pageRef = useRef(0)
    const loadingRef = useRef(false)
    const hasMoreRef = useRef(true)
    const requestVersionRef = useRef(0)

    const loadPage = useCallback((page) => {
        if (!query.trim()) {
            return
        }
        if (loadingRef.current || !hasMoreRef.current) {
            return
        }
        loadingRef.current = true
        const requestVersion = requestVersionRef.current

        if(type === 'users') {
            axios.post('/user/search', null, {params: {s: query, numberOfUsers: 20, page: page}})
            .then((response) => {
                if (requestVersion !== requestVersionRef.current) {
                    return
                }
                setResults((currentResults) => page === 0 ? response.data : [...currentResults, ...response.data])
                hasMoreRef.current = response.data.length === USER_PAGE_SIZE
                pageRef.current = page
                setError('')
                setLoading(false)
            })
            .catch(() => {
                if (requestVersion === requestVersionRef.current) {
                    setError('Search failed. Please try again.')
                    setLoading(false)
                }})
            .finally(() => {
                if (requestVersion === requestVersionRef.current) {
                    loadingRef.current = false
                }})
            return;
        }
        if(type === 'comments') {
            axios.post('/comment/search', null, {params: {s: query, numberOfComments: 10, page: page}})
            .then((response) => {
                if (requestVersion !== requestVersionRef.current) {
                    return
                }
                setResults((currentResults) => page === 0 ? response.data : [...currentResults, ...response.data])
                hasMoreRef.current = response.data.length === PAGE_SIZE
                pageRef.current = page
                setError('')
                setLoading(false)
            })
            .catch(() => {
                if (requestVersion === requestVersionRef.current) {
                    setError('Search failed. Please try again.')
                    setLoading(false)
                }})
            .finally(() => {
                if (requestVersion === requestVersionRef.current) {
                    loadingRef.current = false
                }})
            return;
        }
        axios.post('/post/search', null, {params: {s: query, numberOfPosts: 10, page: page}})
        .then((response) => {
            if (requestVersion !== requestVersionRef.current) {
                return
            }
            setResults((currentResults) => page === 0 ? response.data : [...currentResults, ...response.data])
            hasMoreRef.current = response.data.length === PAGE_SIZE
            pageRef.current = page
            setError('')
            setLoading(false)
        })
        .catch(() => {
            if (requestVersion === requestVersionRef.current) {
                setError('Search failed. Please try again.')
                setLoading(false)
            }})
        .finally(() => {
            if (requestVersion === requestVersionRef.current) {
                loadingRef.current = false
            }})

    }, [query, type])

    useEffect(() => {
        requestVersionRef.current += 1
        loadingRef.current = false
        hasMoreRef.current = true
        pageRef.current = 0
        loadPage(0)
    }, [loadPage])

    useEffect(() => {
        function handleScroll() {
            const nearBottom = window.scrollY + window.innerHeight >= document.documentElement.scrollHeight - 400
            if (nearBottom) {
                loadPage(pageRef.current + 1)
            }
        }

        window.addEventListener('scroll', handleScroll)
        return () => window.removeEventListener('scroll', handleScroll)
    }, [loadPage])

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

    if (loading) {
        return <PreviewContainer />
    }

    return <PreviewContainer>{renderResults()}</PreviewContainer>
}
