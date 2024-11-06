package com.syncit.service;


import java.util.Map;

public interface JsonParsingService {

    String createJsonFile(String projectName);

    void addFolder(String filePath, String jsonPath, String folderName);

    void addFile(String filePath, String jsonPath, Long fileId);

    void deleteFile(String filePath, String jsonPath, String fileName);

    void deleteFolder(String filePath, String jsonPath, String folderName);

    String getFileId(String filePath, String jsonPath);

    Map<String, Object> getProjectStructure(String filePath);
}
