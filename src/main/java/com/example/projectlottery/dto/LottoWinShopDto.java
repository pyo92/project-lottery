package com.example.projectlottery.dto;

import com.example.projectlottery.domain.LottoWinShop;
import com.example.projectlottery.domain.type.LottoPurchaseType;

public record LottoWinShopDto(
        LottoDto lottoDto,
        Integer rank,
        Integer no,
        ShopDto shopDto,
        LottoPurchaseType lottoPurchaseType,
        String displayAddress
) {

    public static LottoWinShopDto of(LottoDto lottoDto, Integer rank, Integer no, ShopDto shopDto, LottoPurchaseType lottoPurchaseType, String displayAddress) {
        return new LottoWinShopDto(lottoDto, rank, no, shopDto, lottoPurchaseType, displayAddress);
    }

    public static LottoWinShopDto from(LottoWinShop entity) {
        return LottoWinShopDto.of(
                LottoDto.from(entity.getLotto()),
                entity.getRank(),
                entity.getNo(),
                ShopDto.from(entity.getShop()),
                entity.getLottoPurchaseType(),
                entity.getDisplayAddress()
        );
    }

    public LottoWinShop toEntity() {
        return LottoWinShop.of(lottoDto.toEntity(), rank, no, shopDto.toEntity(), lottoPurchaseType, displayAddress);
    }
}
