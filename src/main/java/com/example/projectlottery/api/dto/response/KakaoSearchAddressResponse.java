package com.example.projectlottery.api.dto.response;

import com.example.projectlottery.api.dto.SearchAddressDocsDto;
import com.example.projectlottery.api.dto.SearchAddressMetaDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class KakaoSearchAddressResponse {

    @JsonProperty("meta")
    private SearchAddressMetaDto meta;

    @JsonProperty("documents")
    private List<SearchAddressDocsDto> docs;
}
