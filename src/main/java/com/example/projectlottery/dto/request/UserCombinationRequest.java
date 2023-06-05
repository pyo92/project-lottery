package com.example.projectlottery.dto.request;

import com.example.projectlottery.domain.UserCombination;
import com.example.projectlottery.domain.type.UserCombinationType;
import org.springframework.security.core.context.SecurityContextHolder;

public record UserCombinationRequest(
        Long drawNo,
        String combinationType,
        Integer number1,
        Integer number2,
        Integer number3,
        Integer number4,
        Integer number5,
        Integer number6
) {

    public static UserCombinationRequest of(Long drawNo, String combinationType, Integer number1, Integer number2, Integer number3, Integer number4, Integer number5, Integer number6) {
        return new UserCombinationRequest(drawNo, combinationType, number1, number2, number3, number4, number5, number6);
    }

    public UserCombination toEntity() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        UserCombinationType combinationType = UserCombinationType.valueOf(combinationType());

        return UserCombination.of(
                userId,
                drawNo,
                combinationType,
                number1,
                number2,
                number3,
                number4,
                number5,
                number6,
                null //조합 번호 생성 당시에는 추첨하지 않았기에 등위를 알 수 없다.
        );
    }
}
