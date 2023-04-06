package com.example.projectlottery.service;

import com.example.projectlottery.dto.response.LottoResponse;
import com.example.projectlottery.dto.response.ShopResponse;
import com.example.projectlottery.dto.response.querydsl.QShopSummary;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

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

    public void deleteLatestDrawNo() {
        try {
            redisTemplate.delete(CACHE_LATEST_DRAW_NO_KEY);
            log.info("[RedisTemplateService deleteLatestDrawNo() success]");
        } catch (Exception e) {
            log.error("[RedisTemplateService deleteLatestDrawNo() failed]: {}", e.getMessage());
        }
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

    public void deleteAllWinDetail() {
        try {
            redisTemplate.delete(CACHE_WIN_DETAIL_KEY);
            log.info("[RedisTemplateService deleteAllWinDetail() success]");
        } catch (Exception e) {
            log.error("[RedisTemplateService deleteAllWinDetail() failed]: {}", e.getMessage());
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

    public void saveShopRanking(List<QShopSummary> shopRankingResponses) {
        for (QShopSummary dto : shopRankingResponses) {
            if (Objects.isNull(dto) || Objects.isNull(dto.id())) {
                log.error("Required values must not be null");
                return;
            }

            try {
                //dto serialized 값을 value 로 사용
                //score 는 정렬 로직(1등, 2등 배출 내림차순)에 따라 가중치 계산한 값을 사용 (1등.desc, 2등.desc, id.asc)
                double score = (dto.firstPrizeWinCount() * 100000000D) + (dto.secondPrizeWinCount() * 10000D) - (dto.id() / 100000000D);
                zSetOperations.add(CACHE_SHOP_RANKING_KEY, serializeResponseDto(dto), score);
                log.info("[RedisTemplateService saveShopRanking() success] shopId: {}, score: {}", dto.id(), score);
            } catch (Exception e) {
                log.error("[RedisTemplateService saveShopRanking() failed]: {}", e.getMessage());
            }
        }
    }

    public void deleteAllShopRanking() {
        try {
            redisTemplate.delete(CACHE_SHOP_RANKING_KEY);
            log.info("[RedisTemplateService deleteALlShopRanking() success]");
        } catch (Exception e) {
            log.error("[RedisTemplateService deleteALlShopRanking() failed]: {}", e.getMessage());
        }
    }

    public List<QShopSummary> getAllShopRanking() {
        try {
            return zSetOperations.reverseRange(CACHE_SHOP_RANKING_KEY, 0, 99).stream().map((o -> {
                try {
                    return deserializeResponseDto(o.toString(), QShopSummary.class);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            })).toList();
        } catch (Exception e) {
            log.info("[RedisTemplateService getAllShopRanking() failed]");
            return Collections.emptyList();
        }
    }

    public void flushAllCache() {
        this.deleteLatestDrawNo();
        this.deleteAllWinDetail();
        this.deleteAllShopDetail();
        this.deleteAllShopRanking();
    }

    private String serializeResponseDto(Object dto) throws JsonProcessingException {
        return objectMapper.writeValueAsString(dto);
    }

    private <T> T deserializeResponseDto(String serializedValue, Class<T> valueType) throws JsonProcessingException {
        return objectMapper.readValue(serializedValue, valueType);
    }
}
