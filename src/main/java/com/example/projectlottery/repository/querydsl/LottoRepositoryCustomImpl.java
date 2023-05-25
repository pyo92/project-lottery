package com.example.projectlottery.repository.querydsl;

import com.example.projectlottery.domain.Lotto;
import com.example.projectlottery.domain.QLottoWinShop;
import com.example.projectlottery.domain.type.LottoPurchaseType;
import com.example.projectlottery.dto.response.querydsl.QShopSummary;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.JPAExpressions;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

public class LottoRepositoryCustomImpl extends QuerydslRepositorySupport implements LottoRepositoryCustom {

    private QLottoWinShop lottoWinShop;
    private QLottoWinShop lottoWinShopForCounting;

    public LottoRepositoryCustomImpl() {
        super(Lotto.class);

        lottoWinShop = QLottoWinShop.lottoWinShop;
        lottoWinShopForCounting = new QLottoWinShop("new"); //같은 entity 를 join 하기 위해 추가 생성 (집계)
    }

    /**
     * 로또추첨결과 - 1, 2등 배출 목록 조회 (for querydsl)
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
}
