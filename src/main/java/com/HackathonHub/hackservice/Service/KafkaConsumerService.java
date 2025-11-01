package com.HackathonHub.hackservice.Service;

import com.HackathonHub.hackservice.Dto.MatchmakingResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class KafkaConsumerService {

    @Autowired
    private ObjectMapper objectMapper;

    // Store pending requests with CompletableFuture for async handling
    private final ConcurrentHashMap<String, CompletableFuture<MatchmakingResponseDto>> pendingRequests =
            new ConcurrentHashMap<>();

    @KafkaListener(topics = "${kafka.topic.matchmaking-response}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeMatchmakingResponse(String message) {
        try {
            MatchmakingResponseDto responseDto = objectMapper.readValue(message, MatchmakingResponseDto.class);
            log.info("Received matchmaking response for hackId: {} and userId: {}",
                    responseDto.getHackId(), responseDto.getRequestingUserId());

            // Create a unique key for the request
            String requestKey = generateRequestKey(responseDto.getHackId(), responseDto.getRequestingUserId());

            // Complete the future if it exists
            CompletableFuture<MatchmakingResponseDto> future = pendingRequests.remove(requestKey);
            if (future != null) {
                future.complete(responseDto);
            } else {
                log.warn("Received response for unknown request: {}", requestKey);
            }
        } catch (Exception e) {
            log.error("Error processing matchmaking response", e);
        }
    }

    public CompletableFuture<MatchmakingResponseDto> registerRequest(String hackId, String userId) {
        String requestKey = generateRequestKey(hackId, userId);
        CompletableFuture<MatchmakingResponseDto> future = new CompletableFuture<>();
        pendingRequests.put(requestKey, future);
        return future;
    }

    private String generateRequestKey(String hackId, String userId) {
        return hackId + ":" + userId;
    }
}