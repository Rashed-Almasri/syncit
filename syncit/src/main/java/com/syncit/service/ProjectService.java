package com.syncit.service;

import com.syncit.DTO.CreateProjectRequestDTO;
import com.syncit.enums.Permission;
import com.syncit.exception.ProjectNotFoundException;
import com.syncit.exception.UnauthorizedUserException;
import com.syncit.exception.UserNotFoundException;
import com.syncit.model.Project;
import com.syncit.model.User;
import com.syncit.repository.ProjectRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ProjectService {

    @Autowired
    AuthorizationService authorizationService;

    @Autowired
    JsonParsingService jsonParsingService;

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    EnrollmentService enrollmentService;

    @Transactional
    public Project createProject(CreateProjectRequestDTO createProjectRequestDTO) throws Exception {
        User user = authorizationService.getCurrentUser();
        String filePath = jsonParsingService.createJsonFile(createProjectRequestDTO.getProjectName());

        Project project = Project.builder()
                .name(createProjectRequestDTO.getProjectName())
                .filePath(filePath)
                .build();

        project = projectRepository.save(project);
        enrollmentService.createEnrollment(user.getId(), project.getId(), Permission.OWNER);

        return project;
    }

    public Map<String, Object> getProjectStructure(Long projectId) throws Exception {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Project with ID " + projectId + " not found"));

        User user = authorizationService.getCurrentUser();
        authorizationService.checkAccessAuthorization(user.getId(), projectId);

        return jsonParsingService.getProjectStructure(project.getFilePath());
    }

    public Project getProjectById(Long id) throws ProjectNotFoundException, UnauthorizedUserException, UserNotFoundException {
        User user = authorizationService.getCurrentUser();
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException("Project with ID " + id + " not found"));

        authorizationService.checkAccessAuthorization(user.getId(), project.getId());
        return project;
    }
}
