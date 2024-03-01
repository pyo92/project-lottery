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

import static com.example.projectlottery.util.StringUtils.isNullOrEmpty;

public class ShopRepositoryCustomImpl extends QuerydslRepositorySupport implements ShopRepositoryCustom {

    private final QShop shop;
    private final QRegion region;
    private final QLottoPrize lottoPrize;
    private final QLottoWinShop lottoWinShop;
    private final QLottoWinShop lottoWinShopAnother;

    public ShopRepositoryCustomImpl() {
        super(Shop.class);

        shop = QShop.shop;
        region = QRegion.region;
        lottoPrize = QLottoPrize.lottoPrize;
        lottoWinShop = QLottoWinShop.lottoWinShop;
        lottoWinShopAnother = new QLottoWinShop("new"); //같은 entity 를 join 하기 위해 추가 생성 (집계)
    }

    /**
     * 로또판매점 검색 페이지 상단 - 행정구역 목록 + 행정구역에 속한 판매점 수 조회
     * @param state1 시.도
     * @param state2 시.군.구
     * @param state3 읍.면.동.리
     * @return 행정구역 목록 + 행정구역에 속한 판매점 수
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
     * 로또판매점 검색 페이지 - 행정구역에 속하고 keyword 가 상호에 포함된 판매점 목록 조회
     * @param state1 시.도
     * @param state2 시.군.구
     * @param state3 읍.면.동.리
     * @param keyword 상호 검색 keyword
     * @param pageable pageable
     * @return 행정구역에 속하고 keyword 가 상호에 포함된 판매점 목록
     */
    @Override
    public Page<QShopSummary> getShopSummaryResponseForShopList(String state1, String state2, String state3, String keyword, Pageable pageable) {
        String keywordUpper = "";
        if (!isNullOrEmpty(keyword)) {
            keywordUpper = keyword.toUpperCase();
        }

        JPQLQuery<QShopSummary> query = from(shop)
                .where(shop.l645YN.eq(true), state1Eq(state1), state2Eq(state2), state3Eq(state3), shop.name.upper().contains(keywordUpper))
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
     * 로또 명당 페이지 - 로또 명당 목록 조회
     * @return 로또 명당 목록
     */
    @Override
    public List<QShopSummary> getShopSummaryResponseForRanking() {
        List<Long> shopIds = from(lottoWinShop)
                .where(lottoWinShop.shop.l645YN.eq(true))
                .groupBy(lottoWinShop.shop.id)
                .select(lottoWinShop.shop.id)
                .orderBy(new CaseBuilder().when(lottoWinShop.rank.eq(1)).then(1000000).otherwise(1).sum().desc())
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
        query.orderBy(new OrderSpecifier<>(Order.ASC, Expressions.constant(1L))); //id.ASC

        return query.fetch();
    }

    /**
     * 로또 명당 페이지 - 신흥 명당 목록 조회 (최근 52주)
     * @return 신흥 명당 목록 (최근 52주)
     */
    @Override
    public List<QShopSummary> getShopSummaryResponseForRecentRanking() {
        //현재 회차 조회
        Long latestDrawNo = from(lottoPrize)
                .where(lottoPrize.rank.eq(1))
                .select(lottoPrize.lotto.drawNo)
                .orderBy(lottoPrize.lotto.drawNo.desc())
                .limit(1)
                .fetch()
                .get(0);

        //최근 52회차 내 당첨 판매점 id 조회
        List<Long> shopIds = from(lottoWinShop)
                .where(lottoWinShop.shop.l645YN.eq(true), lottoWinShop.lotto.drawNo.gt(latestDrawNo - 52))
                .groupBy(lottoWinShop.shop.id)
                .select(lottoWinShop.shop.id)
                .orderBy(new CaseBuilder().when(lottoWinShop.rank.eq(1)).then(1000000).otherwise(1).sum().desc())
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
                                .where(lottoWinShopAnother.shop.id.eq(shop.id), lottoWinShopAnother.lotto.drawNo.gt(latestDrawNo - 52), lottoWinShopAnother.rank.eq(1)),
                        JPAExpressions.select(lottoWinShopAnother.count())
                                .from(lottoWinShopAnother)
                                .where(lottoWinShopAnother.shop.id.eq(shop.id), lottoWinShopAnother.lotto.drawNo.gt(latestDrawNo - 52), lottoWinShopAnother.rank.eq(2))));

        query.orderBy(new OrderSpecifier<>(Order.DESC, Expressions.constant(4L))); //1등.DESC
        query.orderBy(new OrderSpecifier<>(Order.DESC, Expressions.constant(5L))); //2등.DESC
        query.orderBy(new OrderSpecifier<>(Order.ASC, Expressions.constant(1L))); //id.ASC

