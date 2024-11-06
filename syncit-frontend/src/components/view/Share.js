import { Dialog, Transition, Listbox } from '@headlessui/react'
import { Fragment, useState } from 'react'
import ArrowDropDownRoundedIcon from '@mui/icons-material/ArrowDropDownRounded';
import InputField from "../../utils/InputField.js";
const backendUrl = process.env.REACT_APP_BACKEND_URL;

const PERMISSIONS = {
  EDIT: 1,
  VIEW: 2
};

export default function Share({ open, setOpen, title, projectId, setProjects }) {
    const [user, setUser] = useState('');
    const [permission, setPermission] = useState('View');

    function closeModal() {
        setOpen(false)
    }

    function shareProject() {
        const requestBody = {
            projectId: projectId,
            username: user,
            permission: permission === 'View' ? PERMISSIONS.VIEW : PERMISSIONS.EDIT
        };
        fetch(`${backendUrl}/api/enrollment/add`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                "Authorization": localStorage.getItem('jwtKey')
            },
            body: JSON.stringify(requestBody),
        }).then(res => {
            if (!res.ok) {
                throw new Error('Network response was not ok');
            }
        }).then(data => {
            console.log("Project shared successfully:", data);

        }).catch(err => {
            console.error("Error sharing project:", err);
        });
        closeModal();
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
                    <div className="fixed inset-0 bg-black/50"/>
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
                                className="w-full max-w-md transform overflow-hidden rounded-2xl bg-[#1e1e1e] p-6 text-left align-middle shadow-xl transition-all"
                            >
                                <Dialog.Title
                                    as="h3"
                                    className="text-lg font-medium leading-6 text-[#d4d4d4]"
                                >
                                    Collaborate on &apos;{title}&apos;
                                </Dialog.Title>
                                <div className="mt-4">
                                    <InputField value={user} setValue={setUser} label='Collaborator Username' type='text'/>
                                </div>
                                <div className="mt-4">
                                    <label className="text-sm text-gray-400">
                                        Access Level
                                    </label>
                                    <Listbox value={permission} onChange={setPermission}>
                                        <div className="relative mt-1">
                                            <Listbox.Button className="relative w-full cursor-default rounded-lg bg-[#252526] py-2 pl-3 pr-10 text-left shadow-md text-[#d4d4d4] focus:outline-none focus-visible:border-blue-500 focus-visible:ring-2 focus-visible:ring-blue-500 sm:text-sm">
                                                <span className="block truncate">{permission}</span>
                                                <span className="pointer-events-none absolute inset-y-0 right-0 flex items-center pr-2">
                                                    <ArrowDropDownRoundedIcon
                                                        className="h-5 w-5 text-gray-400"
                                                        aria-hidden="true"
                                                    />
                                                </span>
                                            </Listbox.Button>
                                            <Transition
                                                as={Fragment}
                                                leave="transition ease-in duration-100"
                                                leaveFrom="opacity-100"
                                                leaveTo="opacity-0"
                                            >
                                                <Listbox.Options className="absolute mt-1 max-h-60 w-full overflow-auto rounded-md bg-[#252526] py-1 text-base shadow-lg ring-1 ring-black ring-opacity-5 focus:outline-none sm:text-sm">
                                                    {['View', 'Edit'].map((level) => (
                                                        <Listbox.Option
                                                            key={level}
                                                            className={({ active }) =>
                                                                `relative cursor-default select-none py-2 pl-10 pr-4 ${
                                                                    active ? 'bg-blue-500 text-white' : 'text-[#d4d4d4]'
                                                                }`
                                                            }
                                                            value={level}
                                                        >
                                                            {({ selected }) => (
                                                                <span
                                                                    className={`block truncate ${
                                                                        selected ? 'font-medium' : 'font-normal'
                                                                    }`}
                                                                >
                                                                    {level}
                                                                </span>
                                                            )}
                                                        </Listbox.Option>
                                                    ))}
                                                </Listbox.Options>
                                            </Transition>
                                        </div>
                                    </Listbox>
                                </div>
                                <div className="mt-6 flex justify-end space-x-2">
                                    <button
                                        type="button"
                                        className="inline-flex justify-center rounded-md bg-gray-600 px-4 py-2 text-sm font-medium text-white hover:bg-gray-500 focus:outline-none focus-visible:ring-2 focus-visible:ring-blue-500 focus-visible:ring-offset-2"
                                        onClick={closeModal}
                                    >
                                        Cancel
                                    </button>
                                    <button
                                        type="button"
                                        className="inline-flex justify-center rounded-md bg-blue-600 px-4 py-2 text-sm font-medium text-white hover:bg-blue-500 focus:outline-none focus-visible:ring-2 focus-visible:ring-blue-500 focus-visible:ring-offset-2"
                                        onClick={shareProject}
                                    >
                                        Share
                                    </button>
                                </div>
                            </Dialog.Panel>
                        </Transition.Child>
                    </div>
                </div>
            </Dialog>
        </Transition>
    )
}
