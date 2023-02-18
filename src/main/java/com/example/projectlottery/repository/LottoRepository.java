package com.example.projectlottery.repository;

import com.example.projectlottery.domain.Lotto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LottoRepository extends JpaRepository<Lotto, Long> {
}
