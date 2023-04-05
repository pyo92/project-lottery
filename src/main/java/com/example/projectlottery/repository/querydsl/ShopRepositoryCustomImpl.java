package com.example.projectlottery.repository.querydsl;

import com.example.projectlottery.domain.*;
import com.example.projectlottery.domain.type.LottoPurchaseType;
import com.example.projectlottery.dto.response.querydsl.QLottoSummary;
import com.example.projectlottery.dto.response.querydsl.QShopRegion;
import com.example.projectlottery.dto.response.querydsl.QShopSummary;
import com.example.projectlottery.dto.response.querydsl.QShopWinSummary;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.util.StringUtils;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

public class ShopRepositoryCustomImpl extends QuerydslRepositorySupport implements ShopRepositoryCustom {

    private QShop shop;
    private QRegion region;
    private QLottoPrize lottoPrize;
    private QLottoWinShop lottoWinShop;
    private QLottoWinShop lottoWinShopAnother;

    public ShopRepositoryCustomImpl() {
        super(Shop.class);

        shop = QShop.shop;
        region = QRegion.region;
        lottoPrize = QLottoPrize.lottoPrize;
        lottoWinShop = QLottoWinShop.lottoWinShop;
        lottoWinShopAnother = new QLottoWinShop("new"); //같은 entity 를 join 하기 위해 추가 생성 (집계)
    }

    /**
     *  복권판매점 페이지 - 검색 가능한 지역 목록, 지역에 포함된 판매점 수 리스팅
     */
    @Override
    public List<QShopRegion> getShopRegionResponse(String state1, String state2, String state3) {
        return from(shop)
                .innerJoin(region)
                .on(shop.state1.eq(region.state1), region.state2.isNull())
                .where(shop.l645YN.eq(true), state1Eq(state1), state2Eq(state2))
                .groupBy(Expressions.list(
                        Expressions.asString(shop.state1),
                        (StringUtils.isNullOrEmpty(state1) ? Expressions.nullExpression() : Expressions.asString(shop.state2)),
                        (StringUtils.isNullOrEmpty(state2) ? Expressions.nullExpression() : Expressions.asString(shop.state3))))
                .select(Projections.constructor(QShopRegion.class,
                        shop.state1,
                        (StringUtils.isNullOrEmpty(state1) ? Expressions.asString("") : shop.state2),
                        (StringUtils.isNullOrEmpty(state2) ? Expressions.asString("") : shop.state3),
                        shop.count()))
                .orderBy(region.reg.asc(), shop.state1.asc(), shop.state2.asc(), shop.state3.asc())
                .fetch();
    }

    /**
     *  복권판매점 페이지 - 선택한 지역에 속한 복권 판매점 목록 리스팅
     */
    @Override
    public Page<QShopSummary> getShopSummaryResponseForShopList(String state1, String state2, String state3, Pageable pageable) {
        JPQLQuery<QShopSummary> query = from(shop)
                .where(shop.l645YN.eq(true), state1Eq(state1), state2Eq(state2), state3Eq(state3))
                .select(Projections.constructor(QShopSummary.class,
                        shop.id,
                        shop.name,
                        shop.address,
                        Expressions.asString("-"),
                        JPAExpressions.select(lottoWinShopAnother.count())
                                .from(lottoWinShopAnother)
                                .where(lottoWinShopAnother.shop.id.eq(shop.id), lottoWinShopAnother.rank.eq(1)),
                        JPAExpressions.select(lottoWinShopAnother.count())
                                .from(lottoWinShopAnother)
                                .where(lottoWinShopAnother.shop.id.eq(shop.id), lottoWinShopAnother.rank.eq(2))))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        dynamicSortForShopList(query, pageable);

        return new PageImpl<>(query.fetch(), pageable, query.fetchCount());
    }

    /**
     *  로또명당 페이지 - 명당에 해당하는 100개의 판매점 요약 정보 리스팅
     */
    @Override
    public List<QShopSummary> getShopSummaryResponseForRanking() {
        List<Long> shopIds = from(lottoWinShop)
                .where(lottoWinShop.shop.l645YN.eq(true), lottoWinShop.rank.eq(1))
                .groupBy(lottoWinShop.shop.id)
                .select(lottoWinShop.shop.id)
                .orderBy(lottoWinShop.count().desc())
                .limit(100)
                .fetch();

        JPQLQuery<QShopSummary> query = from(shop)
                .where(shop.id.in(shopIds))
                .select(Projections.constructor(QShopSummary.class,
                        shop.id,
                        shop.name,
                        shop.address,
                        Expressions.asString("-"),
                        JPAExpressions.select(lottoWinShopAnother.count())
                                .from(lottoWinShopAnother)
                                .where(lottoWinShopAnother.shop.id.eq(shop.id), lottoWinShopAnother.rank.eq(1)),
                        JPAExpressions.select(lottoWinShopAnother.count())
                                .from(lottoWinShopAnother)
                                .where(lottoWinShopAnother.shop.id.eq(shop.id), lottoWinShopAnother.rank.eq(2))));

        query.orderBy(new OrderSpecifier<>(Order.DESC, Expressions.constant(4L))); //1등.DESC
        query.orderBy(new OrderSpecifier<>(Order.DESC, Expressions.constant(5L))); //2등.DESC
        query.orderBy(new OrderSpecifier<>(Order.ASC, Expressions.constant(2L))); //name.ASC

        return query.fetch();
    }

