package com.example.projectlottery.domain;

import com.example.projectlottery.domain.auditing.AuditingFields;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Objects;

@ToString
@Getter
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
