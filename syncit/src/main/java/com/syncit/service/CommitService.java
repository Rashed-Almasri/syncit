package com.syncit.service;

import com.syncit.model.Commit;
import com.syncit.model.File;
import com.syncit.model.Project;
import com.syncit.model.User;
import com.syncit.repository.CommitRepository;
import com.syncit.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class CommitService {

    private final JsonParsingServiceImpl jsonParsingService;
    private final FileService fileService;
    private final CommitRepository commitRepository;
    private final ProjectRepository projectRepository;

    @Autowired
    public CommitService(JsonParsingServiceImpl jsonParsingService,
                         FileService fileService,
                         CommitRepository commitRepository,
                         ProjectRepository projectRepository) {
        this.jsonParsingService = jsonParsingService;
        this.fileService = fileService;
        this.commitRepository = commitRepository;
        this.projectRepository = projectRepository;
    }

    @Transactional
    public Commit createCommit(User user, Project project, String message) {
        Map<String, Object> projectStructure = jsonParsingService.getProjectStructure(project.getFilePath());
        String commitFilePath = jsonParsingService.createJsonFile("commit_" + project.getName());
        jsonParsingService.saveJsonFile(commitFilePath, projectStructure);
        updateFileContents(projectStructure, commitFilePath);

        Commit commit = Commit.builder()
                .user(user)
                .project(project)
                .message(message)
                .filePath(commitFilePath)
                .build();

        commit = commitRepository.save(commit);
        project.setCurrentCommit(commit);
        projectRepository.save(project);

        return commit;
    }

    private void updateFileContents(Map<String, Object> projectStructure, String commitFilePath) {
        processFolderContents(projectStructure, "", commitFilePath);
    }

    private void processFolderContents(Map<String, Object> folder, String path, String commitFilePath) {
        for (Map.Entry<String, Object> entry : folder.entrySet()) {
            String name = entry.getKey();
            Object value = entry.getValue();

            if (value instanceof Integer) {
                Long fileId =  Long.valueOf(value.toString());
                String fileContent = fileService.getFileContent(fileId);
                Long newFileId = fileService.createFile(fileService.getFileById(fileId).getProjectId());
                fileService.updateFileContent(newFileId, fileContent);
                jsonParsingService.addFile(commitFilePath, path.substring(1) + "/" + name, newFileId);
            } else if (value instanceof Map) {
                processFolderContents((Map<String, Object>) value, path + "/" + name, commitFilePath);
            }
        }
    }

    @Transactional
    public void addToCommit(Project project, String filePath) {
        Map<String, Object> projectStructure = jsonParsingService.getProjectStructure(project.getFilePath());

        if (".".equals(filePath)) {
            processFolderContents(projectStructure, "", project.getFilePath());
        } else {
            processSpecificPath(projectStructure, filePath, project.getFilePath());
        }

        jsonParsingService.saveJsonFile(project.getFilePath(), projectStructure);
    }

    private void processSpecificPath(Map<String, Object> projectStructure, String filePath, String projectFilePath) {
        String[] pathParts = filePath.split("/");
        Map<String, Object> currentFolder = projectStructure;

        for (int i = 0; i < pathParts.length - 1; i++) {
            currentFolder = (Map<String, Object>) currentFolder.get(pathParts[i]);
        }

        String fileName = pathParts[pathParts.length - 1];
        Object value = currentFolder.get(fileName);

        if (value instanceof Long) {
            jsonParsingService.addFile(projectFilePath, filePath, (Long) value);
        } else if (value instanceof Map) {
            processFolderContents((Map<String, Object>) value, filePath, projectFilePath);
        }
    }

    @Transactional
    public void revertToCommit(Project project, Long commitId) {
        Commit commit = commitRepository.findById(commitId)
                .orElseThrow(() -> new RuntimeException("Commit not found"));

        if (!commit.getProject().getId().equals(project.getId())) {
            throw new RuntimeException("Commit does not belong to the specified project");
        }

        project.setFilePath(commit.getFilePath());
        projectRepository.save(project);
    }
}
