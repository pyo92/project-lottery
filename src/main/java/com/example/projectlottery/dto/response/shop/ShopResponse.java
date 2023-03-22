package com.example.projectlottery.dto.response.shop;

import com.example.projectlottery.domain.Shop;
import com.example.projectlottery.domain.type.LottoPurchaseType;

import java.util.Comparator;
import java.util.List;

public record ShopResponse(
        Long id,
        String name,
        String address,
        String tel,
        double longitude,
        double latitude,
        boolean l645YN,
        boolean l720YN,
        boolean spYN,
        long count1stWin,
        long count1stWinAuto,
        long count1stWinManual,
        long count1stWinMix,
        long count2ndWin,
        List<ShopLottoWinResponse> lotto1stWin,
        List<ShopLottoWinResponse> lotto2ndWin
) {

    public static ShopResponse of(Long id, String name, String address, String tel, double longitude, double latitude, boolean l645YN, boolean l720YN, boolean spYN, long count1stWin, long count1stWinAuto, long count1stWinManual, long count1stWinMix, long count2ndWin, List<ShopLottoWinResponse> lotto1stWin, List<ShopLottoWinResponse> lotto2ndWin) {
        return new ShopResponse(id, name, address, tel, longitude, latitude, l645YN, l720YN, spYN, count1stWin, count1stWinAuto, count1stWinManual, count1stWinMix, count2ndWin, lotto1stWin, lotto2ndWin);
    }

    public static ShopResponse from(Shop entity) {
        long count1stWinAuto = entity.getLottoWinShops().stream()
                .filter(lottoWinShop -> lottoWinShop.getRank() == 1 && lottoWinShop.getLottoPurchaseType() == LottoPurchaseType.AUTO)
                .count();
        long count1stWinManual = entity.getLottoWinShops().stream()
                .filter(lottoWinShop -> lottoWinShop.getRank() == 1 && lottoWinShop.getLottoPurchaseType() == LottoPurchaseType.MANUAL)
                .count();
        long count1stWinMix = entity.getLottoWinShops().stream()
                .filter(lottoWinShop -> lottoWinShop.getRank() == 1 && lottoWinShop.getLottoPurchaseType() == LottoPurchaseType.MIX)
                .count();
        long count1stWin = count1stWinAuto + count1stWinManual + count1stWinMix;

        long count2ndWin = entity.getLottoWinShops().stream()
                .filter(lottoWinShop -> lottoWinShop.getRank() == 2)
                .count();

        return ShopResponse.of(
                entity.getId(),
                entity.getName(),
                entity.getAddress(),
                entity.getTel(),
                entity.getLongitude(),
                entity.getLatitude(),
                entity.isL645YN(),
                entity.isL720YN(),
                entity.isSpYN(),
                count1stWin,
                count1stWinAuto,
                count1stWinManual,
                count1stWinMix,
                count2ndWin,
                entity.getLottoWinShops().stream()
                        .filter(lottoWinShop -> lottoWinShop.getRank() == 1)
                        .map(ShopLottoWinResponse::from)
                        .sorted(Comparator.comparing(ShopLottoWinResponse::drawNo)
                                .reversed()
                                .thenComparing(ShopLottoWinResponse::no))
                        .toList(),
                entity.getLottoWinShops().stream()
                        .filter(lottoWinShop -> lottoWinShop.getRank() == 2)
                        .map(ShopLottoWinResponse::from)
                        .sorted(Comparator.comparing(ShopLottoWinResponse::drawNo)
                                .reversed()
                                .thenComparing(ShopLottoWinResponse::no))
                        .toList()
        );
    }
}
