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

    /**
     * 판매점 정보 조회 (for 동행복권 scrap)
     * @param id 판매점 id
     * @return 판매점 정보 dto
     */
    @Transactional(readOnly = true)
    public Optional<ShopDto> getShopById(Long id) {
        return shopRepository.findById(id).map(ShopDto::from);
    }

    /**
     * 판매 중단 판매점 조회 (for 동행복권 scrap)
     * @param scrapStateType scrap 대상 행정구역
     * @param l645YN 로또 판매 여부
     * @param scrapedDt scrap 일자
     * @return 판매 중단 판매점
     */
    @Transactional(readOnly = true)
    public Set<ShopDto> getShopByL645YNAndScrapedDt(ScrapStateType scrapStateType, boolean l645YN, LocalDate scrapedDt) {
        return shopRepository.findByState1EqualsAndL645YNAndScrapedDtBefore(scrapStateType.getDescription(), l645YN, scrapedDt).stream()
                .map(ShopDto::from)
                .collect(Collectors.toUnmodifiableSet()); //순서가 중요하지 않으므로 set 반환
    }

    /**
     * 로또판매점 상세 정보 조회 (for view)
     * @param id 판매점 id
     * @return 로또판매점 상세 정보
     */
    @Transactional(readOnly = true)
    public ShopResponse getShopResponse(Long id) {
        ShopResponse shopResponse = redisTemplateService.getShopDetail(id);

        if (Objects.isNull(shopResponse)) { //redis 조회 실패 시, redis 갱신
            Optional<Shop> shopById = shopRepository.findById(id);

            if (shopById.isPresent()) {
                QShopWinSummary qShopWinSummaries = shopRepository.getShopWinSummaryResponseForShopDetail(id);

                List<QLottoSummary> qLottoSummary1stWin = shopRepository.getLottoSummaryResponseForShopDetail(id, 1);
                List<QLottoSummary> qLottoSummary2ndWin = shopRepository.getLottoSummaryResponseForShopDetail(id, 2);

                shopResponse = ShopResponse.from(shopById.get(), qShopWinSummaries, qLottoSummary1stWin, qLottoSummary2ndWin);

            } else {
                throw new EntityNotFoundException("해당 판매점 없습니다. (id: " + id + ")");
            }

            redisTemplateService.saveShopDetail(shopResponse);
        }

        return shopResponse;
    }

    /**
     * 행정구역 조회 (for view)
     * @param state1 시.도
     * @param state2 시.군.구
     * @param state3 읍.면.동.리
     * @return 행정구역
     */
    @Transactional(readOnly = true)
    public List<QShopRegion> getShopRegionResponse(String state1, String state2, String state3) {
        return shopRepository.getShopRegionResponse(state1, state2, state3);
    }

    /**
     * 로또판매점 목록 조회 (for view)
     * @param state1 시.도
     * @param state2 시.군.구
     * @param state3 읍.면.동.리
     * @param keyword 상호 검색 keyword
     * @param pageable pageable
     * @return 로또판매점 목록
     */
    @Transactional(readOnly = true)
    public Page<QShopSummary> getShopListResponse(String state1, String state2, String state3, String keyword, Pageable pageable) {
        return shopRepository.getShopSummaryResponseForShopList(state1, state2, state3, keyword, pageable);
    }

    /**
     * 로또명당 목록 조회 (for view)
     * @return 로또명당
     */
    @Transactional(readOnly = true)
    public List<QShopSummary> getShopRankingResponse() {
        List<QShopSummary> shopRankingResponses = redisTemplateService.getAllShopRanking(false); //redis cache

        if (shopRankingResponses.isEmpty()) { //redis 조회 실패 시, redis 갱신
            shopRankingResponses = shopRepository.getShopSummaryResponseForRanking();

            redisTemplateService.saveShopRanking(false, shopRankingResponses); //redis cache
        }

        return shopRankingResponses;
    }

    /**
     * 신흥명당 목록 조회 - 최근 52주 (for view)
     * @return 신흥명당 - 최근 52주
     */
    @Transactional(readOnly = true)
    public List<QShopSummary> getShopRecentRankingResponse() {
        List<QShopSummary> shopRankingResponses = redisTemplateService.getAllShopRanking(true); //redis cache

        if (shopRankingResponses.isEmpty()) { //redis 조회 실패 시, redis 갱신
            shopRankingResponses = shopRepository.getShopSummaryResponseForRecentRanking();

            redisTemplateService.saveShopRanking(true, shopRankingResponses); //redis cache
        }

        return shopRankingResponses;
    }

    /**
     * 로또판매점 정보 저장 (for 동행복권 로또판매점 scrap)
     * @param dto 로또판매점 dto
     */
    public void save(ShopDto dto) {
        shopRepository.save(dto.toEntity());
    }
}
