package com.syncit.service;

import com.syncit.enums.Permission;
import com.syncit.exception.UnauthorizedUserException;
import com.syncit.exception.UserNotFoundException;
import com.syncit.key.EnrollmentId;
import com.syncit.model.Enrollment;
import com.syncit.model.User;
import com.syncit.repository.UserRepository;
import com.syncit.security.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthorizationService {

    @Autowired
    EnrollmentService enrollmentService;

    @Autowired
    UserRepository userRepository;

    public void isAuthorizedToShare(Long userId, Long projectId) throws Exception {
        User user = getCurrentUser();
        Optional<Enrollment> enrollmentOptional = enrollmentService.findById(new EnrollmentId(userId, projectId));
        if(enrollmentOptional.isPresent()) {
            if(enrollmentOptional.get().getPermission().equals(Permission.OWNER)){
                return;
            }
        }
        throw new UnauthorizedUserException("User is not authorized to share this project");
    }

    public void isAuthorizedToEdit(Long userId, Long projectId) throws Exception {
        User user = getCurrentUser();
        Optional<Enrollment> enrollmentOptional = enrollmentService.findById(new EnrollmentId(userId, projectId));
        if(enrollmentOptional.isPresent()) {
            if(enrollmentOptional.get().getPermission().equals(Permission.OWNER) || enrollmentOptional.get().getPermission().equals(Permission.EDIT)){
                return;
            }
        }
        throw new UnauthorizedUserException("User is not authorized to edit this project");
    }

    public boolean isAuthorizedToAccess(Long userId, Long projectId) {
        Optional<Enrollment> enrollment = enrollmentService.findById(new EnrollmentId(userId, projectId));
        return enrollment.isPresent();
    }

    public void checkAccessAuthorization(Long userId, Long projectId) throws UnauthorizedUserException {
        if (!isAuthorizedToAccess(userId, projectId)) {
            throw new UnauthorizedUserException("User is not authorized to access this project");
        }
    }

    public User getCurrentUser() throws UserNotFoundException {
        String username = SecurityUtil.getCurrentUsername();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }
}
