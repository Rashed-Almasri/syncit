package com.syncit.service;

import com.syncit.DTO.AddEnrollmentRequestDTO;
import com.syncit.enums.Permission;
import com.syncit.exception.ProjectNotFoundException;
import com.syncit.exception.UnauthorizedUserException;
import com.syncit.key.EnrollmentId;
import com.syncit.model.Enrollment;
import com.syncit.model.Project;
import com.syncit.model.User;
import com.syncit.repository.EnrollmentRepository;
import com.syncit.repository.ProjectRepository;
import com.syncit.repository.UserRepository;
import com.syncit.security.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    @Autowired
    public EnrollmentService(EnrollmentRepository enrollmentRepository,
                             UserRepository userRepository,
                             ProjectRepository projectRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
    }

    public Enrollment createEnrollment(Long userId, Long projectId, Permission permission) {
        User user = findUserById(userId);
        Project project = findProjectById(projectId);

        Enrollment enrollment = Enrollment.builder()
                .id(new EnrollmentId(userId, projectId))
                .user(user)
                .project(project)
                .permission(permission)
                .build();

        return enrollmentRepository.save(enrollment);
    }

    public Optional<Enrollment> findById(EnrollmentId enrollmentId) {
        return enrollmentRepository.findById(enrollmentId);
    }

    public List<Project> getAllProjectsByUserId() {
        User user = getCurrentUser();

        List<Enrollment> enrollments = enrollmentRepository.findByUserId(user.getId());
        List<Project> projects = new ArrayList<>();

        for (Enrollment enrollment : enrollments) {
            projects.add(enrollment.getProject());
        }
        return projects;
    }

    public int sharedWithNumber(Long projectId) {
        List<Enrollment> enrollments = enrollmentRepository.findByProjectId(projectId);
        return Math.max(0, enrollments.size() - 1);
    }

    public Permission permissionMapper(int p) {
        if(p == 0){
            return Permission.OWNER;
        }
        if(p == 1){
            return Permission.EDIT;
        }
        return Permission.VIEW;
    }

    public void addEnrollment(AddEnrollmentRequestDTO requestDTO) throws ProjectNotFoundException, UnauthorizedUserException {
        User currentUser = getCurrentUser();
        Project project = findProjectById(requestDTO.getProjectId());
        User userToAdd = findUserByUsername(requestDTO.getUsername());

        if(isAuthorizedToShare(currentUser, project)){
            createEnrollment(userToAdd.getId(), project.getId(), permissionMapper(requestDTO.getPermission()));
        }
        throw new UnauthorizedUserException("User " + currentUser.getUsername() + " is not authorized to share the project");
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedUserException("User with ID " + userId + " not found"));
    }

    private Project findProjectById(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Project with ID " + projectId + " not found"));
    }

    private User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UnauthorizedUserException("User " + username + " not found"));
    }

    private boolean isAuthorizedToShare(User user, Project project) {
        EnrollmentId enrollmentId = new EnrollmentId(user.getId(), project.getId());
        Optional<Enrollment> enrollment = enrollmentRepository.findById(enrollmentId);

        if(enrollment.isPresent()){
            return enrollment.get().getPermission().equals(Permission.OWNER);
        }

        return false;
    }

    private User getCurrentUser() {
        String username = SecurityUtil.getCurrentUsername();
        return findUserByUsername(username);
    }
}
