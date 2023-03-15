package com.example.projectlottery.repository.querydsl;

import com.example.projectlottery.domain.QLottoWinShop;
import com.example.projectlottery.domain.QShop;
import com.example.projectlottery.domain.Shop;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.util.StringUtils;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

public class ShopRepositoryCustomImpl extends QuerydslRepositorySupport implements ShopRepositoryCustom {

    private QShop shop;
    private QLottoWinShop lottoWinShop;

    public ShopRepositoryCustomImpl() {
        super(Shop.class);

        this.shop = QShop.shop;
        this.lottoWinShop = QLottoWinShop.lottoWinShop;
    }

    @Override
    public Page<Shop> findByStates(String state1, String state2, Pageable pageable) {
        JPQLQuery<Shop> query = from(shop)
                .where(state1Eq(state1), state2Eq(state2), shop.l645YN.eq(true));

        List<Shop> shops = getQuerydsl().applyPagination(pageable, query).fetch();

        return new PageImpl<>(shops, pageable, query.fetchCount());
    }

    private BooleanExpression state1Eq(String state1) {
        return StringUtils.isNullOrEmpty(state1) ? null : shop.state1.eq(state1);
    }

    private BooleanExpression state2Eq(String state2) {
        return StringUtils.isNullOrEmpty(state2) ? null : shop.state2.eq(state2);
    }

    @Override
    public List<Shop> findByWins() {
        List<Long> shopIdWin1st = from(lottoWinShop)
                .where(lottoWinShop.shop.l645YN.eq(true), lottoWinShop.rank.eq(1)) //판매중지 아닌 판매점 중에서 1등 배출 판매점
                .groupBy(lottoWinShop.shop.id) //group by
                .select(lottoWinShop.shop.id)
                .orderBy(lottoWinShop.count().desc()) //1등 배출 횟수 내림차순
                .limit(100) //상위 100건
                .fetch();

        return from(shop)
                .where(shop.id.in(shopIdWin1st))
                .fetch();
    }
}
