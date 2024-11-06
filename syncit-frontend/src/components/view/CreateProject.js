import { Dialog, Transition } from '@headlessui/react';
import { Fragment, useState } from 'react';
import InputField from "../../utils/InputField.js";
const backendUrl = process.env.REACT_APP_BACKEND_URL;

export default function CreateProject({ open, setOpen, files, setFiles }) {
    const [projectName, setProjectName] = useState('');

    function closeModal() {
        setOpen(false);
        setProjectName('');
    }

    function createProject() {
        fetch(`${backendUrl}/api/project/create`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                "Authorization": localStorage.getItem('jwtKey')
            },
            body: JSON.stringify({
                projectName: projectName 
            }),
            credentials: 'include',
        }).then(res => {
            if (!res.ok) {
                throw new Error('Failed to create project');
            }
            return res.json();
        }).then(data => {
            setFiles(oldState => [data, ...oldState]);
            closeModal(); 
        }).catch(err => {
            console.error(err);
        });
    }

    return (
        <Transition appear show={open} as={Fragment}>
            <Dialog as="div" className="relative z-10" onClose={closeModal}>
                <Transition.Child
                    as={Fragment}
                    enter="ease-out duration-300"
                    enterFrom="opacity-0"
                    enterTo="opacity-100"
                    leave="ease-in duration-200"
                    leaveFrom="opacity-100"
                    leaveTo="opacity-0"
                >
                    <div className="fixed inset-0 bg-black/50" />
                </Transition.Child>

                <div className="fixed inset-0 overflow-y-auto">
                    <div className="flex min-h-full items-center justify-center p-4 text-center">
                        <Transition.Child
                            as={Fragment}
                            enter="ease-out duration-300"
                            enterFrom="opacity-0 scale-95"
                            enterTo="opacity-100 scale-100"
                            leave="ease-in duration-200"
                            leaveFrom="opacity-100 scale-100"
                            leaveTo="opacity-0 scale-95"
                        >
                            <Dialog.Panel
                                className="w-full max-w-md transform overflow-hidden rounded-2xl bg-gray-800 p-6 text-left align-middle shadow-xl transition-all"
                            >
                                <Dialog.Title
                                    as="h3"
                                    className="text-lg font-medium leading-6 text-white"
                                >
                                    Create Project
                                </Dialog.Title>
                                <div className="mt-2">
                                    <p className="text-sm text-gray-400">
                                        Create a new Project
                                    </p>
                                    <InputField value={projectName} setValue={setProjectName} label='Project Name' type='text' />
                                </div>
                                <div className="mt-4 flex w-full justify-end">
                                    <button
                                        type="button"
                                        className="inline-flex justify-center px-4 py-2 text-sm font-medium text-white bg-blue-500 border border-transparent rounded-md hover:bg-blue-600 focus:outline-none focus-visible:ring-2 focus-visible:ring-offset-2 focus-visible:ring-blue-500"
                                        onClick={createProject}
                                    >
                                        Create
                                    </button>
                                </div>
                            </Dialog.Panel>
                        </Transition.Child>
                    </div>
                </div>
            </Dialog>
        </Transition>
    );
}
