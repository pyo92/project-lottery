package com.example.projectlottery.service;

import com.example.projectlottery.domain.Shop;
import com.example.projectlottery.domain.type.ScrapStateType;
import com.example.projectlottery.dto.ShopDto;
import com.example.projectlottery.dto.response.ShopResponse;
import com.example.projectlottery.dto.response.querydsl.QLottoSummary;
import com.example.projectlottery.dto.response.querydsl.QShopRegion;
import com.example.projectlottery.dto.response.querydsl.QShopSummary;
import com.example.projectlottery.dto.response.querydsl.QShopWinSummary;
import com.example.projectlottery.repository.ShopRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
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
            Optional<Shop> shopById = shopRepository.findById(id);

            if (shopById.isPresent()) {
                QShopWinSummary QShopWinSummary = shopRepository.getShopWinSummaryResponseForShopDetail(id);

                List<QLottoSummary> qLottoSummary = shopRepository.getLottoSummaryResponseForShopDetail(id, 1);
                List<QLottoSummary> qLottoSummary1 = shopRepository.getLottoSummaryResponseForShopDetail(id, 2);

                shopResponse = ShopResponse.from(shopById.get(), QShopWinSummary, qLottoSummary, qLottoSummary1);

            } else {
                throw new EntityNotFoundException("해당 판매점 없습니다. (id: " + id + ")");
            }

            redisTemplateService.saveShopDetail(shopResponse);
        }

        return shopResponse;
    }

    @Transactional(readOnly = true)
    public List<QShopRegion> getShopRegionResponse(String state1, String state2, String state3) {
        return shopRepository.getShopRegionResponse(state1, state2, state3);
    }

    @Transactional(readOnly = true)
    public Page<QShopSummary> getShopListResponse(String state1, String state2, String state3, Pageable pageable) {
        return shopRepository.getShopSummaryResponseForShopList(state1, state2, state3, pageable);
    }

    @Transactional(readOnly = true)
    public List<QShopSummary> getShopRankingResponse() {
        List<QShopSummary> shopRankingResponses = redisTemplateService.getAllShopRanking(); //redis cache

        if (shopRankingResponses.isEmpty()) { //redis 조회 실패 시, redis 갱신
            shopRankingResponses = shopRepository.getShopSummaryResponseForRanking();

            redisTemplateService.saveShopRanking(shopRankingResponses); //redis cache
        }

        return shopRankingResponses;
    }

    public void save(ShopDto dto) {
        shopRepository.save(dto.toEntity());
    }
}
