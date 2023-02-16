package com.example.projectlottery.domain;

import com.example.projectlottery.domain.id.LottoPrizeId;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@ToString
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(LottoPrizeId.class)
@Entity
public class LottoPrize {

    @ManyToOne(fetch = FetchType.LAZY)
    @Id
    private Lotto lotto; //회차
    @Id
    private Integer rank; //등위

    @Column(nullable = false)
    private long winAmount; //총 당첨금액
    @Column(nullable = false)
    private long winGameCount; //당첨게임 수
    @Column(nullable = false)
    private long winAmountPerGame; //게임당 당첨금액

    public static LottoPrize of(Lotto lotto, Integer rank, long winAmount, long winGameCount, long winAmountPerGame) {
        return new LottoPrize(lotto, rank, winAmount, winGameCount, winAmountPerGame);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LottoPrize that)) return false;
        return Objects.equals(lotto, that.lotto) && Objects.equals(rank, that.rank);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lotto, rank);
    }
}
