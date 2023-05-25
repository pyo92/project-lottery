package com.example.projectlottery.service;

import com.example.projectlottery.dto.LottoWinShopDto;
import com.example.projectlottery.repository.LottoWinShopRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class LottoWinShopService {

    private final LottoWinShopRepository lottoWinShopRepository;

    /**
     * 로또 당첨 판매점 정보 저장 (for 동행복권 로또 당첨 판매점 scrap)
     * @param dto 로또 당첨 판매점 dto
     */
    public void save(LottoWinShopDto dto) {
        lottoWinShopRepository.save(dto.toEntity());
    }
}
