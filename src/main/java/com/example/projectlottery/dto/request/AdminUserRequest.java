package com.example.projectlottery.dto.request;

import java.util.List;

public record AdminUserRequest(
        String userId,
        List<String> userRoles
) {

}
