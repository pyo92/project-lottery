package com.example.projectlottery.repository;

import com.example.projectlottery.domain.Shop;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Set;

public interface ShopRepository extends JpaRepository<Shop, Long> {

    Set<Shop> findByL645YNAndScrapedDtBefore(boolean l645YN, LocalDate scrapedDt);
}
