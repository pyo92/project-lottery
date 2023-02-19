package com.example.projectlottery.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SearchAddressDocsDto {

    @JsonProperty("x")
    private Double longitude;
    @JsonProperty("y")
    private Double latitude;
}
