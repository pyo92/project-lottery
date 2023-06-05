package com.example.projectlottery.dto.response;

import com.example.projectlottery.domain.User;
import com.example.projectlottery.domain.type.UserRoleType;

public record UserResponse(
        String userId,
        UserRoleType userRoleType
) {

    public static UserResponse of(String userId, UserRoleType userRoleType) {
        return new UserResponse(userId, userRoleType);
    }

    public static UserResponse from(User entity) {
        return UserResponse.of(
                entity.getUserId(),
                entity.getUserRoleType()
        );
    }
}
