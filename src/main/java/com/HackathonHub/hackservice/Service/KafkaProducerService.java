package com.HackathonHub.hackservice.Service;

import com.HackathonHub.hackservice.Dto.MatchmakingRequestDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaProducerService {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${kafka.topic.matchmaking-request}")
    private String matchmakingRequestTopic;

    public void sendMatchmakingRequest(MatchmakingRequestDto requestDto) {
        try {
            String message = objectMapper.writeValueAsString(requestDto);
            kafkaTemplate.send(matchmakingRequestTopic, requestDto.getHackId(), message);
            log.info("Sent matchmaking request for hackId: {} and userId: {}",
                    requestDto.getHackId(), requestDto.getRequestingUserId());
        } catch (JsonProcessingException e) {
            log.error("Error serializing matchmaking request", e);
            throw new RuntimeException("Failed to send matchmaking request", e);
        }
    }
}