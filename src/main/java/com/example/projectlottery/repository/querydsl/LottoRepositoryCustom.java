package com.example.projectlottery.repository.querydsl;

import com.example.projectlottery.dto.response.analysis.*;
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

    /**
     * 로또 회차별 당첨번호 AC 분석 (for querydsl)
     * @param startDrawNo 시작 회차
     * @param endDrawNo 종료 회차
     * @return 로또 회차별 당첨번호 AC 통계
     */
    List<NumberAcAnalysisResponse> getLottoNumberAcAnalysis(Long startDrawNo, Long endDrawNo);

    /**
     * 로또 회차별 당첨번호 총합 분석 (for querydsl)
     * @param startDrawNo 시작 회차
     * @param endDrawNo 종료 회차
     * @return 로또 회차별 당첨번호 AC 통계
     */
    List<NumberSumAnalysisResponse> getLottoNumberSumAnalysis(Long startDrawNo, Long endDrawNo);

    /**
     * 로또 번호 출현 횟수 분석 (for querydsl)
     * @param startDrawNo 시작 회차
     * @param endDrawNo 종료 회차
     * @param bonus 보너스 번호 포함 여부
     * @return 로또 번호 출현 횟수
     */
    List<NumberHitCountWithBonusAnalysisResponse> getLottoNumberHitCountAnalysis(Long startDrawNo, Long endDrawNo, Boolean bonus);

    /**
     * 로또 번호 대역별 색상 통계 분석 (for querydsl)
     *
     * @param startDrawNo 시작 회차
     * @param endDrawNo 종료 회차
     * @param bonus 보너스 번호 포함 여부
     * @return 로또 대역별 색상 통계
     */
    List<NumberRangeHitCountAnalysisResponse> getLottoNumberRangeHitCountAnalysis(Long startDrawNo, Long endDrawNo, Boolean bonus);

    /**
     * 로또 번호 미출현 기간 분석 (for querydsl)
     * @param startDrawNo 시작 회차
     * @param endDrawNo 종료 회차
     * @param bonus 보너스 번호 포함 여부
     * @return 로또 번호 미출현 기간
     */
    List<NumberMissCountAnalysisResponse> getLottoNumberMissCountAnalysis(Long startDrawNo, Long endDrawNo, Boolean bonus);
}
