package com.example.projectlottery.repository.querydsl;

import com.example.projectlottery.domain.QLotto;
import com.example.projectlottery.domain.QLottoPrize;
import com.example.projectlottery.domain.QLottoWinShop;
import com.example.projectlottery.domain.QShop;
import com.example.projectlottery.dto.response.APIResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class AdminRepositoryCustomImpl extends QuerydslRepositorySupport implements AdminRepositoryCustom {

    private final QShop shop;
    private final QLotto lotto;
    private final QLottoPrize lottoPrize;
    private final QLottoWinShop lottoWinShop;

    public AdminRepositoryCustomImpl() {
        super(Object.class);

        shop = QShop.shop;
        lotto = QLotto.lotto;
        lottoPrize = QLottoPrize.lottoPrize;
        lottoWinShop = QLottoWinShop.lottoWinShop;
    }

    @Override
    public List<APIResponse> getAllAPIModifiedAt() {
        List<APIResponse> result = new ArrayList<>();

        result.addAll(
                from(shop)
                        .select(Projections.constructor(APIResponse.class,
                                Expressions.asString("/scrap/L645/shop"),
                                Expressions.asString("shop"),
                                shop.modifiedAt.max()))
                        .fetch()
        );

        result.addAll(
                from(lotto)
                        .select(Projections.constructor(APIResponse.class,
                                Expressions.asString("/scrap/L645/win/number"),
                                Expressions.asString("lotto"),
                                lotto.modifiedAt.max()))
                        .fetch()
        );

        result.addAll(
                from(lottoPrize)
                        .select(Projections.constructor(APIResponse.class,
                                Expressions.asString("/scrap/L645/win/prize"),
                                Expressions.asString("lotto_prize"),
                                lottoPrize.modifiedAt.max()))
                        .fetch()
        );

        result.addAll(
                from(lottoWinShop)
                        .select(Projections.constructor(APIResponse.class,
                                Expressions.asString("/scrap/L645/win/shop"),
                                Expressions.asString("lotto_win_shop"),
                                lottoWinShop.modifiedAt.max()))
                        .fetch()
        );

        return result;
    }
}
