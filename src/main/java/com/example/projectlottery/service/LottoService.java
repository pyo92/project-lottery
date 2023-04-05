package com.example.projectlottery.service;

import com.example.projectlottery.domain.Lotto;
import com.example.projectlottery.dto.LottoDto;
import com.example.projectlottery.dto.response.LottoPrizeResponse;
import com.example.projectlottery.dto.response.LottoResponse;
import com.example.projectlottery.dto.response.querydsl.QShopSummary;
import com.example.projectlottery.repository.LottoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class LottoService {

    private final LottoRepository lottoRepository;

    private final LottoPrizeService lottoPrizeService;

    private final RedisTemplateService redisTemplateService;

    @Transactional(readOnly = true)
    public LottoDto getLotto(Long drawNo) {
        return lottoRepository.findById(drawNo)
                .map(LottoDto::from)
                .orElseThrow(() -> new EntityNotFoundException("해당 회차가 없습니다. (drawNo: " + drawNo + ")"));
    }

    @Transactional(readOnly = true)
    public Long getLatestDrawNo() {
        Long latestDrawNo = redisTemplateService.getLatestDrawNo();

        if (Objects.isNull(latestDrawNo)) { //redis 조회 실패 시, redis 갱신
            latestDrawNo = lottoRepository.getLatestDrawNo();

            redisTemplateService.saveLatestDrawNo(latestDrawNo);
        }

        return latestDrawNo;
    }

    @Transactional(readOnly = true)
    public LottoResponse getLottoResponse(Long drawNo) {
        LottoResponse lottoResponse = redisTemplateService.getWinDetail(drawNo);

        if (Objects.isNull(lottoResponse)) { //redis 조회 실패 시, redis 갱신
            Optional<Lotto> lottoByDrawNo = lottoRepository.findById(drawNo);

            if (lottoByDrawNo.isPresent()) {
                List<LottoPrizeResponse> prizeResponse = lottoPrizeService.getPrizeResponse(drawNo);

                List<QShopSummary> QShopSummaryWin1St = lottoRepository.getShopSummaryResponseForLotto(drawNo, 1);
                List<QShopSummary> QShopSummaryWin2Nd = lottoRepository.getShopSummaryResponseForLotto(drawNo, 2);

                lottoResponse = LottoResponse.from(lottoByDrawNo.get(), prizeResponse, QShopSummaryWin1St, QShopSummaryWin2Nd);

            } else {
                throw new EntityNotFoundException("해당 회차가 없습니다. (drawNo: " + drawNo + ")");
            }

            redisTemplateService.saveWinDetail(lottoResponse);
        }

        return lottoResponse;
    }

    public void save(LottoDto dto) {
        lottoRepository.save(dto.toEntity());
    }
}
