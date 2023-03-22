package com.example.projectlottery.service;

import com.example.projectlottery.dto.response.lotto.LottoResponse;
import com.example.projectlottery.dto.response.shop.ShopResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisTemplateService {
    private static final String CACHE_LATEST_DRAW_NO_KEY = "L645_LATEST_DRAW_NO";
    private static final String CACHE_WIN_DETAIL_KEY = "L645_WIN_DETAIL";
    private static final String CACHE_SHOP_DETAIL_KEY = "L645_SHOP_DETAIL";
    private static final String CACHE_SHOP_RANKING_KEY = "L645_SHOP_RANKING";

    private final RedisTemplate<String, Object> redisTemplate;

    private final ObjectMapper objectMapper;

    private ValueOperations<String, Object> valueOperations;
    private HashOperations<String, String, String> hashOperations;
    private ZSetOperations<String, Object> zSetOperations;

    @PostConstruct
    public void init() {
        valueOperations = redisTemplate.opsForValue();
        hashOperations = redisTemplate.opsForHash();
        zSetOperations = redisTemplate.opsForZSet();
    }

    public void saveLatestDrawNo(Long latestDrawNo) {
        if (Objects.isNull(latestDrawNo)) {
            log.error("Required values must not be null");
            return;
        }

        valueOperations.set(CACHE_LATEST_DRAW_NO_KEY,  String.valueOf(latestDrawNo));
        log.info("[RedisTemplateService saveLatestDrawNo() success] drawNo: {}", latestDrawNo);
    }

    public Long getLatestDrawNo() {
        try {
            return Long.valueOf(String.valueOf(valueOperations.get(CACHE_LATEST_DRAW_NO_KEY)));
        } catch (Exception e) {
            log.error("[RedisTemplateService getLatestDrawNo() failed]: {}", e.getMessage());
            return null;
        }
    }

    public void saveWinDetail(LottoResponse dto) {
        if (Objects.isNull(dto) || Objects.isNull(dto.drawNo())) {
            log.error("Required values must not be null");
            return;
        }

        try {
            //drawNo 를 hashKey 로 사용
            hashOperations.put(CACHE_WIN_DETAIL_KEY, String.valueOf(dto.drawNo()), serializeResponseDto(dto));
            log.info("[RedisTemplateService saveWinDetail() success] drawNo: {}", dto.drawNo());
        } catch (Exception e) {
            log.error("[RedisTemplateService saveWinDetail() failed]: {}", e.getMessage());
        }
    }

    public LottoResponse getWinDetail(Long drawNo) {
        try {
            return deserializeResponseDto(hashOperations.get(CACHE_WIN_DETAIL_KEY, String.valueOf(drawNo)), LottoResponse.class);
        } catch (Exception e) {
            log.info("[RedisTemplateService getWinDetail() failed] drawNo: {}", drawNo);
            return null;
        }
    }

    public void saveShopDetail(ShopResponse dto) {
        if (Objects.isNull(dto) || Objects.isNull(dto.id())) {
            log.error("Required values must not be null");
            return;
        }

        try {
            //shopId 를 hashKey 로 사용
            hashOperations.put(CACHE_SHOP_DETAIL_KEY, String.valueOf(dto.id()), serializeResponseDto(dto));
            log.info("[RedisTemplateService saveShopDetail() success] shopId: {}", dto.id());
        } catch (Exception e) {
            log.error("[RedisTemplateService saveShopDetail() failed]: {}", e.getMessage());
        }
    }

    public void deleteAllShopDetail() {
        try {
            redisTemplate.delete(CACHE_SHOP_DETAIL_KEY);
            log.info("[RedisTemplateService deleteAllShopDetail() success]");
        } catch (Exception e) {
            log.error("[RedisTemplateService deleteAllShopDetail() failed]: {}", e.getMessage());
        }
    }

    public ShopResponse getShopDetail(Long shopId) {
        try {
            return deserializeResponseDto(hashOperations.get(CACHE_SHOP_DETAIL_KEY, String.valueOf(shopId)), ShopResponse.class);
        } catch (Exception e) {
            log.info("[RedisTemplateService saveShopRanking() failed] shopId: {}", shopId);
            return null;
        }
    }

    public void saveShopRanking(Set<ShopResponse> shopResponses) {
        int rank = 0;
        for (ShopResponse dto : shopResponses) {
            if (Objects.isNull(dto) || Objects.isNull(dto.id())) {
                log.error("Required values must not be null");
                return;
            }

            try {
                //dto serialized 값을 value, treeSet 정렬 순서를 score 로 사용
                zSetOperations.add(CACHE_SHOP_RANKING_KEY, serializeResponseDto(dto), rank++);
                log.info("[RedisTemplateService saveShopRanking() success] rank: {}, shopId: {}", rank, dto.id());
            } catch (Exception e) {
                log.error("[RedisTemplateService saveShopRanking() failed]: {}", e.getMessage());
            }
        }
    }

    public void deleteALlShopRanking() {
        try {
            redisTemplate.delete(CACHE_SHOP_RANKING_KEY);
            log.info("[RedisTemplateService deleteALlShopRanking() success]");
        } catch (Exception e) {
            log.error("[RedisTemplateService deleteALlShopRanking() failed]: {}", e.getMessage());
        }
    }

    public Set<ShopResponse> getAllShopRanking() {
        try {
            return zSetOperations.range(CACHE_SHOP_RANKING_KEY, 0, 99).stream().map(o -> {
                try {
                    return deserializeResponseDto(o.toString(), ShopResponse.class);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }).collect(Collectors.toCollection(() ->
                    new TreeSet<>(Comparator.comparing(ShopResponse::count1stWin)
                            .thenComparing(ShopResponse::count1stWinAuto)
                            .thenComparing(ShopResponse::count2ndWin).reversed()
                            .thenComparing(ShopResponse::id))));
        } catch (Exception e) {
            log.info("[RedisTemplateService getAllShopRanking() failed]");
            return Collections.emptySet();
        }
    }

    private String serializeResponseDto(Object dto) throws JsonProcessingException {
        return objectMapper.writeValueAsString(dto);
    }

    private <T> T deserializeResponseDto(String serializedValue, Class<T> valueType) throws JsonProcessingException {
        return objectMapper.readValue(serializedValue, valueType);
    }
}
