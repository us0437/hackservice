package com.HackathonHub.hackservice.Controller;

import com.HackathonHub.hackservice.Dto.HackDeadlineDto;
import com.HackathonHub.hackservice.Dto.MatchmakingResponseDto;
import com.HackathonHub.hackservice.Enitities.HackInfo;
import com.HackathonHub.hackservice.Service.HackService;
import com.HackathonHub.hackservice.Service.HackathonSchedulerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/hack")
@Slf4j
public class HackController {

    @Autowired
    private HackService hackService;

    @Autowired
    private HackathonSchedulerService schedulerService;

    /**
     * Get hackathon information by hackId
     */
    @GetMapping("/{hackId}")
    public ResponseEntity<HackInfo> getHackathonInfo(@PathVariable String hackId) {
        try {
            HackInfo hackInfo = hackService.getHackathonInfo(hackId);
            return ResponseEntity.ok(hackInfo);
        } catch (RuntimeException e) {
            log.error("Error fetching hackathon info", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Get all hackathons
     */
    @GetMapping("/all")
    public ResponseEntity<List<HackInfo>> getAllHackathons() {
        try {
            List<HackInfo> hackathons = hackService.getAllHackathons();
            return ResponseEntity.ok(hackathons);
        } catch (Exception e) {
            log.error("Error fetching all hackathons", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get interested users for a hackathon (for matchmaking service)
     */
    @GetMapping("/{hackId}/interested-users")
    public ResponseEntity<List<String>> getInterestedUsers(@PathVariable String hackId) {
        try {
            List<String> users = hackService.getInterestedUsers(hackId);
            return ResponseEntity.ok(users);
        } catch (RuntimeException e) {
            log.error("Error fetching interested users", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Get hackathon deadline (for userservice)
     */
    @GetMapping("/{hackId}/deadline")
    public ResponseEntity<HackDeadlineDto> getHackathonDeadline(@PathVariable String hackId) {
        try {
            HackDeadlineDto deadline = hackService.getHackathonDeadline(hackId);
            return ResponseEntity.ok(deadline);
        } catch (RuntimeException e) {
            log.error("Error fetching deadline", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Mark user as interested in a hackathon and get matched users
     */
    @PostMapping("/{hackId}/interested")
    public ResponseEntity<MatchmakingResponseDto> markUserInterested(
            @PathVariable String hackId,
            @RequestParam String userId) {
        try {
            MatchmakingResponseDto response = hackService.markUserInterested(hackId, userId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Error marking user as interested", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(MatchmakingResponseDto.builder()
                            .requestingUserId(userId)
                            .hackId(hackId)
                            .build());
        }
    }

    /**
     * Remove user interest (optional endpoint)
     */
    @DeleteMapping("/{hackId}/interested")
    public ResponseEntity<Void> removeUserInterest(
            @PathVariable String hackId,
            @RequestParam String userId) {
        try {
            hackService.removeUserInterest(hackId, userId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            log.error("Error removing user interest", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Get remote hackathons
     */
    @GetMapping("/remote")
    public ResponseEntity<List<HackInfo>> getRemoteHackathons() {
        try {
            List<HackInfo> hackathons = hackService.getRemoteHackathons();
            return ResponseEntity.ok(hackathons);
        } catch (Exception e) {
            log.error("Error fetching remote hackathons", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get hackathons by location
     */
    @GetMapping("/location")
    public ResponseEntity<List<HackInfo>> getHackathonsByLocation(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String country) {
        try {
            List<HackInfo> hackathons = hackService.getHackathonsByLocation(
                    city != null ? city : "",
                    state != null ? state : "",
                    country != null ? country : ""
            );
            return ResponseEntity.ok(hackathons);
        } catch (Exception e) {
            log.error("Error fetching hackathons by location", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Manual trigger for fetching hackathons (for testing/admin purposes)
     */
    @PostMapping("/fetch")
    public ResponseEntity<String> manualFetchHackathons() {
        try {
            schedulerService.manualFetchHackathons();
            return ResponseEntity.ok("Hackathon fetch triggered successfully");
        } catch (Exception e) {
            log.error("Error triggering manual fetch", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error triggering hackathon fetch");
        }
    }
}