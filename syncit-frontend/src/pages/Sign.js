import { useState } from 'react';
import { Transition } from '@headlessui/react';
import { useNavigate } from "react-router-dom";
import InputField from "../utils/InputField.js";
import { Code } from 'lucide-react';



export default function Sign({ username, setUsername, setLoggedin }) {
    const [password, setPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [email, setEmail] = useState('');
    const [signingIn, setSigningIn] = useState(true);
    const navigate = useNavigate();
    const backendUrl = process.env.REACT_APP_BACKEND_URL;
    function handleSubmit(e) {
        if (signingIn) {
            fetch(`${backendUrl}/api/auth/login`, {
                 method: 'POST',
                 headers: {
                    'Content-Type': 'application/json'
                 },
                 body: JSON.stringify({
                    "username": username,
                    "password": password
                 }),
                 credentials: 'include',
            }).then(res => {
                if (res.status !== 200) {
                    alert('Username or Password is incorrect');
                    return;
                }
                return res.json()
            }).then(data => {
                console.log(data);
                localStorage.setItem('username', username);
                localStorage.setItem('jwtKey', data.token);
                setLoggedin(true);
                navigate('/view');
            }).catch(err => {
                console.log(err);
            })
        } else {
            fetch(`${backendUrl}/api/auth/register`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    username,
                    email,
                    password
                })
            }).then(res => {
                if (res.status !== 200) {
                    alert('Registration failed. Please try again.');
                    return;
                }
                return res.json()
            }).then(data => {
                console.log(data);
                localStorage.setItem('username', username);
                localStorage.setItem('jwtKey', data.token);
                setLoggedin(true);
                navigate('/view');
            }).catch(err => {
                console.log(err);
            })
        }
    }

    return (
        <div className='bg-gray-900 min-h-screen flex justify-center items-center p-4'>
            <div className={`transition-all duration-500 bg-gray-800 w-full max-w-4xl rounded-lg shadow-xl flex flex-col md:flex-row overflow-hidden ${signingIn ? 'h-[500px]' : 'h-[600px]'}`}>
                <div className='md:w-1/2 p-8 flex flex-col justify-center bg-gray-700'>
                    <div className='flex items-center mb-6'>
                        <Code size={40} className="text-blue-400 mr-3" />
                        <h1 className='text-4xl text-white font-bold'>SyncIT</h1>
                    </div>
                    <p className='text-gray-300 text-xl mb-4'>Collaborative Code Editing</p>
                    <p className='text-gray-400'>Join our community of developers and collaborate in real-time.</p>
                </div>
                <div className='md:w-1/2 p-8 flex flex-col justify-center bg-gray-800'>
                    <h2 className='text-2xl text-white font-semibold mb-6'>{signingIn ? 'Sign In' : 'Create Account'}</h2>
                    <div className='space-y-4'>
                        <Transition
                            show={!signingIn}
                            enter="transition-opacity duration-300"
                            enterFrom="opacity-0"
                            enterTo="opacity-100"
                            leave="transition-opacity duration-300"
                            leaveFrom="opacity-100"
                            leaveTo="opacity-0"
                            as="div"
                        >
                            <InputField value={email} setValue={setEmail} label='Email' type='email' />
                        </Transition>
                        <InputField value={username} setValue={setUsername} label='Username' type='text' />
                        <InputField value={password} setValue={setPassword} label='Password' type='password' />
                        <Transition
                            show={!signingIn}
                            enter="transition-opacity duration-300"
                            enterFrom="opacity-0"
                            enterTo="opacity-100"
                            leave="transition-opacity duration-300"
                            leaveFrom="opacity-100"
                            leaveTo="opacity-0"
                            as="div"
                        >
                            <InputField value={confirmPassword} setValue={setConfirmPassword} label='Confirm Password' type='password' />
                        </Transition>
                    </div>
                    <div className='mt-6 flex flex-col sm:flex-row justify-between items-center'>
                        <button
                            onClick={() => setSigningIn(!signingIn)}
                            className="text-blue-400 hover:text-blue-300 mb-4 sm:mb-0"
                        >
                            {signingIn ? 'Create an account' : 'Already have an account'}
                        </button>
                        <button
                            onClick={handleSubmit}
                            className="w-full sm:w-auto bg-blue-600 hover:bg-blue-700 text-white font-semibold py-2 px-6 rounded-md transition duration-300 ease-in-out"
                        >
                            {signingIn ? 'Sign In' : 'Sign Up'}
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
}