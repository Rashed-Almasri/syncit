package com.syncit.repository;

import com.syncit.key.EnrollmentId;
import com.syncit.model.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EnrollmentRepository extends JpaRepository<Enrollment, EnrollmentId> {
    List<Enrollment> findByUserId(long userId);
    List<Enrollment> findByProjectId(long projectId);
}
