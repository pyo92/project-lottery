package com.example.projectlottery.api.dto.response;

import com.example.projectlottery.api.dto.GeoCoordinateToRegionDocsDto;
import com.example.projectlottery.api.dto.GeoCoordinateToRegionMetaDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class KakaoGeoCoordinateToRegionResponse {

    @JsonProperty("meta")
    private GeoCoordinateToRegionMetaDto meta;

    @JsonProperty("documents")
    private List<GeoCoordinateToRegionDocsDto> docs;
}
