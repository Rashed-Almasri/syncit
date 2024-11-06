package com.coderunner.factory.commands;

public class CppDockerCommand implements DockerCommand {
    @Override
    public String generateCommand(String filePath, String fileName) {
        String baseDir = "/code";
        return String.format("docker run --rm -v %s:%s/%s.cpp gcc:latest /bin/bash -c \"g++ %s/%s.cpp -o %s/%s && %s/%s\"", filePath, baseDir, fileName, baseDir, fileName, baseDir, fileName, baseDir, fileName);
    }
}
