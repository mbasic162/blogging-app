import PreviewContainer from '/src/components/PreviewContainer'
import PostPreview from '/src/components/PostPreview'
import {useCallback, useEffect, useRef, useState} from "react";
import axios from "axios";

const PAGE_SIZE = 10;

export default function Home() {
    const [posts, setPosts] = useState([]);
    const [error, setError] = useState('');
    const [hasMore, setHasMore] = useState(true);
    const pageRef = useRef(0);
    const loadingRef = useRef(false);
    const hasMoreRef = useRef(true);

    const loadPage = useCallback((page) => {
        if (loadingRef.current || !hasMoreRef.current) {
            return;
        }
        loadingRef.current = true;
        axios.post('/post', null, {params: {numberOfPosts: PAGE_SIZE, page}})
            .then((response) => {
                setPosts((currentPosts) => page === 0 ? response.data : [...currentPosts, ...response.data]);
                const nextHasMore = response.data.length === PAGE_SIZE;
                hasMoreRef.current = nextHasMore;
                setHasMore(nextHasMore);
                pageRef.current = page;
                setError('');
            })
            .catch(() => setError('Could not load posts. Please try again.'))
            .finally(() => {
                loadingRef.current = false;
            });
    }, []);

    useEffect(() => {
        loadPage(0);
    }, [loadPage]);

    useEffect(() => {
        function handleScroll() {
            const nearBottom = window.scrollY + window.innerHeight >= document.documentElement.scrollHeight - 400;
            if (nearBottom) {
                loadPage(pageRef.current + 1);
            }
        }

        window.addEventListener('scroll', handleScroll);
        return () => window.removeEventListener('scroll', handleScroll);
    }, [loadPage]);

    if (error && posts.length === 0) {
        return <PreviewContainer>{error}</PreviewContainer>;
    }

    if (!hasMore && posts.length === 0) {
        return <PreviewContainer>No posts found.</PreviewContainer>;
    }

    return (
        <>
            <PreviewContainer>
                {posts.map((post) => (
                    <PostPreview key={post.id} id={post.id} title={post.title} rating={post.rating} date={post.date} username={post.username} profilePicture={post.profilePicture}/>
                ))}
                {error && <p>{error}</p>}
            </PreviewContainer>
        </>
    )
}