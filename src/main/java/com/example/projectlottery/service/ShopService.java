package com.example.projectlottery.service;

import com.example.projectlottery.domain.type.ScrapStateType;
import com.example.projectlottery.dto.ShopDto;
import com.example.projectlottery.dto.response.shop.ShopResponse;
import com.example.projectlottery.repository.ShopRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class ShopService {

    private final ShopRepository shopRepository;

    private final RedisTemplateService redisTemplateService;

    @Transactional(readOnly = true)
    public Optional<ShopDto> getShopById(Long id) {
        return shopRepository.findById(id).map(ShopDto::from);
    }

    @Transactional(readOnly = true)
    public Set<ShopDto> getShopByL645YNAndScrapedDt(ScrapStateType scrapStateType, boolean l645YN, LocalDate scrapedDt) {
        return shopRepository.findByState1EqualsAndL645YNAndScrapedDtBefore(scrapStateType.getDescription(), l645YN, scrapedDt).stream()
                .map(ShopDto::from)
                .collect(Collectors.toUnmodifiableSet()); //순서가 중요하지 않으므로 set 반환
    }

    @Transactional(readOnly = true)
    public ShopResponse getShopResponse(Long id) {
        ShopResponse shopResponse = redisTemplateService.getShopDetail(id);

        if (Objects.isNull(shopResponse)) { //redis 조회 실패 시, redis 갱신
            shopResponse = shopRepository.findById(id)
                    .map(ShopResponse::from)
                    .orElseThrow(() -> new EntityNotFoundException("해당 판매점 없습니다. (id: " + id + ")"));

            redisTemplateService.saveShopDetail(shopResponse);
        }

        return shopResponse;
    }

    @Transactional(readOnly = true)
    public Page<ShopResponse> getShopListResponse(String state1, String state2, Pageable pageable) {
        return shopRepository.findByStates(state1, state2, pageable)
                .map(ShopResponse::from);
    }

    @Transactional(readOnly = true)
    public List<ShopResponse> getShopRankingResponse() {
        List<ShopResponse> shopResponses = redisTemplateService.getAllShopRanking(); //redis cache

        if (shopResponses.isEmpty()) { //redis 조회 실패 시, redis 갱신
            shopResponses = shopRepository.findByWins().stream().map(ShopResponse::from)
                    .sorted(Comparator.comparing(ShopResponse::count1stWin) //1등 배출횟수
                            .thenComparing(ShopResponse::count2ndWin) //2등 배출횟수
                            .reversed() //앞의 조건까지 내림차순 처리
                            .thenComparing(ShopResponse::id))
                    .toList();

            redisTemplateService.saveShopRanking(shopResponses); //redis cache
        }

        return shopResponses;
    }

    public void save(ShopDto dto) {
        shopRepository.save(dto.toEntity());
    }
}
