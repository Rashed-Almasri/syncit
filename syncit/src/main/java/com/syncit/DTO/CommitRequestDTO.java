package com.syncit.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.message.LoggerNameAwareMessage;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommitRequestDTO {
    private Long projectId;
    private String message;
}
