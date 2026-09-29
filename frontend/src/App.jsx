import {useEffect} from 'react'
import {createBrowserRouter,RouterProvider} from 'react-router-dom'
import Home from './pages/Home'
import Post from './pages/Post'
import Profile from './pages/Profile'
import Register from './pages/Register'
import Login from './pages/Login'
import CreatePost from './pages/CreatePost'
import EditProfile from './pages/EditProfile'
import axios from 'axios'


async function postLoader({params}) {
    return (await axios.get(`/post/${params.postURI}`)).data
}

async function profileLoader({params}) {
    return (await axios.get(`/user/${params.username}`)).data
}

export default function App() {

    useEffect(() => {
        if (!localStorage.getItem('token')) {
            return;
        }

        axios.get('/auth/verify');
    }, []);


    const router = createBrowserRouter([
        {
            path: "/",
            element: <Home/>
        },
        {
            path: "/edit-profile",
            element: <EditProfile/>
        },
        {
            path: "/post/create",
            element: <CreatePost/>
        },
        {
            path: "/post/:postURI",
            element: <Post/>,
            loader: postLoader
        },
        {
            path: "/:username",
            element: <Profile/>,
            loader: profileLoader
        },
        {
            path: "/register",
            element: <Register/>
        },
        {
            path: "/login",
            element: <Login/>
        }
    ]);
    return (
        <RouterProvider router={router}/>
    )
}