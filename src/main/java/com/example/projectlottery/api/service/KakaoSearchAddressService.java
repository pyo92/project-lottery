package com.example.projectlottery.api.service;

import com.example.projectlottery.api.dto.KakaoSearchAddressResponse;
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
public class KakaoSearchAddressService {

    private final RestTemplate restTemplate;

    private final UriBuilderService uriBuilderService;

    @Value("${kakao.rest.api.key}")
    private String kakaoRestApiKey;

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
}
