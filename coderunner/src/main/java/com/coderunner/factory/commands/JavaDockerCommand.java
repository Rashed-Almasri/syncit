package com.coderunner.factory.commands;

public class JavaDockerCommand implements DockerCommand {
    @Override
    public String generateCommand(String filePath, String fileName) {
        String baseDir = "/code";
        return String.format("docker run --rm -v %s:%s/Main.java openjdk:11 /bin/bash -c \"javac %s/Main.java && java -cp %s Main\"", filePath, baseDir, baseDir, baseDir);
    }
}