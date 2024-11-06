package com.coderunner.controller;

import com.coderunner.DTO.RunRequestDTO;
import com.coderunner.service.DockerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/run")
public class CodeRunnerController {

    private final DockerService dockerService;

    @Autowired
    public CodeRunnerController(DockerService dockerService) {
        this.dockerService = dockerService;
    }

    @PostMapping
    public ResponseEntity<String> runCode(@RequestBody RunRequestDTO request) throws Exception{
        String result = dockerService.runCode(request);
        return ResponseEntity.ok(result);
    }
}
