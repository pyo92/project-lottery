package com.example.projectlottery.repository;

import com.example.projectlottery.domain.LottoWinShop;
import com.example.projectlottery.domain.id.LottoWinShopId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LottoWinShopRepository extends JpaRepository<LottoWinShop, LottoWinShopId> {
}
