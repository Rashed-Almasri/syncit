package com.coderunner.service;

import com.coderunner.DTO.RunRequestDTO;
import com.coderunner.factory.commands.DockerCommand;
import com.coderunner.factory.commands.DockerCommandFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class DockerService {

    private final DockerCommandFactory commandFactory = new DockerCommandFactory();

    @Autowired
    OsService osService;

    public String runCode(RunRequestDTO request) {
        String code = request.getCode();
        String extension = request.getExtension();
        String fileName = UUID.randomUUID().toString();

        File codeFile;
        try {
            codeFile = createTemporaryFile(fileName, extension, code);
            DockerCommand dockerCommand = commandFactory.getDockerCommand(extension);
            String command = dockerCommand.generateCommand(codeFile.getAbsolutePath(), fileName);

            String result = executeDockerCommand(command);

            codeFile.delete();
            return result;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error running code: " + e.getMessage(), e);
        }
    }

    private File createTemporaryFile(String fileName, String extension, String code) throws IOException {
        File codeFile = File.createTempFile(fileName, extension);
        try (FileWriter writer = new FileWriter(codeFile)) {
            writer.write(code);
        }
        return codeFile;
    }

    private String executeDockerCommand(String command) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = createProcessBuilder(command);
        Process process = processBuilder.start();

        boolean completed = process.waitFor(30, TimeUnit.SECONDS);
        if (!completed) {
            process.destroyForcibly();
            return "Execution timed out";
        }

        String output = readStream(process.getInputStream());
        String errorOutput = readStream(process.getErrorStream());

        return (output + "\n" + errorOutput).trim();
    }

    private String readStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }
        return output.toString();
    }

    private ProcessBuilder createProcessBuilder(String command) {
        String os = osService.getLowerCaseOsName();
        if (os.contains("win")) {
            return new ProcessBuilder("cmd.exe", "/c", command);
        }
        return new ProcessBuilder("/bin/sh", "-c", command);
    }
}
