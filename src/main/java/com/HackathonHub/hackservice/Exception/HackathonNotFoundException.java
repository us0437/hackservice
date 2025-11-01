package com.HackathonHub.hackservice.Exception;

public class HackathonNotFoundException extends RuntimeException {
    public HackathonNotFoundException(String message) {
        super(message);
    }

    public HackathonNotFoundException(String hackId, boolean byId) {
        super("Hackathon not found with id: " + hackId);
    }
}