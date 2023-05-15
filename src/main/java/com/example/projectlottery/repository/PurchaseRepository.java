package com.example.projectlottery.repository;

import com.example.projectlottery.domain.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    List<Purchase> findByDrawNo(Long drawNo);
}
