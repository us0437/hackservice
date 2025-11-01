package com.HackathonHub.hackservice.Enitities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class HackInfoDto {
    @Id
    @JsonProperty("hack_id")
    private String hackId;

    @JsonProperty("name")
    @NonNull
    private String hackName;

    @JsonProperty("website")
    @NonNull
    private String hackDetail;

//    @JsonProperty("created_at")
//    @NonNull
//    private LocalDate createdAt;

    @JsonProperty("start")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private LocalDateTime deadline;

    @JsonProperty("city")
    private String city;

    @JsonProperty("state")
    private String state;

    @JsonProperty("country")
    private String country;

    @JsonProperty("virtual")
    private boolean remote;

    @JsonProperty("interested_users")
    private List<String> interestedUsers;

    public HackInfo transformToHackInfo() {
        return HackInfo.builder()
                .hackId(UUID.randomUUID().toString())
                .hackName(hackName)
                .hackDetail(hackDetail)
                .deadline(deadline == null? LocalDateTime.now(): deadline)
                .city(city == null? "NA": city)
                .state(state == null? "NA": state)
                .country(country == null? "NA": country)
                .remote(remote)
                .interestedUsers(interestedUsers != null ? interestedUsers : List.of()).build();
    }
}
