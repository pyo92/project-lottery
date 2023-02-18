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

    public void save(LottoWinShopDto dto) {
        lottoWinShopRepository.save(dto.toEntity());
    }
}
