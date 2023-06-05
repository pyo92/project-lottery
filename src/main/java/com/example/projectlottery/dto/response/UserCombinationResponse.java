package com.example.projectlottery.dto.response;

import com.example.projectlottery.domain.UserCombination;
import com.example.projectlottery.domain.type.UserCombinationType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record UserCombinationResponse(
        Long drawNo,
        Integer number1,
        Integer number2,
        Integer number3,
        Integer number4,
        Integer number5,
        Integer number6,
        Integer rank,
        UserCombinationType combinationType,
        LocalDateTime createdAt
) {

    public static UserCombinationResponse of(Long drawNo, Integer number1, Integer number2, Integer number3, Integer number4, Integer number5, Integer number6, Integer rank,  UserCombinationType combinationType, LocalDateTime createdAt) {
        return new UserCombinationResponse(drawNo, number1, number2, number3, number4, number5, number6, rank, combinationType, createdAt);
    }

    public static UserCombinationResponse from(UserCombination entity) {
        return UserCombinationResponse.of(
                entity.getDrawNo(),
                entity.getNumber1(),
                entity.getNumber2(),
                entity.getNumber3(),
                entity.getNumber4(),
                entity.getNumber5(),
                entity.getNumber6(),
                entity.getRank(),
                entity.getCombinationType(),
                entity.getCreatedAt()
        );
    }

    public String toStringCreatedAt() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return createdAt.format(formatter);
    }
}
