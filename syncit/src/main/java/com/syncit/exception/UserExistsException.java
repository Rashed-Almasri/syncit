package com.syncit.exception;

public class  UserExistsException extends Exception {
    public UserExistsException(String username) {
        super(String.format("User with name \"%s\" already exists", username));
    }
}