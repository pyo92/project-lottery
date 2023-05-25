package com.example.projectlottery.service;

import com.example.projectlottery.dto.LottoPrizeDto;
import com.example.projectlottery.dto.response.LottoPrizeResponse;
import com.example.projectlottery.repository.LottoPrizeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class LottoPrizeService {

    private final LottoPrizeRepository lottoPrizeRepository;

    /**
     * 로또 등위별 당첨정보 저장 (for 동행복권 로또추첨결과 scrap)
     * @param dto 등위별 당첨정보 dto
     */
    public void save(LottoPrizeDto dto) {
        lottoPrizeRepository.save(dto.toEntity());
    }

    /**
     * 로또 회차별 등위별 당첨정보 조회
     * @param drawNo 회차 번호
     * @return 등위별 당첨정보 목록
     */
    @Transactional(readOnly = true)
    List<LottoPrizeResponse> getPrizeResponse(Long drawNo) {
        return lottoPrizeRepository.findByLotto_DrawNo(drawNo).stream()
                .map(LottoPrizeResponse::from)
                .toList();
    }
}
