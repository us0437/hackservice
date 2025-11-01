package com.HackathonHub.hackservice.Repository;

import com.HackathonHub.hackservice.Enitities.HackInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface HackRepository extends JpaRepository<HackInfo, String> {

    Optional<HackInfo> findByHackId(String hackId);

    @Query("SELECT h.deadline FROM HackInfo h WHERE h.hackId = :hackId")
    Optional<LocalDateTime> findDeadlineByHackId(@Param("hackId") String hackId);

    @Query("SELECT h.interestedUsers FROM HackInfo h WHERE h.hackId = :hackId")
    Optional<List<String>> findInterestedUsersByHackId(@Param("hackId") String hackId);

    boolean existsByHackId(String hackId);
}