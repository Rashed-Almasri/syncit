package com.syncit.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.syncit.enums.Permission;
import com.syncit.key.EnrollmentId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "enrollments")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Enrollment {
    @EmbeddedId
    private EnrollmentId id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @MapsId("projectId")
    @JsonIgnore
    @JoinColumn(name = "project_id")
    private Project project;

    @Column(nullable = false)
    private Permission permission;
}
