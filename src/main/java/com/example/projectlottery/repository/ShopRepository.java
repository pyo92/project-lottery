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

    Set<Shop> findByL645YNAndScrapedDtBefore(boolean l645YN, LocalDate scrapedDt);

    @Override
    default void customize(QuerydslBindings bindings, QShop root) {
        bindings.excludeUnlistedProperties(true);
        bindings.including(root.name);
        bindings.bind(root.name).first(StringExpression::contains);
    }
}
