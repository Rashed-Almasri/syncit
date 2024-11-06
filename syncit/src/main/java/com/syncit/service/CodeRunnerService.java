package com.syncit.service;

import com.syncit.DTO.RunCodeDTO;
import com.syncit.DTO.RunCodeRequestDTO;
import com.syncit.model.File;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


@Service
public class CodeRunnerService {
    @Autowired
    FileService fileService;
    @Autowired
    private RestTemplate restTemplate;

    public String run(RunCodeDTO runCodeDTO) throws Exception {
        File file = fileService.getFileById(runCodeDTO.getFileId());
        String url = "http://localhost:8081/api/run";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        RunCodeRequestDTO runCodeRequestDTO = new RunCodeRequestDTO();
        runCodeRequestDTO.setCode(file.getContent());
        runCodeRequestDTO.setExtension(runCodeDTO.getExtension());

        HttpEntity<RunCodeRequestDTO> requestEntity = new HttpEntity<>(runCodeRequestDTO, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
            return response.getBody();
        }
        catch (Exception e) {
            return "ERROR RUNNING THE CODE" + e.getMessage();
        }
    }

}
