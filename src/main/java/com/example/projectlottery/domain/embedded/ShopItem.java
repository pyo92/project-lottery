package com.example.projectlottery.domain.embedded;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@ToString
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class ShopItem {

    @Column
    private boolean l645YN; //로또6/45 판매 여부
    @Column
    private boolean l720YN; //연금복권720+ 판매 여부
    @Column
    private boolean spYN; //스피또 판매 여부

    public static ShopItem of (boolean l645YN, boolean l720YN, boolean spYN) {
        return new ShopItem(l645YN, l720YN, spYN);
    }
}
