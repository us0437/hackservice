package com.HackathonHub.hackservice.Dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HackDeadlineDto {
    private String hackId;
    private LocalDateTime deadline;
}