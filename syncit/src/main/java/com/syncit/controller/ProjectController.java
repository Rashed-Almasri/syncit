package com.syncit.controller;

import com.syncit.DTO.*;
import com.syncit.exception.ProjectNotFoundException;
import com.syncit.exception.UnauthorizedUserException;
import com.syncit.model.Project;
import com.syncit.service.FileManagerService;
import com.syncit.service.JsonParsingService;
import com.syncit.service.ProjectService;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/project")
@RequiredArgsConstructor
public class ProjectController {

    @Autowired
    ProjectService projectService;
    @Autowired
    FileManagerService fileManagerService;

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody CreateProjectRequestDTO createProjectRequestDTO) throws Exception {
        Project project = projectService.createProject(createProjectRequestDTO);
        return ResponseEntity.ok(project);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProjectStructure(@PathVariable("id") Long id) throws Exception {
        Map<String, Object> projectStructure = projectService.getProjectStructure(id);
        return ResponseEntity.ok(projectStructure);
    }

    @PostMapping("/addFile")
    public ResponseEntity<?> addFile(@RequestBody AddFileRequestDTO addFileRequestDTO) throws Exception {
        fileManagerService.addFile(addFileRequestDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/addFolder")
    public ResponseEntity<?> addFolder(@RequestBody AddFolderRequestDTO addFolderRequestDTO) throws Exception {
        fileManagerService.addFolder(addFolderRequestDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/deleteFolder")
    public ResponseEntity<?> deleteFolder(@RequestBody DeleteFolderDTO deleteFolderDTO) throws Exception {
        fileManagerService.deleteFolder(deleteFolderDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/deleteFile")
    public ResponseEntity<?> deleteFile(@RequestBody DeleteFileDTO deleteFileDTO) throws Exception{
        fileManagerService.deleteFile(deleteFileDTO);
        return ResponseEntity.ok().build();
    }
}
