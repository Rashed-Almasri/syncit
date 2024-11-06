import React, { useState, useEffect, useRef } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import Editor from '@monaco-editor/react';
import NavBar from "../NavBar/NavBar";
import SockJS from 'sockjs-client';
import { Stomp } from "@stomp/stompjs";
import { ChevronRight, ChevronDown, Folder, File , Plus, Trash2 } from 'react-feather';


import pythonIcon from '../../assets/python-icon.svg';
import cPlusIcon from '../../assets/cpp.svg';
import javaScriptIcon from '../../assets/js.svg';
import htmlIcon from '../../assets/html.svg';
import javaIcon from '../../assets/java.svg';
import cssIcon from '../../assets/css.svg';
import jsonIcon from '../../assets/json.svg';

import txtIcon from '../../assets/txt2.svg';
const backendUrl = process.env.REACT_APP_BACKEND_URL;
const extensionToLanguageMap = {
  '.js': 'javascript',
  '.py': 'python',
  '.java': 'java',
  '.cpp': 'cpp',
  '.html': 'html',
  '.css': 'css',
  '.json': 'json',
  '.md': 'markdown',
};


const extensionToIconMap = {
  '.js': javaScriptIcon,    
  '.py': pythonIcon,
  '.java': javaIcon,
  '.cpp': cPlusIcon,
  '.html': htmlIcon,
  '.css': cssIcon,
  '.json': jsonIcon,
  '.md': txtIcon,
  'txt': txtIcon,  
};

const getFileExtension = (filename) => {
  try{
    return filename.slice(((filename.lastIndexOf('.') - 1) >>> 0) + 1).toLowerCase();
  }
  catch{
    return '';
  }
 
};

const FileTree = ({ structure, onFileClick, onAddFile, onAddFolder, onDeleteFile, onDeleteFolder }) => {
  const [expandedFolders, setExpandedFolders] = useState({});

  const toggleFolder = (path) => {
    setExpandedFolders(prev => ({ ...prev, [path]: !prev[path] }));
  };

  const renderTree = (node, path = '') => {
    return Object.entries(node).map(([key, value]) => {
      const currentPath = `${path}${key}`;
      const fileExtension = getFileExtension(key);
      const fileIconSrc = extensionToIconMap[fileExtension] || txtIcon; 
      if (typeof value === 'object') {
        const isExpanded = expandedFolders[currentPath];
        return (
          <div key={currentPath} className="ml-2">
            <div className="flex items-center space-x-2 mb-1">
              <span className="cursor-pointer hover:text-blue-400 flex items-center" onClick={() => toggleFolder(currentPath)}>
                {isExpanded ? <ChevronDown size={16} /> : <ChevronRight size={16} />}
                <Folder size={16} className="mr-1" />
                {key}
              </span>
              <button onClick={() => onAddFile(currentPath)} className="text-green-400 hover:text-green-200"><Plus size={14} /></button>
              <button onClick={() => onAddFolder(currentPath)} className="text-blue-400 hover:text-blue-200"><Folder size={14} /></button>
              <button onClick={() => onDeleteFolder(currentPath)} className="text-red-400 hover:text-red-200"><Trash2 size={14} /></button>
            </div>
            {isExpanded && <div className="ml-4">{renderTree(value, `${currentPath}/`)}</div>}
          </div>
        );
      } else {
        return (
          <div key={currentPath} className="flex items-center ml-6 mb-1">
            <img src={`${fileIconSrc}`} alt="file-icon" className="mr-2" width="16" height="16" />
            <span className="cursor-pointer hover:text-blue-400 flex items-center" onClick={() => onFileClick(currentPath, value)}>
              {key}
            </span>
            <button onClick={() => onDeleteFile(currentPath)} className="ml-2 text-red-400 hover:text-red-200"><Trash2 size={14} /></button>
          </div>
        );
      }
    });
  };

  return (
    <div className="bg-gray-800 p-4 h-full overflow-auto fixed-width-300">
      {renderTree(structure)}
    </div>
  );
};

const Terminal = ({ projectId, selectedFileId , filePath}) => {
  const [terminalContent, setTerminalContent] = useState('');
  const [command, setCommand] = useState('');

  const handleCommandSubmit = async (e) => {
    e.preventDefault();
    if (command.trim() === 'clear' || command.trim() === 'cls') {
      setTerminalContent('');
      setCommand('');
      return;
    }
    if (!command.trim().startsWith('sync')) {
      setTerminalContent(prev => prev + '\nERROR: the command should start with sync');
      setCommand('');
      return;
    }
    try {
      const response = await fetch(`${backendUrl}/api/terminal`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': localStorage.getItem('jwtKey')
        },
        body: JSON.stringify({
          command: command,
          projectId: Number(projectId),
          fileId: selectedFileId,
          extension: getFileExtension(filePath)
        })
      });
      if (response.ok) {
        const result = await response.text();
        setTerminalContent(prev => `${prev}\n$ ${command}\n${result}`);
      } else {
        
        setTerminalContent(prev => `${prev}\nError: ${response.status} - ${response.statusText}`);
      }
    } catch (error) {

      setTerminalContent(prev => `${prev}\nError: ${error.message}`);
    }
    setCommand('');
  };

  return (
    <div className="flex flex-col bg-black text-green-400 p-2 font-mono text-sm h-56">
      <div className="flex-1 overflow-auto whitespace-pre-wrap">{terminalContent}</div>
      <form onSubmit={handleCommandSubmit} className="flex mt-2">
        <span className="mr-2">$</span>
        <input
          type="text"
          value={command}
          onChange={(e) => setCommand(e.target.value)}
          className="flex-1 bg-transparent outline-none"
          placeholder="Enter command..."
        />
      </form>
    </div>
  );
};

