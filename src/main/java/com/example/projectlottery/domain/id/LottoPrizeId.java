package com.example.projectlottery.domain.id;

import com.example.projectlottery.domain.Lotto;
import lombok.Data;

import java.io.Serializable;

@Data
public class LottoPrizeId implements Serializable {

    private Lotto lotto;
    private Integer rank;
}
