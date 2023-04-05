package com.example.projectlottery.repository.querydsl;

import com.example.projectlottery.dto.response.querydsl.QShopSummary;

import java.util.List;

public interface LottoRepositoryCustom {

    List<QShopSummary> getShopSummaryResponseForLotto(Long drawNo, Integer rank);
}
