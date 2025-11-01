package com.HackathonHub.hackservice.Dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MatchmakingResponseDto {
    private String requestingUserId;
    private String hackId;
    private List<String> rankedUsers; // Users ranked by compatibility
}