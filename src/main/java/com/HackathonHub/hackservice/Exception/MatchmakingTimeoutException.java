package com.HackathonHub.hackservice.Exception;

public class MatchmakingTimeoutException extends RuntimeException {
    public MatchmakingTimeoutException(String message) {
        super(message);
    }

    public MatchmakingTimeoutException(String userId, String hackId) {
        super("Matchmaking request timeout for user " + userId + " and hackathon " + hackId);
    }
}