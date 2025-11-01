package com.HackathonHub.hackservice.Enitities;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MatchedUsersResponse {
    private String hackId;
    private String requestingUserId;
    private List<String> matchedUsers; // Ordered list of compatible users
    private String correlationId; // To track the request-response
}