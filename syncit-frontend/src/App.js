import { BrowserRouter, Routes, Route, useNavigate } from "react-router-dom";
import Sign from './pages/Sign';
import ProjectList from './pages/View.js';
import ProjectEditor from './components/test/ProjectEditor.js';
import ScrollToTop from "./utils/ScrollToTop.js";
import { useEffect, useState } from "react";

function App() {
    const [username, setUsername] = useState('');
    const [loggedin, setLoggedin] = useState(false);
    const [jwtKey, setJwtKey] = useState('');

    useEffect(() => {
        const storedJwtKey = localStorage.getItem('jwtKey');
        if (storedJwtKey) {
            setJwtKey(storedJwtKey);
        }
    }, []);

    return (
        <BrowserRouter>
            <ScrollToTop />
            <Routes>
                <Route path="/" element={<Sign setUsername={setUsername} username={username} setLoggedin={setLoggedin} />} />
                <Route path="/view" element={<ProjectList />} />
                <Route path="/project/:id" element={<ProjectEditor />} />
                </Routes>
        </BrowserRouter>
    );
}

export default App;
