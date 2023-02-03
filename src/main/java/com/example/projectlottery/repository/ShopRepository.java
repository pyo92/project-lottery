package com.example.projectlottery.repository;

import com.example.projectlottery.domain.Shop;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopRepository extends JpaRepository<Shop, Long> {
}
