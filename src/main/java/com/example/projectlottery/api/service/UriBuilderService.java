package com.example.projectlottery.api.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Slf4j
@Service
public class UriBuilderService {

    private static final String KAKAO_LOCAL_SEARCH_ADDRESS_URL = "https://dapi.kakao.com/v2/local/search/address.json";
    private static final String KAKAO_GEO_COORDINATE_TO_REGION_URL = "https://dapi.kakao.com/v2/local/geo/coord2regioncode.json";

    public URI buildUriByAddress(String address) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(KAKAO_LOCAL_SEARCH_ADDRESS_URL);
        builder.queryParam("query", address);

        URI uri = builder.build().encode().toUri();
        log.info("[UriBuilderService buildUriByAddress] address: {}, uri: {}", address, uri);

        return uri;
    }

    public URI buildUriByCoordinate(double longitude, double latitude) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(KAKAO_GEO_COORDINATE_TO_REGION_URL);
        builder.queryParam("x", longitude);
        builder.queryParam("y", latitude);

        URI uri = builder.build().encode().toUri();
        log.info("[UriBuilderService buildUriByAddress] longitude: {}, latitude: {}, uri: {}", longitude, latitude, uri);

        return uri;
    }
}
