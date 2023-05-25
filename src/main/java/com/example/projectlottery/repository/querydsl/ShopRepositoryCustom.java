package com.example.projectlottery.repository.querydsl;

import com.example.projectlottery.dto.response.querydsl.QLottoSummary;
import com.example.projectlottery.dto.response.querydsl.QShopRegion;
import com.example.projectlottery.dto.response.querydsl.QShopSummary;
import com.example.projectlottery.dto.response.querydsl.QShopWinSummary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ShopRepositoryCustom {

    /**
     * 로또판매점 검색 페이지 상단 - 행정구역 목록 + 행정구역에 속한 판매점 수 조회
     * @param state1 시.도
     * @param state2 시.군.구
     * @param state3 읍.면.동.리
     * @return 행정구역 목록 + 행정구역에 속한 판매점 수
     */
    List<QShopRegion> getShopRegionResponse(String state1, String state2, String state3);

    /**
     * 로또판매점 검색 페이지 - 행정구역에 속하고 keyword 가 상호에 포함된 판매점 목록 조회
     * @param state1 시.도
     * @param state2 시.군.구
     * @param state3 읍.면.동.리
     * @param keyword 상호 검색 keyword
     * @param pageable pageable
     * @return 행정구역에 속하고 keyword 가 상호에 포함된 판매점 목록
     */
    Page<QShopSummary> getShopSummaryResponseForShopList(String state1, String state2, String state3, String keyword, Pageable pageable);

    /**
     * 로또 명당 페이지 - 로또 명당 목록 조회
     * @return 로또 명당 목록
     */
    List<QShopSummary> getShopSummaryResponseForRanking();

    /**
     * 로또판매점 상세 페이지 - 상단 1, 2등 당첨 집계 요약 조회
     * @param shopId 판매점 id
     * @return 1, 2등 당첨 집계 요약
     */
    QShopWinSummary getShopWinSummaryResponseForShopDetail(Long shopId);

    /**
     * 로또판매점 상세 페이지 - 하단 1, 2등 당첨 회차 목록 조회
     * @param shopId 판매점 id
     * @param rank 등위
     * @return 1, 2등 당첨 회차 목록
     */
    List<QLottoSummary> getLottoSummaryResponseForShopDetail(Long shopId, Integer rank);
}
