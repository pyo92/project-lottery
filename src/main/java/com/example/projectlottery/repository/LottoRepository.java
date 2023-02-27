package com.example.projectlottery.repository;

import com.example.projectlottery.domain.Lotto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface LottoRepository extends JpaRepository<Lotto, Long> {

    @Query("select max(l.drawNo) from Lotto l")
    Long getLatestDrawNo(); //회차 선택 combo-box 생성 용도로 사용할 JPQL
}
