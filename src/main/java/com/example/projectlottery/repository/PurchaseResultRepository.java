package com.example.projectlottery.repository;

import com.example.projectlottery.domain.PurchaseResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PurchaseResultRepository extends JpaRepository<PurchaseResult, Long> {


    /**
     * 사용자의 구매 회차 목록 조회 (for 로또구매내역 페이지 회차 selector 생성용)
     * @return 구매 회차 목록
     */
    @Query("select distinct pr.drawNo from PurchaseResult pr where pr.userId = :userId order by 1 desc")
    List<Long> getPurchasedDrawNo(String userId);

    /**
     * 회차별 구매내역 조회
     * @param userId 사용자 id
     * @param drawNo 회차 번호
     * @return 회차별 구매내역
     */
    List<PurchaseResult> findByUserIdAndDrawNo(String userId, Long drawNo);
}
