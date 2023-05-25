package com.example.projectlottery.repository.querydsl;

import com.example.projectlottery.dto.response.querydsl.QShopSummary;

import java.util.List;

public interface LottoRepositoryCustom {

    /**
     * 로또추첨결과 - 1, 2등 배출 목록 조회 (for querydsl)
     * @param drawNo 회차 번호
     * @param rank 등위
     * @return 1, 2등 배출 목록
     */
    List<QShopSummary> getShopSummaryResponseForLotto(Long drawNo, Integer rank);
}
