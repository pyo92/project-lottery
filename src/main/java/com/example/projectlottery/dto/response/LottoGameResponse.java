package com.example.projectlottery.dto.response;

import java.util.Arrays;
import java.util.Set;

public record LottoGameResponse(
        int number1,
        int number2,
        int number3,
        int number4,
        int number5,
        int number6
) {

    public static LottoGameResponse of(int number1, int number2, int number3, int number4, int number5, int number6) {
        return new LottoGameResponse(number1, number2, number3, number4, number5, number6);
    }

    public static LottoGameResponse of(Set<Integer> gameSet) {
        int i = 0;
        int[] numbers = new int[6];
        for (Integer n : gameSet) {
            numbers[i++] = n;
        }

        Arrays.sort(numbers);

        return LottoGameResponse.of(
                numbers[0],
                numbers[1],
                numbers[2],
                numbers[3],
                numbers[4],
                numbers[5]
        );
    }
}
