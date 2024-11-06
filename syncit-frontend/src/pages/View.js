import React, { useState, useEffect } from 'react';
import { useNavigate } from "react-router-dom";
import NavBar from "../components/NavBar/NavBar";
import Share from "../components/view/Share";
import CreateProject from "../components/view/CreateProject";
const backendUrl = process.env.REACT_APP_BACKEND_URL;

const ProjectItem = ({ project, onClick, onShare }) => {
  const [sharedWithCount, setSharedWithCount] = useState(0);

  useEffect(() => {
    const fetchSharedWithCount = async () => {
      try {
        const jwtKey = localStorage.getItem('jwtKey');
        const response = await fetch(`${backendUrl}/api/enrollment/sharedWith/${project.id}`, {
          headers: {
            "Authorization": jwtKey
          }
        });
        if (response.ok) {
          const count = await response.json();
          setSharedWithCount(count);
        } else {
          console.error(`Failed to fetch sharedWith count for project ${project.id}`);
        }
      } catch (error) {
        console.error(`Error fetching sharedWith count for project ${project.id}:`, error);
      }
    };

    fetchSharedWithCount();
  }, [project.id]);

  return (
    <div 
      className="flex justify-between items-center py-4 pl-8 rounded-lg w-full bg-gray-800 mb-4 cursor-pointer hover:bg-gray-700 transition-colors"
      onClick={() => onClick(project.id)} 
    >
      <h2 className="text-gray-300 truncate basis-6/12 text-xl font-bold">{project.name}</h2>
      <div className="basis-2/12 text-gray-400 text-sm">Shared with: {sharedWithCount}</div>
      <div className="basis-2/12">
        <button
          onClick={(e) => {
            e.stopPropagation();
            onShare(project.id, project.name);
          }}
          className="bg-blue-500 hover:bg-blue-600 text-white font-bold py-2 px-4 rounded"
        >
          Share
        </button>
      </div>
      <div className="basis-2/12 flex justify-end pr-4">
        <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
        </svg>
      </div>
    </div>
  );
};

export default function ProjectList() {
  const [projects, setProjects] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [isShareModalOpen, setIsShareModalOpen] = useState(false);
  const [selectedProject, setSelectedProject] = useState(null);
  const [isCreateProjectModalOpen, setIsCreateProjectModalOpen] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchProjects = async () => {
      let jwtKey = localStorage.getItem('jwtKey');
      if (!jwtKey) {
        navigate('/');
        return;
      }

      try {
        const response = await fetch(`${backendUrl}/api/enrollment`, {
          headers: {
            "Authorization": jwtKey
          }
        });
        if (response.status === 401 || response.status === 403) {
          localStorage.removeItem('jwtKey');
          navigate('/');
          return;
        }
        if (!response.ok) {
          throw new Error('Failed to fetch projects');
        }
        const data = await response.json();
        setProjects(data);
      } catch (err) {
        console.error('Error fetching projects:', err);
        setError('Failed to load projects. Please try again later.');
      } finally {
        setLoading(false);
      }
    };

    fetchProjects();
  }, [navigate]);

  const handleProjectClick = (projectId) => {
    navigate(`/project/${projectId}`);
  };
  

  const handleShareClick = (projectId, projectName) => {
    setSelectedProject({ id: projectId, name: projectName });
    setIsShareModalOpen(true);
  };

  const handleCreateProjectClick = () => {
    setIsCreateProjectModalOpen(true);
  };

  if (loading) {
    return (
      <div className="h-screen w-screen bg-gray-900 flex justify-center items-center">
        <div className="text-gray-300 text-xl">Loading projects...</div>
      </div>
    );
  }

  return (
    <>
      <NavBar title='SyncIT' signedin={true} setsignedin={() => {
        localStorage.removeItem('jwtKey');
        navigate('/');
      }} />
      <div className="bg-gray-900 flex flex-col justify-top items-center p-4 min-h-screen">
        <div className="w-full max-w-4xl">
          <div className="flex justify-between mb-6">
            <h1 className="text-gray-300 font-bold text-3xl">Your Projects</h1>
            <button
              onClick={handleCreateProjectClick}
              className="bg-green-500 hover:bg-green-600 text-white font-bold py-2 px-4 rounded"
            >
              + Create Project
            </button>
          </div>

          {error && (
            <div className="bg-red-500 text-white p-4 rounded-md mb-4">{error}</div>
          )}

          {!error && projects.length === 0 && (
            <div className="text-gray-300 text-center">No projects found.</div>
          )}

          {projects.map((project) => (
            <ProjectItem 
              key={project.id} 
              project={project} 
              onClick={handleProjectClick}
              onShare={handleShareClick}
            />
          ))}
        </div>
      </div>

      {selectedProject && (
        <Share
          open={isShareModalOpen}
          setOpen={setIsShareModalOpen}
          title={selectedProject.name}
          projectId={selectedProject.id}
          setProjects={setProjects}
        />
      )}

      <CreateProject
        open={isCreateProjectModalOpen}
        setOpen={setIsCreateProjectModalOpen}
        files={projects}
        setFiles={setProjects}
      />
    </>
  );
}
