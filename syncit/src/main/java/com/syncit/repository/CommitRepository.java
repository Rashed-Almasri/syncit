package com.syncit.repository;

import com.syncit.model.Commit;
import com.syncit.model.User;
import com.syncit.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommitRepository extends JpaRepository<Commit, Long> {
    List<Commit> findByProject(Project project);
}
