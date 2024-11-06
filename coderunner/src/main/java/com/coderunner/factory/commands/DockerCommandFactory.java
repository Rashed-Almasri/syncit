package com.coderunner.factory.commands;

public class DockerCommandFactory {
    public DockerCommand getDockerCommand(String extension) {
        switch (extension) {
            case ".py":
                return new PythonDockerCommand();
            case ".java":
                return new JavaDockerCommand();
            case ".cpp":
                return new CppDockerCommand();
            default:
                throw new UnsupportedOperationException("Unsupported language: " + extension);
        }
    }
}