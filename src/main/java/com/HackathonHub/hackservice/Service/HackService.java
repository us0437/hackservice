package com.HackathonHub.hackservice.Service;

import com.HackathonHub.hackservice.Dto.HackDeadlineDto;
import com.HackathonHub.hackservice.Dto.MatchmakingRequestDto;
import com.HackathonHub.hackservice.Dto.MatchmakingResponseDto;
import com.HackathonHub.hackservice.Enitities.HackInfo;
import com.HackathonHub.hackservice.Exception.HackathonNotFoundException;
import com.HackathonHub.hackservice.Exception.MatchmakingTimeoutException;
import com.HackathonHub.hackservice.Exception.UserAlreadyInterestedException;
import com.HackathonHub.hackservice.Repository.HackRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class HackService {

    @Autowired
    private HackRepository hackRepository;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Autowired
    private KafkaConsumerService kafkaConsumerService;

    /**
     * Get hackathon information by hackId
     */
    public HackInfo getHackathonInfo(String hackId) {
        log.info("Fetching hackathon info for hackId: {}", hackId);
        return hackRepository.findByHackId(hackId)
                .orElseThrow(() -> new HackathonNotFoundException(hackId, true));
    }

    /**
     * Get all hackathons
     */
    public List<HackInfo> getAllHackathons() {
        log.info("Fetching all hackathons");
        return hackRepository.findAll();
    }

    /**
     * Get interested users for a hackathon (for matchmaking service)
     */
    public List<String> getInterestedUsers(String hackId) {
        log.info("Fetching interested users for hackId: {}", hackId);
        return hackRepository.findInterestedUsersByHackId(hackId)
                .orElseThrow(() -> new HackathonNotFoundException(hackId, true));
    }

    /**
     * Get hackathon deadline (for userservice)
     */
    public HackDeadlineDto getHackathonDeadline(String hackId) {
        log.info("Fetching deadline for hackId: {}", hackId);
        LocalDateTime deadline = hackRepository.findDeadlineByHackId(hackId)
                .orElseThrow(() -> new HackathonNotFoundException(hackId, true));

        return HackDeadlineDto.builder()
                .hackId(hackId)
                .deadline(deadline)
                .build();
    }

    /**
     * Mark user as interested in a hackathon and get matched users via Kafka
     */
    @Transactional
    public MatchmakingResponseDto markUserInterested(String hackId, String userId) {
        log.info("Marking user {} as interested in hackathon {}", userId, hackId);

        // Fetch hackathon
        HackInfo hackInfo = hackRepository.findByHackId(hackId)
                .orElseThrow(() -> new HackathonNotFoundException(hackId, true));

        // Initialize interested users list if null
        if (hackInfo.getInterestedUsers() == null) {
            hackInfo.setInterestedUsers(new ArrayList<>());
        }

        // Check if user is already interested
        if (hackInfo.getInterestedUsers().contains(userId)) {
            throw new UserAlreadyInterestedException(userId, hackId);
        }

        // Add user to interested users list
        hackInfo.getInterestedUsers().add(userId);
        hackRepository.save(hackInfo);

        // Get all interested users except the requesting user
        List<String> otherInterestedUsers = new ArrayList<>(hackInfo.getInterestedUsers());
        otherInterestedUsers.remove(userId);

        // If no other interested users, return empty response
        if (otherInterestedUsers.isEmpty()) {
            log.info("No other interested users found for hackathon {}", hackId);
            return MatchmakingResponseDto.builder()
                    .requestingUserId(userId)
                    .hackId(hackId)
                    .rankedUsers(new ArrayList<>())
                    .build();
        }

        // Register request with consumer service to wait for response
        CompletableFuture<MatchmakingResponseDto> responseFuture =
                kafkaConsumerService.registerRequest(hackId, userId);

        // Send matchmaking request via Kafka
        MatchmakingRequestDto requestDto = MatchmakingRequestDto.builder()
                .requestingUserId(userId)
                .hackId(hackId)
                .interestedUsers(otherInterestedUsers)
                .build();

        kafkaProducerService.sendMatchmakingRequest(requestDto);

        // Wait for response with timeout (30 seconds)
        try {
            MatchmakingResponseDto response = responseFuture.get(30, TimeUnit.SECONDS);
            log.info("Received matchmaking response for user {} and hackathon {}", userId, hackId);
            return response;
        } catch (Exception e) {
            log.error("Error waiting for matchmaking response", e);
            throw new MatchmakingTimeoutException(userId, hackId);
        }
    }

    /**
     * Remove user from interested users (optional - for future use)
     */
    @Transactional
    public void removeUserInterest(String hackId, String userId) {
        log.info("Removing user {} interest from hackathon {}", userId, hackId);

        HackInfo hackInfo = hackRepository.findByHackId(hackId)
                .orElseThrow(() -> new HackathonNotFoundException(hackId, true));

        if (hackInfo.getInterestedUsers() != null) {
            hackInfo.getInterestedUsers().remove(userId);
            hackRepository.save(hackInfo);
        }
    }

    /**
     * Get hackathons by location (optional - for future use)
     */
    public List<HackInfo> getHackathonsByLocation(String city, String state, String country) {
        log.info("Fetching hackathons for location: {}, {}, {}", city, state, country);
        // You can add custom query in repository for this
        return hackRepository.findAll().stream()
                .filter(hack -> hack.getCity().equalsIgnoreCase(city)
                        || hack.getState().equalsIgnoreCase(state)
                        || hack.getCountry().equalsIgnoreCase(country))
                .toList();
    }

    /**
     * Get remote hackathons (optional - for future use)
     */
    public List<HackInfo> getRemoteHackathons() {
        log.info("Fetching remote hackathons");
        return hackRepository.findAll().stream()
                .filter(HackInfo::isRemote)
                .toList();
    }
}