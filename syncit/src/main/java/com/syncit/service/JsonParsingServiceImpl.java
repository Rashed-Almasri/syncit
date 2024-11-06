package com.syncit.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class JsonParsingServiceImpl implements JsonParsingService {

    private static final String PROJECTS_DIR = ".\\src\\main\\resources\\projects\\";

    private final ObjectMapper objectMapper;

    public JsonParsingServiceImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String generateFileName(String name) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String timestamp = now.format(formatter);

        Random random = new Random();
        int randomNum = 10000 + random.nextInt(90000);

        return timestamp + randomNum + name;
    }

    @Override
    public String createJsonFile(String projectName) {
        String filePath = PROJECTS_DIR + generateFileName(projectName) + ".json";
        Map<String, Object> projectStructure = new LinkedHashMap<>();
        projectStructure.put(projectName, new LinkedHashMap<>());

        saveJsonFile(filePath, projectStructure);
        return filePath;
    }

    public void addFolder(String filePath, String jsonPath, String folderName) {
        Map<String, Object> projectStructure = loadJsonFile(filePath);

        Map<String, Object> targetFolder = traversePath(projectStructure, jsonPath);

        targetFolder.put(folderName, new HashMap<String, Object>());

        saveJsonFile(filePath, projectStructure);
    }


    @Override
    public void addFile(String filePath, String jsonPath, Long fileId) {
        Map<String, Object> projectStructure = loadJsonFile(filePath);

        String[] pathElements = jsonPath.split("/");

        StringBuilder folderPath = new StringBuilder();
        for (int i = 0; i < pathElements.length - 1; i++) {
            if (i > 0) {
                folderPath.append("/");
            }
            folderPath.append(pathElements[i]);
        }

        Map<String, Object> targetFolder = traversePath(projectStructure, folderPath.toString());

        String fileName = pathElements[pathElements.length - 1];

        targetFolder.put(fileName, fileId);

        saveJsonFile(filePath, projectStructure);
    }

    @Override
    public void deleteFile(String filePath, String folderPath, String fileName) {
        Map<String, Object> projectStructure = loadJsonFile(filePath);

        Map<String, Object> parentFolder = traversePath(projectStructure, folderPath);

        if (parentFolder != null) {
            if (parentFolder.containsKey(fileName)) {
                parentFolder.remove(fileName);
            } else {
                throw new RuntimeException("File not found: " + fileName);
            }
        } else {
            throw new RuntimeException("Path not found: " + folderPath);
        }
        saveJsonFile(filePath, projectStructure);
    }

    @Override
    public void deleteFolder(String filePath, String folderPath, String folderName) {
        Map<String, Object> projectStructure = loadJsonFile(filePath);

        String fullPath = folderPath.endsWith("/") ? folderPath + folderName : folderPath + "/" + folderName;

        String parentPath = fullPath.substring(0, fullPath.lastIndexOf('/'));
        Map<String, Object> parentFolder = traversePath(projectStructure, parentPath);

        String[] pathElements = fullPath.split("/");
        String targetFolderName = pathElements[pathElements.length - 1];

        if (parentFolder != null) {
            parentFolder.remove(targetFolderName);
        }

        saveJsonFile(filePath, projectStructure);
    }



    @Override
    public String getFileId(String filePath, String jsonPath) {
        Map<String, Object> projectStructure = loadJsonFile(filePath);
        Map<String, Object> targetFolder = traversePath(projectStructure, jsonPath);

        String[] pathElements = jsonPath.split("/");
        String fileName = pathElements[pathElements.length - 1];

        return (String) targetFolder.get(fileName);
    }

    @Override
    public Map<String, Object> getProjectStructure(String filePath) {
        return loadJsonFile(filePath);
    }

    public Map<String, Object> loadJsonFile(String filePath) {
        try {
            return objectMapper.readValue(new File(filePath), Map.class);
        } catch (IOException e) {
            throw new RuntimeException("Error reading JSON file", e);
        }
    }

    public void saveJsonFile(String filePath, Map<String, Object> data) {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(filePath), data);
        } catch (IOException e) {
            throw new RuntimeException("Error saving JSON file", e);
        }
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> traversePath(Map<String, Object> projectStructure, String jsonPath) {
        String[] pathElements = jsonPath.split("/");

        Map<String, Object> currentFolder = projectStructure;

        for (String folder : pathElements) {
            currentFolder.putIfAbsent(folder, new HashMap<String, Object>());

            currentFolder = (Map<String, Object>) currentFolder.get(folder);

            if (currentFolder == null) {
                throw new IllegalArgumentException("Path not found: " + folder);
            }
        }

        return currentFolder;
    }

    public List<Long> getAllFileIds(String filePath, String folderPath) {
        Map<String, Object> projectStructure = loadJsonFile(filePath);
        Map<String, Object> targetFolder = traversePath(projectStructure, folderPath);

        List<Long> fileIds = new ArrayList<>();
        collectFileIds(targetFolder, fileIds);

        return fileIds;
    }

    private void collectFileIds(Map<String, Object> currentFolder, List<Long> fileIds) {
        for (Map.Entry<String, Object> entry : currentFolder.entrySet()) {
            Object value = entry.getValue();

            if (value instanceof Long) {
                fileIds.add((Long) value);
            } else if (value instanceof Map) {
                collectFileIds((Map<String, Object>) value, fileIds);
            }
        }
    }
}