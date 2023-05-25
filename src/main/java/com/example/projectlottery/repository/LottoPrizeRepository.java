package com.example.projectlottery.repository;

import com.example.projectlottery.domain.LottoPrize;
import com.example.projectlottery.domain.id.LottoPrizeId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LottoPrizeRepository extends JpaRepository<LottoPrize, LottoPrizeId> {

    /**
     * 회차별 등위에 따른 당첨정보 상세 목록 조회
     * @param drawNo 회차 번호
     * @return 로또 등위별 당첨정보 상세 목록
     */
    List<LottoPrize> findByLotto_DrawNo(Long drawNo);
}
