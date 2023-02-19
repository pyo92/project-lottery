package com.example.projectlottery.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GeoCoordinateToRegionDocsDto {

    @JsonProperty("region_type")
    private String regionType;
    @JsonProperty("address_name")
    private String addressName;
    @JsonProperty("region_1depth_name")
    private String regionDepthName1;
    @JsonProperty("region_2depth_name")
    private String regionDepthName2;
    @JsonProperty("region_3depth_name")
    private String regionDepthName3;
    @JsonProperty("code")
    private String code;
    @JsonProperty("x")
    private String longitude;
    @JsonProperty("y")
    private String latitude;
}
