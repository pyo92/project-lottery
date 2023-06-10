package com.example.projectlottery.repository;

import com.example.projectlottery.domain.UserCombination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserCombinationRepository extends JpaRepository<UserCombination, Long> {

    /**
     * 사용자의 번호 조합 생성 목록 조회 (for 번호 조합 생성내역 페이지 회차 selector 생성용)
     * @return 구매 회차 목록
     */
    @Query("select distinct uc.drawNo from UserCombination uc where uc.userId = :userId order by 1 desc")
    List<Long> getCombinationDrawNo(String userId);

    /**
     * 회차별 번호 조합 생성 내역 조회
     * @param drawNo 회차 번호
     * @return 회차별 번호 조합 생성 내역
     */
    List<UserCombination> findByDrawNo(Long drawNo);

    /**
     * 회차별 번호 조합 생성 내역 조회
     * @param userId 사용자 id
     * @param drawNo 회차 번호
     * @return 회차별 번호 조합 생성 내역
     */
    List<UserCombination> findByUserIdAndDrawNo(String userId, Long drawNo);
}
