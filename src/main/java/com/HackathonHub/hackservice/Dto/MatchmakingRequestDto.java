package com.HackathonHub.hackservice.Dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MatchmakingRequestDto {
    private String requestingUserId;
    private String hackId;
    private List<String> interestedUsers;
}