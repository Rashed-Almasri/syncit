package com.syncit.controller;

import com.syncit.model.File;
import com.syncit.model.FileEditingSession;
import com.syncit.service.FileEditingSessionManager;
import com.syncit.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

@Slf4j
@Controller
@RequiredArgsConstructor
@CrossOrigin("*")
public class FileController {
    private final FileService fileService;
    private final FileEditingSessionManager sessionManager;

    @MessageMapping("/file/{fileId}/open")
    @SendTo("/topic/file/{fileId}")
    public String handleFileOpen(@DestinationVariable Long fileId) {
        log.info("File open requested for id: " + fileId);
        FileEditingSession session = sessionManager.getOrCreateSession(fileId, fileService.getFileContent(fileId));
        if (session.tryLock()) {
            try {
                return session.getContent();
            } finally {
                session.unlock();
            }
        } else {
            return "LOCKED";
        }
    }

    @MessageMapping("/file/{fileId}/edit")
    @SendTo("/topic/file/{fileId}")
    public String handleFileEdit(@DestinationVariable Long fileId, String content) {
        log.info("File edit requested for id: " + fileId);
        FileEditingSession session = sessionManager.getOrCreateSession(fileId, content);
        if (session.tryLock()) {
            try {
                session.updateContent(content);
                fileService.updateFileContent(fileId, content);
                return content;
            } finally {
                session.unlock();
            }
        } else {
            return "LOCKED";
        }
    }

    @MessageMapping("/file/{fileId}/close")
    public void handleFileClose(@DestinationVariable Long fileId) {
        log.info("File close requested for id: " + fileId);
        sessionManager.removeSession(fileId);
    }
}