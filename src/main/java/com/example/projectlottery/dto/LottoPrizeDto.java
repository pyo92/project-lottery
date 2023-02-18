package com.example.projectlottery.dto;

import com.example.projectlottery.domain.LottoPrize;

public record LottoPrizeDto(
        LottoDto lottoDto,
        Integer rank,
        long winAmount,
        long winGameCount,
        long winAmountPerGame
) {

    public static LottoPrizeDto of(LottoDto lottoDto, Integer rank, long winAmount, long winGameCount, long winAmountPerGame) {
        return new LottoPrizeDto(lottoDto, rank, winAmount, winGameCount, winAmountPerGame);
    }

    public static LottoPrizeDto from(LottoPrize entity) {
        return LottoPrizeDto.of(
                LottoDto.from(entity.getLotto()),
                entity.getRank(),
                entity.getWinAmount(),
                entity.getWinGameCount(),
                entity.getWinAmountPerGame()
        );
    }

    public LottoPrize toEntity() {
        return LottoPrize.of(lottoDto.toEntity(), rank, winAmount, winGameCount, winAmountPerGame);
    }
}
