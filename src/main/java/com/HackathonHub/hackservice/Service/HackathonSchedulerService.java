package com.HackathonHub.hackservice.Service;

import com.HackathonHub.hackservice.Enitities.HackInfo;
import com.HackathonHub.hackservice.Enitities.HackInfoDto;
import com.HackathonHub.hackservice.Repository.HackRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class HackathonSchedulerService {

    @Autowired
    private HackRepository hackRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${hackathon.api.url}")
    private String hackathonApiUrl;

    /**
     * Fetch hackathons from external API every 24 hours
     * Initial delay of 0ms means it runs immediately on server start
     * Fixed rate of 24 hours (86400000ms)
     */
    @Scheduled(initialDelay = 0, fixedRate = 86400000)
    public void fetchAndSaveHackathons() {
        log.info("Starting scheduled hackathon fetch from external API");

        try {
            // Fetch hackathons from external API
            HackInfoDto[] hackathons = restTemplate.getForObject(hackathonApiUrl, HackInfoDto[].class);

            if (hackathons == null || hackathons.length == 0) {
                log.warn("No hackathons received from external API");
                return;
            }

            int newHackathonsCount = 0;
            int existingHackathonsCount = 0;

            for (HackInfoDto dto : hackathons) {
                // Check if hackathon already exists in database
                if (!hackRepository.existsByHackId(dto.getHackId())) {
                    HackInfo hackInfo = dto.transformToHackInfo();

                    // Initialize interested users list if null
                    if (hackInfo.getInterestedUsers() == null) {
                        hackInfo.setInterestedUsers(new ArrayList<>());
                    }

                    hackRepository.save(hackInfo);
                    newHackathonsCount++;
                    log.debug("Saved new hackathon: {} (ID: {})", hackInfo.getHackName(), hackInfo.getHackId());
                } else {
                    existingHackathonsCount++;
                }
            }

            log.info("Hackathon fetch completed. New: {}, Existing: {}, Total fetched: {}",
                    newHackathonsCount, existingHackathonsCount, hackathons.length);

        } catch (Exception e) {
            log.error("Error fetching hackathons from external API", e);
        }
    }

    /**
     * Manual trigger method for testing purposes
     */
    public void manualFetchHackathons() {
        log.info("Manual fetch triggered");
        fetchAndSaveHackathons();
    }
}