        return query.fetch();
    }

    /**
     * 로또판매점 상세 페이지 - 상단 1, 2등 당첨 집계 요약 조회
     * @param shopId 판매점 id
     * @return 상단 1, 2등 당첨 집계 요약
     */
    @Override
    public QShopWinSummary getShopWinSummaryResponseForShopDetail(Long shopId) {
        //오류수정
        return from(shop)
                .leftJoin(lottoWinShop)
                    .on(shop.eq(lottoWinShop.shop), lottoWinShop.rank.in(1, 2))
                .leftJoin(lottoPrize)
                    .on(lottoWinShop.lotto.eq(lottoPrize.lotto), lottoWinShop.rank.eq(lottoPrize.rank))
                .where(shop.id.eq(shopId))
                .groupBy(shop.id)
                .select(Projections.constructor(QShopWinSummary.class,
                        new CaseBuilder().when(lottoWinShop.rank.eq(1)).then(1).otherwise(0).sum().coalesce(0),
                        new CaseBuilder().when(lottoWinShop.rank.eq(1).and(lottoWinShop.lottoPurchaseType.eq(LottoPurchaseType.AUTO))).then(1).otherwise(0).sum().coalesce(0),
                        new CaseBuilder().when(lottoWinShop.rank.eq(1).and(lottoWinShop.lottoPurchaseType.ne(LottoPurchaseType.AUTO))).then(1).otherwise(0).sum().coalesce(0),
                        new CaseBuilder().when(lottoWinShop.rank.eq(1)).then(lottoPrize.winAmountPerGame).otherwise(Expressions.nullExpression()).sum().coalesce(0L),
                        new CaseBuilder().when(lottoWinShop.rank.eq(1)).then(lottoPrize.winAmountPerGame).otherwise(Expressions.nullExpression()).max().coalesce(0L),
                        new CaseBuilder().when(lottoWinShop.rank.eq(1)).then(lottoPrize.winAmountPerGame).otherwise(Expressions.nullExpression()).min().coalesce(0L),
                        new CaseBuilder().when(lottoWinShop.rank.eq(2)).then(1).otherwise(0).sum().coalesce(0),
                        new CaseBuilder().when(lottoWinShop.rank.eq(2)).then(lottoPrize.winAmountPerGame).otherwise(Expressions.nullExpression()).sum().coalesce(0L),
                        new CaseBuilder().when(lottoWinShop.rank.eq(2)).then(lottoPrize.winAmountPerGame).otherwise(Expressions.nullExpression()).max().coalesce(0L),
                        new CaseBuilder().when(lottoWinShop.rank.eq(2)).then(lottoPrize.winAmountPerGame).otherwise(Expressions.nullExpression()).min().coalesce(0L)))
                .fetch().get(0);
    }

    /**
     * 로또판매점 상세 페이지 - 하단 1, 2등 당첨 회차 목록 조회
     * @param shopId 판매점 id
     * @param rank 등위
     * @return 1, 2등 당첨 회차 목록
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
                        JPAExpressions.select(lottoPrize.winGameCount)
                                .from(lottoPrize)
                                .where(lottoPrize.lotto.eq(lottoWinShop.lotto), lottoPrize.rank.eq(1)),
                        JPAExpressions.select(lottoPrize.winGameCount)
                                .from(lottoPrize)
                                .where(lottoPrize.lotto.eq(lottoWinShop.lotto), lottoPrize.rank.eq(2)),
                        new CaseBuilder().when(lottoWinShop.lottoPurchaseType.eq(LottoPurchaseType.AUTO)).then("자동")
                                .when(lottoWinShop.lottoPurchaseType.eq(LottoPurchaseType.MANUAL)).then("수동")
                                .when(lottoWinShop.lottoPurchaseType.eq(LottoPurchaseType.MIX)).then("반자동")
                                .otherwise("-")))
                .orderBy(lottoWinShop.lotto.drawNo.desc()).fetch();
    }

    /**
     * 시.도 조건 처리 (for 행정구역 복합 검색조건 dynamic query)
     * @param state1 시.도
     * @return 시.도 boolean
     */
    private BooleanExpression state1Eq(String state1) {
        return StringUtils.isNullOrEmpty(state1) ? null : shop.state1.eq(state1);
    }

    /**
     * 시.군.구 조건 처리 (for 행정구역 복합 검색조건 dynamic query)
     * @param state2 시.군.구
     * @return 시.군.구 boolean
     */
    private BooleanExpression state2Eq(String state2) {
        return StringUtils.isNullOrEmpty(state2) ? null : shop.state2.eq(state2);
    }

    /**
     * 읍.면.동.리 조건 처리 (for 행정구역 복합 검색조건 dynamic query)
     * @param state3 읍.면.동.리
     * @return 읍.면.동.리 boolean
     */
    private BooleanExpression state3Eq(String state3) {
        return StringUtils.isNullOrEmpty(state3) ? null : shop.state3.eq(state3);
    }

    /**
     * 로또판매점 검색 페이지 정렬 처리 (for dynamic query)
     * @param query 정렬 전 검색 query
     * @param pageable pageable
     */
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
                query.orderBy(new OrderSpecifier<>(Order.ASC, Expressions.constant(1L))); //id.ASC
            } else {
                query.orderBy(new OrderSpecifier<>(direction, Expressions.constant(3L)));
            }
        }
    }
}
