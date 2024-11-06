package com.syncit.model;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FileEditingSession {
    private final Long fileId;
    private String content;
    private final Lock lock = new ReentrantLock();

    public FileEditingSession(Long fileId, String content) {
        this.fileId = fileId;
        this.content = content;
    }

    public Long getFileId() {
        return fileId;
    }

    public String getContent() {
        return content;
    }

    public void updateContent(String newContent) {
        this.content = newContent;
    }

    public boolean tryLock() {
        return lock.tryLock();
    }

    public void unlock() {
        lock.unlock();
    }
}