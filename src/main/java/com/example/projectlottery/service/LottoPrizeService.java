package com.example.projectlottery.service;

import com.example.projectlottery.dto.LottoPrizeDto;
import com.example.projectlottery.repository.LottoPrizeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class LottoPrizeService {

    private final LottoPrizeRepository lottoPrizeRepository;

    public void save(LottoPrizeDto dto) {
        lottoPrizeRepository.save(dto.toEntity());
    }
}
