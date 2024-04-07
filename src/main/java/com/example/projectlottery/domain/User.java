package com.example.projectlottery.domain;

import com.example.projectlottery.domain.auditing.AuditingFields;
import com.example.projectlottery.domain.type.UserRoleType;
import jakarta.persistence.*;
import lombok.*;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@ToString(callSuper = true)
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class User extends AuditingFields {

    @Id
    private String userId;

    @OrderBy(value = "ord")
    @ManyToMany
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_name")
    )
    private Set<UserRole> userRoles = new LinkedHashSet<>();

    public static User of(String userId, List<UserRoleType> userRoleTypes) {
        return new User(
                userId,
                userRoleTypes.stream()
                        .map(UserRole::from)
                        .collect(Collectors.toUnmodifiableSet())
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User that)) return false;
        return this.userId != null && this.userId.equals(that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.userId);
    }
}
