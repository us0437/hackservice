package com.HackathonHub.hackservice.Enitities;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeadlineInfo {
    private String hackId;
    private String hackName;
    private LocalDateTime deadline;
}