    /**
     *  복권판매점상세 페이지 - '당첨요약' 부분에 해당하는 1, 2등 당첨집계 정보 조회
     */
    @Override
    public QShopWinSummary getShopWinSummaryResponseForShopDetail(Long shopId) {
        QShopWinSummary fetch = from(lottoWinShop)
                .innerJoin(lottoPrize)
                .on(lottoWinShop.lotto.eq(lottoPrize.lotto), lottoWinShop.rank.eq(lottoPrize.rank))
                .where(lottoWinShop.shop.id.eq(shopId), lottoWinShop.rank.in(1, 2))
                .groupBy(lottoWinShop.shop.id)
                .select(Projections.constructor(QShopWinSummary.class,
                        new CaseBuilder().when(lottoWinShop.rank.eq(1)).then(1).otherwise(0).sum(),
                        new CaseBuilder().when(lottoWinShop.rank.eq(1).and(lottoWinShop.lottoPurchaseType.eq(LottoPurchaseType.AUTO))).then(1).otherwise(0).sum(),
                        new CaseBuilder().when(lottoWinShop.rank.eq(1).and(lottoWinShop.lottoPurchaseType.ne(LottoPurchaseType.AUTO))).then(1).otherwise(0).sum(),
                        new CaseBuilder().when(lottoWinShop.rank.eq(1)).then(lottoPrize.winAmountPerGame).otherwise(Expressions.nullExpression()).sum(),
                        new CaseBuilder().when(lottoWinShop.rank.eq(1)).then(lottoPrize.winAmountPerGame).otherwise(Expressions.nullExpression()).max(),
                        new CaseBuilder().when(lottoWinShop.rank.eq(1)).then(lottoPrize.winAmountPerGame).otherwise(Expressions.nullExpression()).min(),
                        new CaseBuilder().when(lottoWinShop.rank.eq(2)).then(1).otherwise(0).sum(),
                        new CaseBuilder().when(lottoWinShop.rank.eq(2)).then(lottoPrize.winAmountPerGame).otherwise(Expressions.nullExpression()).sum(),
                        new CaseBuilder().when(lottoWinShop.rank.eq(2)).then(lottoPrize.winAmountPerGame).otherwise(Expressions.nullExpression()).max(),
                        new CaseBuilder().when(lottoWinShop.rank.eq(2)).then(lottoPrize.winAmountPerGame).otherwise(Expressions.nullExpression()).min()))
                .fetch()
                .get(0);

        return fetch;
    }

    /**
     *  복권판매점상세 페이지 - 1, 2등 당첨내역에 해당하는 회차별 추첨결과 요약 정보 리스팅
     */
    @Override
    public List<QLottoSummary> getLottoSummaryResponseForShopDetail(Long shopId, Integer rank) {
        return from(lottoWinShop)
                .where(lottoWinShop.shop.id.eq(shopId), lottoWinShop.rank.eq(rank))
                .select(Projections.constructor(QLottoSummary.class,
                        lottoWinShop.lotto.drawNo,
                        lottoWinShop.no,
                        lottoWinShop.lotto.drawDt.stringValue(),
                        lottoWinShop.lotto.lottoWinNumber.number1,
                        lottoWinShop.lotto.lottoWinNumber.number2,
                        lottoWinShop.lotto.lottoWinNumber.number3,
                        lottoWinShop.lotto.lottoWinNumber.number4,
                        lottoWinShop.lotto.lottoWinNumber.number5,
                        lottoWinShop.lotto.lottoWinNumber.number6,
                        lottoWinShop.lotto.lottoWinNumber.numberB,
                        JPAExpressions.select(lottoPrize.winAmountPerGame)
                                .from(lottoPrize)
                                .where(lottoPrize.lotto.eq(lottoWinShop.lotto), lottoPrize.rank.eq(lottoWinShop.rank)),
                        new CaseBuilder().when(lottoWinShop.lottoPurchaseType.eq(LottoPurchaseType.AUTO)).then("자동")
                                .when(lottoWinShop.lottoPurchaseType.eq(LottoPurchaseType.MANUAL)).then("수동")
                                .when(lottoWinShop.lottoPurchaseType.eq(LottoPurchaseType.MIX)).then("반자동")
                                .otherwise("-")))
                .orderBy(lottoWinShop.lotto.drawNo.desc()).fetch();
    }

    private BooleanExpression state1Eq(String state1) {
        return StringUtils.isNullOrEmpty(state1) ? null : shop.state1.eq(state1);
    }

    private BooleanExpression state2Eq(String state2) {
        return StringUtils.isNullOrEmpty(state2) ? null : shop.state2.eq(state2);
    }

    private BooleanExpression state3Eq(String state3) {
        return StringUtils.isNullOrEmpty(state3) ? null : shop.state3.eq(state3);
    }

    private void dynamicSortForShopList(JPQLQuery<?> query, Pageable pageable) {
        if (pageable.getSort().isEmpty()) {
            query.orderBy(new OrderSpecifier<>(Order.ASC, Expressions.constant(3L))); //address,ASC
        } else {
            String[] order = pageable.getSort().toString().split(":");
            Order direction = order[1].toUpperCase().contains("DESC") ? Order.DESC : Order.ASC;

            if (order[0].equals("name")) {
                query.orderBy(new OrderSpecifier<>(direction, Expressions.constant(2L)));
            } else if (order[0].equals("win")) {
                query.orderBy(new OrderSpecifier<>(direction, Expressions.constant(4L))); //1등
                query.orderBy(new OrderSpecifier<>(direction, Expressions.constant(5L))); //2등
                query.orderBy(new OrderSpecifier<>(Order.ASC, Expressions.constant(3L))); //address.ASC
            } else {
                query.orderBy(new OrderSpecifier<>(direction, Expressions.constant(3L)));
            }
        }
    }
}
