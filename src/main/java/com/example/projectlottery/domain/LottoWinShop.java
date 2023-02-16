package com.example.projectlottery.domain;

import com.example.projectlottery.domain.id.LottoWinShopId;
import com.example.projectlottery.domain.type.LottoPurchaseType;
import jakarta.persistence.*;
import lombok.*;

@ToString
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(LottoWinShopId.class)
@Entity
public class LottoWinShop {

    @ManyToOne(fetch = FetchType.LAZY)
    @Id
    private Lotto lotto;
    @Id
    private Long rank;
    @Id
    private Long no;

    @ManyToOne(fetch = FetchType.LAZY)
    private Shop shop;

    @Enumerated(EnumType.STRING)
    @Column
    private LottoPurchaseType lottoPurchaseType;

    public static LottoWinShop of(Lotto lotto, Long rank, Long no, Shop shop, LottoPurchaseType lottoPurchaseType) {
        return new LottoWinShop(lotto, rank, no, shop, lottoPurchaseType);
    }
}
