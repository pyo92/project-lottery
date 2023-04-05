package com.example.projectlottery.dto.response;

import com.example.projectlottery.domain.LottoPrize;

/**
 *  `LottoResponse` 에 포함되는 회차별 등위별 당첨게임수, 당첨금 등 정보 response
 */
public record LottoPrizeResponse(
        Integer rank,
        long winAmount,
        long winGameCount,
        long winAmountPerGame
) {

    public static LottoPrizeResponse of(Integer rank, long winAmount, long winGameCount, long winAmountPerGame) {
        return new LottoPrizeResponse(
                rank,
                winAmount,
                winGameCount,
                winAmountPerGame
        );
    }

    public static LottoPrizeResponse from(LottoPrize entity) {
        return LottoPrizeResponse.of(
                entity.getRank(),
                entity.getWinAmount(),
                entity.getWinGameCount(),
                entity.getWinAmountPerGame()
        );
    }
}
