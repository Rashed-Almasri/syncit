package com.syncit.DTO;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RunCodeDTO {
    private Long fileId;
    private String extension;
}
