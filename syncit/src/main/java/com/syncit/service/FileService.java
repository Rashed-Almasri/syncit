package com.syncit.service;

import com.syncit.helper.CodeTemplates;
import com.syncit.model.File;
import com.syncit.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FileService {
    @Autowired
    FileRepository fileRepository;

    public Long createFile(Long projectId){
        File file = File.builder()
                .projectId(projectId)
                .build();

        fileRepository.save(file);
        return file.getId();
    }

    public File getFileById(Long id) {
        return fileRepository.findById(id).orElseThrow(() -> new RuntimeException("File not found"));
    }

    public void updateFileContent(Long id, String content) {
        File file = getFileById(id);
        file.setContent(content);
        fileRepository.save(file);
    }

    public String getFileContent(Long id){
        File file = fileRepository.findById(id).orElseThrow(() -> new RuntimeException("File not found"));
        return file.getContent();
    }

    public void setTemplateCode(Long id, String filePath){
        StringBuilder extension = new StringBuilder();
        CodeTemplates templates = new CodeTemplates();

        for (int i = filePath.length() - 1; i >= 0; i--) {
            char currentChar = filePath.charAt(i);
            if (currentChar == '.') {
                break;
            }
            extension.append(currentChar);
        }

        String extensionStr = extension.reverse().toString();
        switch (extensionStr){
            case "java":
                updateFileContent(id, templates.getJavaTemplate());
                return;
            case "py":
                updateFileContent(id, templates.getPythonTemplate());
                return;
            case "cpp":
                updateFileContent(id, templates.getCppTemplate());
                return;
            case "js":
                updateFileContent(id, templates.getJavascriptTemplate());
                return;
            case "html":
                updateFileContent(id, templates.getHtmlTemplate());
                return;
            case "css":
                updateFileContent(id, templates.getCssTemplate());
                return;
            case "md":
                updateFileContent(id, templates.getMarkdownTemplate());
                return;
            case "json":
                updateFileContent(id, templates.getJsonTemplate());
                return;
        }
    }
}
