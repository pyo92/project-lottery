package com.example.projectlottery.domain.id;

import com.example.projectlottery.domain.Lotto;
import lombok.Data;

import java.io.Serializable;

@Data
public class LottoWinShopId implements Serializable {

    private Lotto lotto;
    private Long rank;
    private Long no;
}
