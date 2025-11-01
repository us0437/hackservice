package com.HackathonHub.hackservice.Enitities;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InterestedUsersRequest {
    private String hackId;
    private String requestingUserId;
    private List<String> interestedUsers; // All interested users except the requesting user
}