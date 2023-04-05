package com.example.projectlottery.repository.querydsl;

import com.example.projectlottery.dto.response.querydsl.QLottoSummary;
import com.example.projectlottery.dto.response.querydsl.QShopRegion;
import com.example.projectlottery.dto.response.querydsl.QShopSummary;
import com.example.projectlottery.dto.response.querydsl.QShopWinSummary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ShopRepositoryCustom {

    List<QShopRegion> getShopRegionResponse(String state1, String state2, String state3);

    Page<QShopSummary> getShopSummaryResponseForShopList(String state1, String state2, String state3, Pageable pageable);

    List<QShopSummary> getShopSummaryResponseForRanking();

    QShopWinSummary getShopWinSummaryResponseForShopDetail(Long shopId);

    List<QLottoSummary> getLottoSummaryResponseForShopDetail(Long shopId, Integer rank);
}
