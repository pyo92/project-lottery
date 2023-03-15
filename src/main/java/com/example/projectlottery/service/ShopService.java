package com.example.projectlottery.service;

import com.example.projectlottery.domain.Shop;
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

    @Transactional(readOnly = true)
    public Optional<ShopDto> getShopById(Long id) {
        return shopRepository.findById(id).map(ShopDto::from);
    }

    @Transactional(readOnly = true)
    public Set<ShopDto> getShopByL645YNAndScrapedDt(boolean l645YN, LocalDate scrapedDt) {
        return shopRepository.findByL645YNAndScrapedDtBefore(l645YN, scrapedDt).stream()
                .map(ShopDto::from)
                .collect(Collectors.toUnmodifiableSet()); //순서가 중요하지 않으므로 set 반환
    }

    @Transactional(readOnly = true)
    public ShopResponse getShopResponse(Long id) {
        return shopRepository.findById(id)
                .map(ShopResponse::from)
                .orElseThrow(() -> new EntityNotFoundException("해당 판매점 없습니다. (id: " + id + ")"));
    }

    @Transactional(readOnly = true)
    public Page<ShopResponse> getShopListResponse(String state1, String state2, Pageable pageable) {
        return shopRepository.findByStates(state1, state2, pageable)
                .map(ShopResponse::from);
    }

    @Transactional(readOnly = true)
    public TreeSet<ShopResponse> getShopRankingResponse() {
        List<Shop> byWins = shopRepository.findByWins();

        return byWins.stream().map(ShopResponse::from)
                .collect(Collectors.toCollection(() ->
                        new TreeSet<>(Comparator.comparing(ShopResponse::count1stWin) //1등 배출횟수
                                .thenComparing(ShopResponse::count1stWinAuto) //자동 1등 배출횟수
                                .thenComparing(ShopResponse::count2ndWin) //2등 배출횟수
                                .reversed() //앞의 조건까지 내림차순 처리
                                .thenComparing(ShopResponse::id))));
    }

    public void save(ShopDto dto) {
        shopRepository.save(dto.toEntity());
    }
}
