package com.example.projectlottery.repository.querydsl;

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

    public ShopRepositoryCustomImpl() {
        super(Shop.class);
        this.shop = QShop.shop;
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


}
