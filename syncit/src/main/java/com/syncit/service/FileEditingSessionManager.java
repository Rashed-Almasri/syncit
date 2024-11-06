package com.syncit.service;

import com.syncit.model.FileEditingSession;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class FileEditingSessionManager {
    private final ConcurrentHashMap<Long, FileEditingSession> sessions = new ConcurrentHashMap<>();

    public FileEditingSession getOrCreateSession(Long fileId, String initialContent) {
        return sessions.computeIfAbsent(fileId, id -> new FileEditingSession(id, initialContent));
    }

    public void removeSession(Long fileId) {
        sessions.remove(fileId);
    }
}