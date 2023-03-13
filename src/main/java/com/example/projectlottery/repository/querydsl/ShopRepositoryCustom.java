package com.example.projectlottery.repository.querydsl;

import com.example.projectlottery.domain.Shop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ShopRepositoryCustom {

    Page<Shop> findByStates(String state1, String state2, Pageable pageable);
}
