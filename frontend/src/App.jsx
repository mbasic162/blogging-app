import {BrowserRouter as Router, Routes, Route} from 'react-router-dom'
import Home from './pages/Home'
import Post from './pages/Post'
import Profile from './pages/Profile'

export default function App() {
    return (
        <Router>
            <Routes>
                <Route path="/" element={<Home/>}/>
                <Route path="/post/:postURI" element={<Post/>}/>
                <Route path="/:username" element={<Profile/>}/>
            </Routes>
        </Router>
    )
}