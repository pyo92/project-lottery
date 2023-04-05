package com.example.projectlottery.dto.response;

import com.example.projectlottery.domain.Shop;
import com.example.projectlottery.dto.response.querydsl.QLottoSummary;
import com.example.projectlottery.dto.response.querydsl.QShopWinSummary;

import java.util.List;

/**
 *  복권판매점상세 페이지에 포함되는 모든 정보를 담은 response (for redis cache too)
 */
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
        long amountSum1stWin,
        long amountMax1stWin,
        long amountMin1stWin,
        long count2ndWin,
        long amountSum2ndWin,
        long amountMax2ndWin,
        long amountMin2ndWin,
        List<QLottoSummary> lotto1stWin,
        List<QLottoSummary> lotto2ndWin
) {

    public static ShopResponse of(Long id, String name, String address, String tel, double longitude, double latitude, boolean l645YN, boolean l720YN, boolean spYN,
                                  long count1stWin, long count1stWinAuto, long count1stWinManual, long amountSum1stWin, long amountMax1stWin, long amountMin1stWin,
                                  long count2ndWin, long amountSum2ndWin, long amountMax2ndWin, long amountMin2ndWin, List<QLottoSummary> lotto1stWin, List<QLottoSummary> lotto2ndWin) {
        return new ShopResponse(id, name, address, tel, longitude, latitude, l645YN, l720YN, spYN,
                count1stWin, count1stWinAuto, count1stWinManual, amountSum1stWin, amountMax1stWin, amountMin1stWin,
                count2ndWin, amountSum2ndWin, amountMax2ndWin, amountMin2ndWin, lotto1stWin, lotto2ndWin);
    }

    public static ShopResponse from(Shop entity, QShopWinSummary QShopWinSummary, List<QLottoSummary> r1, List<QLottoSummary> r2) {
//        List<LottoWinShop> lotto1stWinShops = entity.getLottoWinShops().stream()
//                .filter(lottoWinShop -> lottoWinShop.getRank() == 1)
//                .toList();
//        long count1stWinAuto = lotto1stWinShops.stream()
//                .filter(lottoWinShop -> lottoWinShop.getLottoPurchaseType() == LottoPurchaseType.AUTO)
//                .count();
//        long count1stWinManual = lotto1stWinShops.stream()
//                .filter(lottoWinShop -> lottoWinShop.getLottoPurchaseType() != LottoPurchaseType.AUTO)
//                .count();
//        long count1stWin = count1stWinAuto + count1stWinManual;
//
//        List<Long> lotto1stWinAmounts = lotto1stWinShops.stream()
//                .map(lottoWinShop -> lottoWinShop.getLotto().getLottoPrizes().stream()
//                        .filter(lottoPrize -> lottoPrize.getRank() == 1)
//                        .mapToLong(LottoPrize::getWinAmountPerGame)
//                        .sum())
//                .toList();
//        long amountSum1stWin = lotto1stWinAmounts.stream().mapToLong(value -> value).sum();
//        long amountMax1stWin = lotto1stWinAmounts.stream().mapToLong(value -> value).max().orElse(0L);
//        long amountMin1stWin = lotto1stWinAmounts.stream().mapToLong(value -> value).min().orElse(0L);
//
//        List<LottoWinShop> lotto2ndWinShops = entity.getLottoWinShops().stream()
//                .filter(lottoWinShop -> lottoWinShop.getRank() == 2)
//                .toList();
//        long count2ndWin = lotto2ndWinShops.stream().count();
//
//        List<Long> lotto2ndWinAmounts = lotto2ndWinShops.stream()
//                .map(lottoWinShop -> lottoWinShop.getLotto().getLottoPrizes().stream()
//                        .filter(lottoPrize -> lottoPrize.getRank() == 2)
//                        .mapToLong(LottoPrize::getWinAmountPerGame)
//                        .sum())
//                .toList();
//        long amountSum2ndWin = lotto2ndWinAmounts.stream().mapToLong(value -> value).sum();
//        long amountMax2ndWin = lotto2ndWinAmounts.stream().mapToLong(value -> value).max().orElse(0L);
//        long amountMin2ndWin = lotto2ndWinAmounts.stream().mapToLong(value -> value).min().orElse(0L);

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
                QShopWinSummary.count1stWin(),
                QShopWinSummary.count1stWinAuto(),
                QShopWinSummary.count1stWinManual(),
                QShopWinSummary.amountSum1stWin(),
                QShopWinSummary.amountMax1stWin(),
                QShopWinSummary.amountMin1stWin(),
                QShopWinSummary.count2ndWin(),
                QShopWinSummary.amountSum2ndWin(),
                QShopWinSummary.amountMax2ndWin(),
                QShopWinSummary.amountMin2ndWin(),
                r1,
                r2
        );
    }
}
