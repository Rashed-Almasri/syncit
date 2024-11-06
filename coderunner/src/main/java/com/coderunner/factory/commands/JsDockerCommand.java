package com.coderunner.factory.commands;

public class JsDockerCommand implements DockerCommand {
    @Override
    public String generateCommand(String filePath, String fileName) {
        String baseDir = "/code";
        return String.format("docker run --rm -v %s:%s/script.js node:14-alpine node %s/script.js", filePath, baseDir, baseDir);
    }
}