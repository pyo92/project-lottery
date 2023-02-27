package com.example.projectlottery.service;

import com.example.projectlottery.dto.LottoDto;
import com.example.projectlottery.dto.response.LottoResponse;
import com.example.projectlottery.repository.LottoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class LottoService {

    private final LottoRepository lottoRepository;

    @Transactional(readOnly = true)
    public LottoDto getLotto(Long drawNo) {
        return lottoRepository.findById(drawNo)
                .map(LottoDto::from)
                .orElseThrow(() -> new EntityNotFoundException("해당 회차가 없습니다. (drawNo: " + drawNo + ")"));
    }

    @Transactional(readOnly = true)
    public Long getLatestDrawNo() {
        return lottoRepository.getLatestDrawNo();
    }

    @Transactional(readOnly = true)
    public LottoResponse getLottoResponse(Long drawNo) {
        return lottoRepository.findById(drawNo)
                .map(LottoResponse::from)
                .orElseThrow(() -> new EntityNotFoundException("해당 회차가 없습니다. (drawNo: " + drawNo + ")"));
    }

    public void save(LottoDto dto) {
        lottoRepository.save(dto.toEntity());
    }
}
