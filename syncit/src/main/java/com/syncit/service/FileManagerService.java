package com.syncit.service;

import com.syncit.DTO.*;
import com.syncit.exception.ProjectNotFoundException;
import com.syncit.model.Project;
import com.syncit.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

@Service
public class FileManagerService {

    @Autowired
    JsonParsingService jsonParsingService;

    @Autowired
    FileService fileService;

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    AuthorizationService authorizationService;

    @Transactional
    public void addFile(AddFileRequestDTO addFileRequestDTO) throws Exception {
        authorizationService.isAuthorizedToEdit(authorizationService.getCurrentUser().getId(), addFileRequestDTO.getProjectId());
        Project project = projectRepository.findById(addFileRequestDTO.getProjectId()).orElseThrow(() ->new ProjectNotFoundException("Project with ID " + addFileRequestDTO.getProjectId() + " not found"));
        Long fileId = fileService.createFile(addFileRequestDTO.getProjectId());
        jsonParsingService.addFile(
                project.getFilePath(),
                addFileRequestDTO.getFilePath(),
                fileId
        );
        fileService.setTemplateCode(fileId, addFileRequestDTO.getFilePath());
    }

    public void addFolder(AddFolderRequestDTO addFolderRequestDTO) throws Exception {
        authorizationService.isAuthorizedToEdit(authorizationService.getCurrentUser().getId(), addFolderRequestDTO.getProjectId());
        Project project = projectRepository.findById(addFolderRequestDTO.getProjectId()).orElseThrow(() ->new ProjectNotFoundException("Project with ID " + addFolderRequestDTO.getProjectId() + " not found"));

        jsonParsingService.addFolder(project.getFilePath(), addFolderRequestDTO.getFolderPath(), addFolderRequestDTO.getFolderName());
    }

    public void deleteFile(DeleteFileDTO deleteFileDTO) throws Exception {
        authorizationService.isAuthorizedToEdit(authorizationService.getCurrentUser().getId(), deleteFileDTO.getProjectId());
        Project project = projectRepository.findById(deleteFileDTO.getProjectId()).orElseThrow(() ->new ProjectNotFoundException("Project with ID " + deleteFileDTO.getProjectId() + " not found"));

        jsonParsingService.deleteFile(project.getFilePath(), deleteFileDTO.getFolderPath(), deleteFileDTO.getFileName());
    }

    public void deleteFolder(DeleteFolderDTO deleteFolderDTO) throws Exception {
        authorizationService.isAuthorizedToEdit(authorizationService.getCurrentUser().getId(), deleteFolderDTO.getProjectId());
        Project project = projectRepository.findById(deleteFolderDTO.getProjectId()).orElseThrow(() ->new ProjectNotFoundException("Project with ID " + deleteFolderDTO.getProjectId() + " not found"));

        jsonParsingService.deleteFolder(project.getFilePath(), deleteFolderDTO.getFolderPath(), deleteFolderDTO.getFolderName());
    }


}
