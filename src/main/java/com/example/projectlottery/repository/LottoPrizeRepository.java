package com.example.projectlottery.repository;

import com.example.projectlottery.domain.LottoPrize;
import com.example.projectlottery.domain.id.LottoPrizeId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LottoPrizeRepository extends JpaRepository<LottoPrize, LottoPrizeId> {
}