const ProjectEditor = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [projectStructure, setProjectStructure] = useState({});
  const [selectedFile, setSelectedFile] = useState(null);
  const [fileContent, setFileContent] = useState('');
  const [language, setLanguage] = useState('plaintext');
  const [canEdit, setCanEdit] = useState(false);
  const editorRef = useRef(null);
  const [stompClient, setStompClient] = useState(null);

  const fetchProjectStructure = async () => {
    try {
      const response = await fetch(`${backendUrl}/api/project/${id}`, {
        headers: {
          "Authorization": localStorage.getItem('jwtKey')
        }
      });
      if (response.ok) {
        const data = await response.json();
        setProjectStructure(data);
      } else {
        console.error('Failed to fetch project structure');
      }
    } catch (error) {
      console.error('Error fetching project structure:', error);
    }
  };
  useEffect(() => {
    fetchProjectStructure();
    return () => {
      if (stompClient) stompClient.disconnect();
    };
  }, [id]);
  const [editorHeight, setEditorHeight] = useState('100%');
  const resizeTimeoutRef = useRef(null);
  const containerRef = useRef(null);

  useEffect(() => {
    const handleResize = () => {
      if (resizeTimeoutRef.current) {
        window.cancelAnimationFrame(resizeTimeoutRef.current);
      }

      resizeTimeoutRef.current = window.requestAnimationFrame(() => {
        if (containerRef.current) {
          const newHeight = containerRef.current.getBoundingClientRect().height;
          setEditorHeight(`${newHeight}px`);
        }
      });
    };

    const resizeObserver = new ResizeObserver(handleResize);
    if (containerRef.current) {
      resizeObserver.observe(containerRef.current);
    }

    return () => {
      if (resizeTimeoutRef.current) {
        window.cancelAnimationFrame(resizeTimeoutRef.current);
      }
      resizeObserver.disconnect();
    };
  }, []);

  const checkEditPermission = async () => {
    try {
      const response = await fetch(`${backendUrl}/api/auth/canedit`, {
        method: 'POST',
        headers: {
          "Authorization": localStorage.getItem('jwtKey'),
          "Content-Type": "application/json"
        },
        body: JSON.stringify({ projectId: parseInt(id) }) // Send an object with projectId
      });
      return response.status === 200;
    } catch (error) {
      console.error('Error checking edit permission:', error);
      return false;
    }
  };
  

  const webSocketConnection = (fileId) => {
    const socket = new SockJS(`${backendUrl}/ws`);
    const client = Stomp.over(socket);

    client.connect(
      { 'Authorization': localStorage.getItem('jwtKey') },
      (frame) => {

        client.subscribe(`/topic/file/${fileId}`, (message) => {
          if (message.body !== "LOCKED") setFileContent(message.body);
        });
        
        setStompClient(client);

        client.send(`/app/file/${fileId}/open`, {}, "");
      },
      (error) => {
        console.error("STOMP error:", error);
      }
    );

    return () => {
      if (client.connected) {
        client.send(`/app/file/${fileId}/close`, {}, "");
        client.disconnect();
      }
    };
  };

  const handleFileClick = async (path, fileId) => {
    if (stompClient) stompClient.disconnect();
    setSelectedFile({ path, id: fileId });

    const fileExtension = getFileExtension(path);
    const language = extensionToLanguageMap[fileExtension] || 'plaintext';
    setLanguage(language);

    const hasEditPermission = await checkEditPermission();
    setCanEdit(hasEditPermission);

    webSocketConnection(fileId);
  };

  const handleEditorChange = (value) => {
    setFileContent(value);
    if (stompClient && stompClient.connected && selectedFile) {
      stompClient.send(`/app/file/${selectedFile.id}/edit`, {}, value);
    }
  };

  const handleAddFile = async (folderPath) => {
    const fileName = prompt('Enter file name:');
    if (!fileName) return;
    const filePath = folderPath ? `${folderPath}/${fileName}` : fileName;
    try {
      const response = await fetch(`${backendUrl}/api/project/addFile`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          "Authorization": localStorage.getItem('jwtKey')
        },
        body: JSON.stringify({
          projectId: parseInt(id),
          filePath: filePath
        })
      });
      if (response.ok) fetchProjectStructure();
      else console.error('Failed to add file');
    } catch (error) {
      console.error('Error adding file:', error);
    }
  };

  const handleAddFolder = async (folderPath) => {
    const folderName = prompt('Enter folder name:');
    if (!folderName) return;
    try {
      const response = await fetch(`${backendUrl}/api/project/addFolder`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          "Authorization": localStorage.getItem('jwtKey')
        },
        body: JSON.stringify({
          projectId: parseInt(id),
          folderPath: folderPath,
          folderName: folderName
        })
      });
      if (response.ok) fetchProjectStructure();
      else console.error('Failed to add folder');
    } catch (error) {
      console.error('Error adding folder:', error);
    }
  };

  const handleDeleteFile = async (filePath) => {
    if (!window.confirm('Are you sure you want to delete this file?')) return;
    const pathParts = filePath.split('/');
    const fileName = pathParts.pop();
    const folderPath = pathParts.join('/');
    try {
      const response = await fetch(`${backendUrl}/api/project/deleteFile`, {
        method: 'DELETE',
        headers: {
          'Content-Type': 'application/json',
          "Authorization": localStorage.getItem('jwtKey')
        },
        body: JSON.stringify({
          projectId: parseInt(id),
          folderPath: folderPath,
          fileName: fileName
        })
      });
      if (response.ok) {
        fetchProjectStructure();
        if (selectedFile && selectedFile.path === filePath) {
          setSelectedFile(null);
          setFileContent('');
        }
      } else console.error('Failed to delete file');
    } catch (error) {
      console.error('Error deleting file:', error);
    }
  };

  const handleDeleteFolder = async (folderPath) => {
    if (!window.confirm('Are you sure you want to delete this folder and all its contents?')) return;
    const pathParts = folderPath.split('/');
    const folderName = pathParts.pop();
    const parentPath = pathParts.join('/');
    try {
      const response = await fetch(`${backendUrl}/api/project/deleteFolder`, {
        method: 'DELETE',
        headers: {
          'Content-Type': 'application/json',
          "Authorization": localStorage.getItem('jwtKey')
        },
        body: JSON.stringify({
          projectId: parseInt(id),
          folderPath: parentPath,
          folderName: folderName
        })
      });
      if (response.ok) {
        fetchProjectStructure();
        if (selectedFile && selectedFile.path.startsWith(folderPath)) {
          setSelectedFile(null);
          setFileContent('');
        }
      } else console.error('Failed to delete folder');
    } catch (error) {
      console.error('Error deleting folder:', error);
    }
  };
  return (
    <div className="flex flex-col h-screen bg-gray-900 text-white">
      <NavBar 
        title='SyncIT' 
        signedin={true} 
        setsignedin={() => {
          localStorage.removeItem('jwtKey');
          navigate('/');
        }} 
      />
      <div className="flex flex-1 relative">
        <div className="w-72 min-w-[200px] max-w-[400px] bg-gray-800 overflow-hidden">
          <FileTree
            structure={projectStructure}
            onFileClick={handleFileClick}
            onAddFile={handleAddFile}
            onAddFolder={handleAddFolder}
            onDeleteFile={handleDeleteFile}
            onDeleteFolder={handleDeleteFolder}
          />
        </div>
        
        <div className="flex-1 flex flex-col overflow-hidden">
          <div className="bg-gray-700 p-2 flex justify-between items-center">
            <select
              value={language}
              onChange={(e) => setLanguage(e.target.value)}
              className="bg-gray-600 text-white p-1 rounded"
            >
              <option value="javascript">JavaScript</option>
              <option value="python">Python</option>
              <option value="java">Java</option>
              <option value="cpp">C++</option>
              <option value="html">HTML</option>
              <option value="css">CSS</option>
              <option value="json">JSON</option>
              <option value="markdown">Markdown</option>
              <option value="plaintext">plaintext</option>
            </select>
            <span className="text-sm">
              {selectedFile ? `${selectedFile.path} ${!canEdit ? '(View Only)' : ''}` : 'No file selected'}
            </span>
          </div>
  
          <div className="flex-1 overflow-hidden" ref={containerRef}>
            <Editor
              height={editorHeight}
              language={language}
              value={fileContent}
              theme="vs-dark"
              onChange={canEdit ? handleEditorChange : undefined}
              onMount={(editor) => { editorRef.current = editor; }}
              options={{
                minimap: { enabled: false },
                fontSize: 14,
                lineNumbers: 'on',
                scrollBeyondLastLine: false,
                automaticLayout: true,
                readOnly: !canEdit,
                domReadOnly: !canEdit,
                cursorStyle: canEdit ? 'line' : 'block',
              }}
            />
          </div>
  
          <div className="h-56">
            <Terminal 
              projectId={id} 
              selectedFileId={selectedFile?.id} 
              filePath={selectedFile?.path} 
            />
          </div>
        </div>
      </div>
    </div>
  );
};
export default ProjectEditor;
