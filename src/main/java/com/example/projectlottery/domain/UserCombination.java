package com.example.projectlottery.domain;

import com.example.projectlottery.domain.auditing.AuditingFields;
import com.example.projectlottery.domain.type.UserCombinationType;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@ToString(callSuper = true)
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(indexes = {
        @Index(columnList = "userId, drawNo"),
        @Index(columnList = "userId, drawNo, combinationType")
})
@Entity
public class UserCombination extends AuditingFields {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column
    private String userId;

    @Column
    private Long drawNo;

    @Enumerated(EnumType.STRING)
    @Column
    private UserCombinationType combinationType; //조합타입

    @Column
    private Integer number1; //번호1
    @Column
    private Integer number2; //번호2
    @Column
    private Integer number3; //번호3
    @Column
    private Integer number4; //번호4
    @Column
    private Integer number5; //번호5
    @Column
    private Integer number6; //번호6

    @Column
    private Integer rank; //등위


    public static UserCombination of(Long id, String userId, Long drawNo, UserCombinationType combinationType, Integer number1, Integer number2, Integer number3, Integer number4, Integer number5, Integer number6, Integer rank) {
        return new UserCombination(id, userId, drawNo, combinationType, number1, number2, number3, number4, number5, number6, rank);
    }

    public static UserCombination of(String userId, Long drawNo, UserCombinationType combinationType, Integer number1, Integer number2, Integer number3, Integer number4, Integer number5, Integer number6, Integer rank) {
        return UserCombination.of(null, userId, drawNo, combinationType, number1, number2, number3, number4, number5, number6, rank);
    }

    public void updateRank(Integer rank) {
        this.rank = rank;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserCombination that)) return false;
        return this.id != null && this.id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }
}
