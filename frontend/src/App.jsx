import {createBrowserRouter,RouterProvider} from 'react-router-dom'
import Home from './pages/Home'
import Post from './pages/Post'
import Profile from './pages/Profile'
import Register from './pages/Register'
import axios from 'axios'


async function postLoader({params}) {
    return (await axios.get(`http://localhost:8080/post/${params.postURI}`)).data
}


export default function App() {
    const router = createBrowserRouter([
        {
            path: "/",
            element: <Home/>
        },
        {
            path: "/post/:postURI",
            element: <Post/>,
            loader: postLoader
        },
        {
            path: "/:username",
            element: <Profile/>
        },
        {
            path: "/register",
            element: <Register/>
        }
    ]);
    return (
        <RouterProvider router={router}/>
    )
}