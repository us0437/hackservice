package com.HackathonHub.hackservice.Exception;

public class UserAlreadyInterestedException extends RuntimeException {
    public UserAlreadyInterestedException(String message) {
        super(message);
    }

    public UserAlreadyInterestedException(String userId, String hackId) {
        super("User " + userId + " is already interested in hackathon " + hackId);
    }
}