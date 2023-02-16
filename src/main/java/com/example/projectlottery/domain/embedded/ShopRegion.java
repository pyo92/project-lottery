package com.example.projectlottery.domain.embedded;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@ToString
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class ShopRegion {

    @Column
    private String state1; //시.도
    @Column
    private String state2; //시.군.구
    @Column
    private String state3; //읍.면.동.리

    public static ShopRegion of(String state1, String state2, String state3) {
        return new ShopRegion(state1, state2, state3);
    }
}
