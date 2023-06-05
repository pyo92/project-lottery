package com.example.projectlottery.repository.querydsl;

import com.example.projectlottery.domain.Lotto;
import com.example.projectlottery.domain.QLotto;
import com.example.projectlottery.domain.QLottoWinShop;
import com.example.projectlottery.domain.type.LottoPurchaseType;
import com.example.projectlottery.dto.response.analysis.*;
import com.example.projectlottery.dto.response.querydsl.QShopSummary;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.JPAExpressions;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class LottoRepositoryCustomImpl extends QuerydslRepositorySupport implements LottoRepositoryCustom {

    private final QLotto lotto;
    private final QLottoWinShop lottoWinShop;
    private final QLottoWinShop lottoWinShopForCounting;

    public LottoRepositoryCustomImpl() {
        super(Lotto.class);

        lotto = QLotto.lotto;
        lottoWinShop = QLottoWinShop.lottoWinShop;
        lottoWinShopForCounting = new QLottoWinShop("new"); //같은 entity 를 join 하기 위해 추가 생성 (집계)
    }

    /**
     * 로또추첨결과 - 1, 2등 배출 목록 조회 (for querydsl)
     *
     * @param drawNo 회차 번호
     * @param rank 등위
     * @return 1, 2등 배출 목록
     */
    @Override
    public List<QShopSummary> getShopSummaryResponseForLotto(Long drawNo, Integer rank) {
        return from(lottoWinShop)
                .where(lottoWinShop.lotto.drawNo.eq(drawNo), lottoWinShop.rank.eq(rank))
                .select(Projections.constructor(QShopSummary.class,
                        lottoWinShop.shop.id,
                        lottoWinShop.shop.name,
                        lottoWinShop.shop.address,
                        new CaseBuilder().when(lottoWinShop.lottoPurchaseType.eq(LottoPurchaseType.AUTO)).then("자동")
                                .when(lottoWinShop.lottoPurchaseType.eq(LottoPurchaseType.MANUAL)).then("수동")
                                .when(lottoWinShop.lottoPurchaseType.eq(LottoPurchaseType.MIX)).then("반자동")
                                .otherwise("-"),
                        JPAExpressions.select(lottoWinShopForCounting.count())
                                .from(lottoWinShopForCounting)
                                .where(lottoWinShopForCounting.shop.id.eq(lottoWinShop.shop.id), lottoWinShopForCounting.rank.eq(1)),
                        JPAExpressions.select(lottoWinShopForCounting.count())
                                .from(lottoWinShopForCounting)
                                .where(lottoWinShopForCounting.shop.id.eq(lottoWinShop.shop.id), lottoWinShopForCounting.rank.eq(2))))
                .orderBy(lottoWinShop.no.asc())
                .fetch();
    }

    /**
     * 로또 회차별 당첨번호 AC 분석 (for querydsl)
     * @param startDrawNo 시작 회차
     * @param endDrawNo 종료 회차
     * @return 로또 회차별 당첨번호 AC 통계
     */
    @Override
    public List<NumberAcAnalysisResponse> getLottoNumberAcAnalysis(Long startDrawNo, Long endDrawNo) {
        List<Tuple> subQuery = from(lotto)
                .where(lotto.drawNo.between(startDrawNo, endDrawNo))
                .select(lotto.drawNo,
                        lotto.lottoWinNumber.number1,
                        lotto.lottoWinNumber.number2,
                        lotto.lottoWinNumber.number3,
                        lotto.lottoWinNumber.number4,
                        lotto.lottoWinNumber.number5,
                        lotto.lottoWinNumber.number6)
                .orderBy(lotto.drawNo.desc())
                .fetch();

        List<NumberAcAnalysisResponse> result = new ArrayList<>();

        subQuery.forEach(r -> {
            Set<Integer> diff = new HashSet<>();

            for (int i = 1; i < 7; i++) {
                for (int j = i + 1; j < 7; j++) {
                    diff.add(Math.abs(r.get(i, Integer.class) - r.get(j, Integer.class)));
                }
            }

            result.add(NumberAcAnalysisResponse.of(
                    r.get(0, Long.class),
                    r.get(1, Integer.class),
                    r.get(2, Integer.class),
                    r.get(3, Integer.class),
                    r.get(4, Integer.class),
                    r.get(5, Integer.class),
                    r.get(6, Integer.class),
                    diff.size() - 5));
        });

        return result;
    }

    /**
     * 로또 회차별 당첨번호 총합 분석 (for querydsl)
     * @param startDrawNo 시작 회차
     * @param endDrawNo 종료 회차
     * @return 로또 회차별 당첨번호 총합 통계
     */
    @Override
    public List<NumberSumAnalysisResponse> getLottoNumberSumAnalysis(Long startDrawNo, Long endDrawNo) {
        List<Tuple> subQuery = from(lotto)
                .where(lotto.drawNo.between(startDrawNo, endDrawNo))
                .select(lotto.drawNo,
                        lotto.lottoWinNumber.number1,
                        lotto.lottoWinNumber.number2,
                        lotto.lottoWinNumber.number3,
                        lotto.lottoWinNumber.number4,
                        lotto.lottoWinNumber.number5,
                        lotto.lottoWinNumber.number6)
                .orderBy(lotto.drawNo.desc())
                .fetch();

        List<NumberSumAnalysisResponse> result = new ArrayList<>();

        subQuery.forEach(r -> {
            result.add(NumberSumAnalysisResponse.of(
                    r.get(0, Long.class),
                    r.get(1, Integer.class),
                    r.get(2, Integer.class),
                    r.get(3, Integer.class),
                    r.get(4, Integer.class),
                    r.get(5, Integer.class),
                    r.get(6, Integer.class),
                    (r.get(1, Integer.class) +
                            r.get(2, Integer.class) +
                            r.get(3, Integer.class) +
                            r.get(4, Integer.class) +
                            r.get(5, Integer.class) +
                            r.get(6, Integer.class))));
        });

        return result;
    }

    /**
     * 로또 번호 출현 횟수 분석 (for querydsl)
     *
     * @param startDrawNo 시작 회차
     * @param endDrawNo 종료 회차
     * @param bonus 보너스 번호 포함 여부
     * @return 로또 번호 출현 횟수
     */
    @Override
    public List<NumberHitCountWithBonusAnalysisResponse> getLottoNumberHitCountAnalysis(Long startDrawNo, Long endDrawNo, Boolean bonus) {
        Map<Integer, Long> numberCount = new HashMap<>();
        Map<Integer, Long> bonusCount = new HashMap<>();

        //추첨 결과 번호 1~6 까지 모든 번호에 대해 출현 통계를 만든다.
        from(lotto)
                .where(lotto.drawNo.between(startDrawNo, endDrawNo))
                .select(Projections.constructor(NumberHitCountAnalysisResponse.class,
                        lotto.lottoWinNumber.number1,
                        lotto.lottoWinNumber.number1.count()))
                .groupBy(lotto.lottoWinNumber.number1)
                .fetch()
                .forEach(r -> {
                    if (numberCount.containsKey(r.number())) {
                        numberCount.put(r.number(), numberCount.get(r.number()) + r.count());
                    } else {
                        numberCount.put(r.number(), r.count());
                    }
                });
        from(lotto)
                .where(lotto.drawNo.between(startDrawNo, endDrawNo))
                .select(Projections.constructor(NumberHitCountAnalysisResponse.class,
                        lotto.lottoWinNumber.number2,
                        lotto.lottoWinNumber.number2.count()))
                .groupBy(lotto.lottoWinNumber.number2)
                .fetch()
                .forEach(r -> {
                    if (numberCount.containsKey(r.number())) {
                        numberCount.put(r.number(), numberCount.get(r.number()) + r.count());
                    } else {
                        numberCount.put(r.number(), r.count());
                    }
                });
        from(lotto)
                .where(lotto.drawNo.between(startDrawNo, endDrawNo))
                .select(Projections.constructor(NumberHitCountAnalysisResponse.class,
                        lotto.lottoWinNumber.number3,
                        lotto.lottoWinNumber.number3.count()))
                .groupBy(lotto.lottoWinNumber.number3)
                .fetch()
                .forEach(r -> {
                    if (numberCount.containsKey(r.number())) {
                        numberCount.put(r.number(), numberCount.get(r.number()) + r.count());
                    } else {
                        numberCount.put(r.number(), r.count());
                    }
                });
        from(lotto)
                .where(lotto.drawNo.between(startDrawNo, endDrawNo))
                .select(Projections.constructor(NumberHitCountAnalysisResponse.class,
                        lotto.lottoWinNumber.number4,
                        lotto.lottoWinNumber.number4.count()))
                .groupBy(lotto.lottoWinNumber.number4)
                .fetch()
                .forEach(r -> {
                    if (numberCount.containsKey(r.number())) {
                        numberCount.put(r.number(), numberCount.get(r.number()) + r.count());
                    } else {
                        numberCount.put(r.number(), r.count());
                    }
                });
        from(lotto)
                .where(lotto.drawNo.between(startDrawNo, endDrawNo))
                .select(Projections.constructor(NumberHitCountAnalysisResponse.class,
                        lotto.lottoWinNumber.number5,
                        lotto.lottoWinNumber.number5.count()))
                .groupBy(lotto.lottoWinNumber.number5)
                .fetch()
                .forEach(r -> {
                    if (numberCount.containsKey(r.number())) {
                        numberCount.put(r.number(), numberCount.get(r.number()) + r.count());
                    } else {
                        numberCount.put(r.number(), r.count());
                    }
                });
        from(lotto)
                .where(lotto.drawNo.between(startDrawNo, endDrawNo))
                .select(Projections.constructor(NumberHitCountAnalysisResponse.class,
                        lotto.lottoWinNumber.number6,
                        lotto.lottoWinNumber.number6.count()))
                .groupBy(lotto.lottoWinNumber.number6)
                .fetch()
                .forEach(r -> {
                    if (numberCount.containsKey(r.number())) {
                        numberCount.put(r.number(), numberCount.get(r.number()) + r.count());
                    } else {
                        numberCount.put(r.number(), r.count());
                    }
                });

        //bonus 번호 포함 여부에 따라 출현 통계를 추가한다.
        if (bonus) {
            from(lotto)
                .where(lotto.drawNo.between(startDrawNo, endDrawNo))
                    .select(Projections.constructor(NumberHitCountAnalysisResponse.class,
                            lotto.lottoWinNumber.numberB,
                            lotto.lottoWinNumber.numberB.count()))
                    .groupBy(lotto.lottoWinNumber.numberB)
                    .fetch()
                    .forEach(r -> {
                        if (bonusCount.containsKey(r.number())) {
                            bonusCount.put(r.number(), bonusCount.get(r.number()) + r.count());
                        } else {
                            bonusCount.put(r.number(), r.count());
                        }
                    });
        }

        List<NumberHitCountWithBonusAnalysisResponse> result = new ArrayList<>();
        for (int i = 1; i <= 45; i++) {
            Long nc = numberCount.containsKey(i) ? numberCount.get(i) : 0;
            Long bc = bonusCount.containsKey(i) ? bonusCount.get(i) : 0;
            result.add(NumberHitCountWithBonusAnalysisResponse.of(i, nc, bc));
        }

        result.sort(Comparator.comparing(NumberHitCountWithBonusAnalysisResponse::number)); //default sort

        return result;
    }

    /**
     * 로또 번호 대역별 색상 통계 분석 (for querydsl)
     *
     * @param startDrawNo 시작 회차
     * @param endDrawNo 종료 회차
     * @param bonus 보너스 번호 포함 여부
     * @return 로또 대역별 색상 통계
     */
    @Override
    public List<NumberRangeHitCountAnalysisResponse> getLottoNumberRangeHitCountAnalysis(Long startDrawNo, Long endDrawNo, Boolean bonus) {
        List<NumberHitCountWithBonusAnalysisResponse> lottoNumberHitCountAnalysis = getLottoNumberHitCountAnalysis(startDrawNo, endDrawNo, bonus);

        long cnt0 = 0;
        long cnt1 = 0;
        long cnt2 = 0;
        long cnt3 = 0;
        long cnt4 = 0;
        
        long bonusCnt0 = 0;
        long bonusCnt1 = 0;
        long bonusCnt2 = 0;
        long bonusCnt3 = 0;
        long bonusCnt4 = 0;

        for (NumberHitCountWithBonusAnalysisResponse r : lottoNumberHitCountAnalysis) {
            int number = r.number();
            long count = r.count();
            long bonusCount = r.bonus();

            if (number <= 10) {
                cnt0 += count;
                bonusCnt0 += bonusCount;
            } else if (number <= 20) {
                cnt1 += count;
                bonusCnt1 += bonusCount;
            } else if (number <= 30) {
                cnt2 += count;
                bonusCnt2 += bonusCount;
            } else if (number <= 40) {
                cnt3 += count;
                bonusCnt3 += bonusCount;
            } else {
                cnt4 += count;
                bonusCnt4 += bonusCount;
            }
        }

        List<NumberRangeHitCountAnalysisResponse> result = new ArrayList<>();
        result.add(NumberRangeHitCountAnalysisResponse.of(10, cnt0, bonusCnt0));
        result.add(NumberRangeHitCountAnalysisResponse.of(20, cnt1, bonusCnt1));
        result.add(NumberRangeHitCountAnalysisResponse.of(30, cnt2, bonusCnt2));
        result.add(NumberRangeHitCountAnalysisResponse.of(40, cnt3, bonusCnt3));
        result.add(NumberRangeHitCountAnalysisResponse.of(45, cnt4, bonusCnt4));

        return result;
    }

    /**
     * 로또 번호 미출현 기간 분석 (for querydsl)
     * @param startDrawNo 시작 회차
     * @param endDrawNo 종료 회차
     * @param bonus 보너스 번호 포함 여부
     * @return 로또 번호 미출현 기간
     */
    @Override
    public List<NumberMissCountAnalysisResponse> getLottoNumberMissCountAnalysis(Long startDrawNo, Long endDrawNo, Boolean bonus) {
        List<NumberMissCountAnalysisResponse> result = new ArrayList<>();

        for (int i = 1; i <= 45; i++) {
            AtomicLong count = new AtomicLong();
            from(lotto)
                    .where(lotto.drawNo.between(startDrawNo, endDrawNo),
                            new BooleanBuilder()
                                    .or(lotto.lottoWinNumber.number1.eq(i))
                                    .or(lotto.lottoWinNumber.number2.eq(i))
                                    .or(lotto.lottoWinNumber.number3.eq(i))
                                    .or(lotto.lottoWinNumber.number4.eq(i))
                                    .or(lotto.lottoWinNumber.number5.eq(i))
                                    .or(lotto.lottoWinNumber.number6.eq(i)))
                    .select(lotto.drawNo.max())
                    .fetch()
                    .forEach(r -> {
                        count.set(endDrawNo - (r == null ? startDrawNo - 1 : r));
                    });

            AtomicLong bonusCount = new AtomicLong();
            from(lotto)
                    .where(lotto.drawNo.between(startDrawNo, endDrawNo),
                            new BooleanBuilder()
                                    .or(lotto.lottoWinNumber.number1.eq(i))
                                    .or(lotto.lottoWinNumber.number2.eq(i))
                                    .or(lotto.lottoWinNumber.number3.eq(i))
                                    .or(lotto.lottoWinNumber.number4.eq(i))
                                    .or(lotto.lottoWinNumber.number5.eq(i))
                                    .or(lotto.lottoWinNumber.number6.eq(i))
                                    .or(lotto.lottoWinNumber.numberB.eq(i)))
                    .select(lotto.drawNo.max())
                    .fetch()
                    .forEach(r -> {
                        bonusCount.set(endDrawNo - (r == null ? startDrawNo - 1 : r));
                    });

            result.add(NumberMissCountAnalysisResponse.of(i, count.get(), bonusCount.get()));
        }

        return result;
    }
}
