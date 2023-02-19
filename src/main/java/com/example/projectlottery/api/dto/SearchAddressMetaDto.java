package com.example.projectlottery.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SearchAddressMetaDto {

    @JsonProperty("total_count")
    private Integer totalCount;
}
