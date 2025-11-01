package com.HackathonHub.hackservice.Enitities;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "hacks")
public class HackInfo {
    @Id
    @JsonProperty("hack_id")
    @NonNull
    private String hackId;

    @JsonProperty("hack_name")
    @NonNull
    private String hackName;

    @JsonProperty("hack_detail")
    @NonNull
    private String hackDetail;

//    @JsonProperty("created_at")
//    @NonNull
//    private LocalDate createdAt;

    @JsonProperty("deadline")
    private LocalDateTime deadline;

    @JsonProperty("city")
    @NonNull
    private String city;

    @JsonProperty("state")
    @NonNull
    private String state;

    @JsonProperty("country")
    @NonNull
    private String country;

    @JsonProperty("remote")
    @NonNull
    private boolean remote;

    @ElementCollection
    @JsonProperty("interested_users")
    @NonNull
    private List<String> interestedUsers;
}
