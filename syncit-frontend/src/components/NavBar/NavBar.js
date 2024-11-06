import docsIcon from '../../assets/syncit.jpeg';
import { useNavigate } from "react-router-dom";
import { useState } from "react";
import Popover from '@mui/material/Popover';

export default function NavBar({ title, signedin, setsignedin, usernames }) {
    const username = localStorage.getItem('username');
    const navigate = useNavigate();

    const [anchorEl, setAnchorEl] = useState(null);
    const open = Boolean(anchorEl);
    const id = open ? 'simple-popover' : undefined;

    const handleClick = (event) => {
        setAnchorEl(event.currentTarget);
    };

    const handleClose = () => {
        setAnchorEl(null);
    };

    return (
        <>
            <div className="sticky top-0 shadow-md z-40 flex justify-between items-center p-4 bg-gray-800">
                <div className="flex items-center gap-4 text-white">
                    <img src={docsIcon} alt="Docs" width={40} height={40} />
                    <h1 className="text-2xl text-white font-bold">
                        {title}
                    </h1>
                </div>
                <div className="flex items-center gap-4">
                    {usernames && (
                        <>
                            <button
                                aria-describedby={id}
                                onClick={handleClick}
                                className="hover:bg-blue-600 text-white px-4 py-2 rounded-3xl shadow-md bg-blue-500"
                            >
                                Active Users {usernames.length}
                            </button>
                            <Popover
                                id={id}
                                open={open}
                                anchorEl={anchorEl}
                                onClose={handleClose}
                                anchorOrigin={{
                                    vertical: 'bottom',
                                    horizontal: 'left',
                                }}
                            >
                                <div className="flex flex-col p-4 bg-gray-700">
                                    {usernames.map((user, index) => (
                                        <p key={index} className="text-gray-300 text-lg font-bold">
                                            {user}
                                        </p>
                                    ))}
                                </div>
                            </Popover>
                        </>
                    )}
                    <button
                        onClick={() => {
                            setsignedin(true);
                            localStorage.removeItem('username');
                            localStorage.removeItem('jwtKey');
                            navigate('/');
                        }}
                        className="hover:bg-blue-600 text-white px-4 py-2 rounded-3xl shadow-md bg-blue-500"
                    >
                        Sign out
                    </button>
                    <p className="text-gray-300 text-lg font-bold">
                        {username}
                    </p>
                </div>
            </div>
        </>
    );
}
