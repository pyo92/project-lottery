package com.example.projectlottery.api.dto;

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
    private MetaDto metaDto;

    @JsonProperty("documents")
    private List<DocumentsDto> documentList;
}
