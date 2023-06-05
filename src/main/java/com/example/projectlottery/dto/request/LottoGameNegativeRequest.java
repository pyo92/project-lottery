package com.example.projectlottery.dto.request;

import java.util.ArrayList;
import java.util.List;

public record LottoGameNegativeRequest(
        Integer number1,
        Integer number2,
        Integer number3,
        Integer number4,
        Integer number5,
        Integer number6,
        Integer number7,
        Integer number8,
        Integer number9,
        Integer number10,
        Integer number11,
        Integer number12,
        Integer number13,
        Integer number14,
        Integer number15,
        Integer number16,
        Integer number17,
        Integer number18,
        Integer number19,
        Integer number20,
        Integer number21,
        Integer number22,
        Integer number23,
        Integer number24,
        Integer number25,
        Integer number26,
        Integer number27,
        Integer number28,
        Integer number29,
        Integer number30,
        Integer number31,
        Integer number32,
        Integer number33,
        Integer number34,
        Integer number35,
        Integer number36,
        Integer number37,
        Integer number38,
        Integer number39
) {

    public static LottoGameNegativeRequest of(Integer number1, Integer number2, Integer number3, Integer number4, Integer number5, Integer number6, Integer number7, Integer number8, Integer number9, Integer number10, Integer number11, Integer number12, Integer number13, Integer number14, Integer number15, Integer number16, Integer number17, Integer number18, Integer number19, Integer number20, Integer number21, Integer number22, Integer number23, Integer number24, Integer number25, Integer number26, Integer number27, Integer number28, Integer number29, Integer number30, Integer number31, Integer number32, Integer number33, Integer number34, Integer number35, Integer number36, Integer number37, Integer number38, Integer number39) {
        return new LottoGameNegativeRequest(number1, number2, number3, number4, number5, number6, number7, number8, number9, number10, number11, number12, number13, number14, number15, number16, number17, number18, number19, number20, number21, number22, number23, number24, number25, number26, number27, number28, number29, number30, number31, number32, number33, number34, number35, number36, number37, number38, number39);
    }

    public List<Integer> toList() {
        List<Integer> list = new ArrayList<>();
        list.add(number1);
        list.add(number2);
        list.add(number3);
        list.add(number4);
        list.add(number5);
        list.add(number6);
        list.add(number7);
        list.add(number8);
        list.add(number9);
        list.add(number10);
        list.add(number11);
        list.add(number12);
        list.add(number13);
        list.add(number14);
        list.add(number15);
        list.add(number16);
        list.add(number17);
        list.add(number18);
        list.add(number19);
        list.add(number20);
        list.add(number21);
        list.add(number22);
        list.add(number23);
        list.add(number24);
        list.add(number25);
        list.add(number26);
        list.add(number27);
        list.add(number28);
        list.add(number29);
        list.add(number30);
        list.add(number31);
        list.add(number32);
        list.add(number33);
        list.add(number34);
        list.add(number35);
        list.add(number36);
        list.add(number37);
        list.add(number38);
        list.add(number39);

        return list;
    }
}
