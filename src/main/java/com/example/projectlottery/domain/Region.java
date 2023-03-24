package com.example.projectlottery.domain;

import com.example.projectlottery.domain.auditing.AuditingFields;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@ToString
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(indexes = {
        @Index(columnList = "reg"),
        @Index(columnList = "state1"),
        @Index(columnList = "state2"),
        @Index(columnList = "parentReg"),
})
@Entity
public class Region extends AuditingFields {

    @Column(length = 5)
    @Id
    private String reg;

    @Column
    private String state1;

    @Column
    private String state2;

    @Column(updatable = false, length = 5)
    private String parentReg;

    public static Region of(String reg, String state1, String state2, String parentReg) {
        return new Region(reg, state1, state2, parentReg);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Region region)) return false;
        return reg != null && reg.equals(region.reg);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reg);
    }
}
