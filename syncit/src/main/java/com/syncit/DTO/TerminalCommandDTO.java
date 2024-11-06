package com.syncit.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class TerminalCommandDTO {
    private String command;
    private Long projectId;
    private Long fileId;
    private String extension;
}
