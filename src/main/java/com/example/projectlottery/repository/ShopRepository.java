package com.example.projectlottery.repository;

import com.example.projectlottery.domain.QShop;
import com.example.projectlottery.domain.Shop;
import com.example.projectlottery.repository.querydsl.ShopRepositoryCustom;
import com.querydsl.core.types.dsl.StringExpression;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;

import java.time.LocalDate;
import java.util.Set;

public interface ShopRepository extends
        JpaRepository<Shop, Long>,
        ShopRepositoryCustom,
        QuerydslPredicateExecutor<Shop>,
        QuerydslBinderCustomizer<QShop> {

    /**
     * 판매 중단 판매점 목록 조회 (for 동행복권 로또 판매점 정보 scrap)
     * @param state1 시.도
     * @param l645YN 로또 판매 여부
     * @param scrapedDt 마지막 scrap 일자
     * @return 판매 중단 판매점 목록
     */
    Set<Shop> findByState1EqualsAndL645YNAndScrapedDtBefore(String state1, boolean l645YN, LocalDate scrapedDt);

    @Override
    default void customize(QuerydslBindings bindings, QShop root) {
        bindings.excludeUnlistedProperties(true);
        bindings.including(root.name);
        bindings.bind(root.name).first(StringExpression::contains);
    }
}
