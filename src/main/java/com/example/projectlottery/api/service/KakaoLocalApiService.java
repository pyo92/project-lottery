package com.example.projectlottery.api.service;

import com.example.projectlottery.api.dto.response.KakaoGeoCoordinateToRegionResponse;
import com.example.projectlottery.api.dto.response.KakaoSearchAddressResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoLocalApiService {

    private final RestTemplate restTemplate;

    private final UriBuilderService uriBuilderService;

    @Value("${kakao.rest.api.key}")
    private String kakaoRestApiKey;

    /**
     * 주소 검색
     * @param address 검색 대상 주소
     * @return 위도, 경도 등 주소에 대한 정보
     */
    public KakaoSearchAddressResponse requestSearchAddress(String address) {
        if (ObjectUtils.isEmpty(address)) {
            return null;
        }

        URI uri = uriBuilderService.buildUriByAddress(address);

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "KakaoAK " + kakaoRestApiKey);

        HttpEntity httpEntity = new HttpEntity(headers);

        return restTemplate.exchange(uri, HttpMethod.GET, httpEntity, KakaoSearchAddressResponse.class).getBody();
    }

    /**
     * 위도, 경도 좌표 검색
     * @param longitude 위도
     * @param latitude 경도
     * @return 위도, 경도에 대한 행정구역 등 주소 정보
     */
    public KakaoGeoCoordinateToRegionResponse requestGeoCoordinateToRegion(double longitude, double latitude) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "KakaoAK " + kakaoRestApiKey);

        URI uri = uriBuilderService.buildUriByCoordinate(longitude, latitude);

        HttpEntity httpEntity = new HttpEntity(headers);

        return restTemplate.exchange(uri, HttpMethod.GET, httpEntity, KakaoGeoCoordinateToRegionResponse.class).getBody();
    }
}
