package com.example.projectlottery.repository;

import com.example.projectlottery.domain.LottoPrize;
import com.example.projectlottery.domain.id.LottoPrizeId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LottoPrizeRepository extends JpaRepository<LottoPrize, LottoPrizeId> {

    List<LottoPrize> findByLotto_DrawNo(Long drawNo);
}
