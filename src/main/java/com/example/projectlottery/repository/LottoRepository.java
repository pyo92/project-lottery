package com.example.projectlottery.repository;

import com.example.projectlottery.domain.Lotto;
import com.example.projectlottery.repository.querydsl.LottoRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LottoRepository extends JpaRepository<Lotto, Long>, LottoRepositoryCustom {

    /**
     * 가장 최근 회차 번호 조회 (for 로또추첨결과 페이지 회차 selector 생성용)
     * @return
     */
    @Query("select max(l.drawNo) from Lotto l")
    Long getLatestDrawNo();
}
