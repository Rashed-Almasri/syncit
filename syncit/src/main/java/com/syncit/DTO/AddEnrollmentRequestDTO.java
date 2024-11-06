package com.syncit.DTO;

import com.syncit.enums.Permission;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddEnrollmentRequestDTO {
    Long projectId;
    String username;
    int permission;
}
