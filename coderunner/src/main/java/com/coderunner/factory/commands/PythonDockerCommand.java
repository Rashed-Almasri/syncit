package com.coderunner.factory.commands;

public class PythonDockerCommand implements DockerCommand {
    @Override
    public String generateCommand(String filePath, String fileName) {
        String baseDir = "/code";
        return String.format("docker run --rm -v %s:%s/script.py python:3.9-slim python %s/script.py", filePath, baseDir, baseDir);
    }
}