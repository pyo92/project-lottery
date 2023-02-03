package com.example.projectlottery.api.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Slf4j
@Service
public class UriBuilderService {

    private static final String KAKAO_LOCAL_SEARCH_ADDRESS_URL = "https://dapi.kakao.com/v2/local/search/address.json";

    public URI buildUriByAddress(String address) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(KAKAO_LOCAL_SEARCH_ADDRESS_URL);
        builder.queryParam("query", address);

        URI uri = builder.build().encode().toUri();
        log.info("[UriBuilderService buildUriByAddress] address: {}, uri: {}", address, uri);

        return uri;
    }
}
