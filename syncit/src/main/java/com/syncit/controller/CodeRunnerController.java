package com.syncit.controller;

import com.syncit.DTO.RunCodeDTO;
import com.syncit.service.CodeRunnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/run")
@RequiredArgsConstructor
@CrossOrigin("*")
public class CodeRunnerController {
    @Autowired
    private CodeRunnerService codeRunnerService;

    @PostMapping
    public ResponseEntity<?> run(@RequestBody RunCodeDTO runCodeDTO) throws Exception {
        return ResponseEntity.ok(codeRunnerService.run(runCodeDTO));
    }
}
