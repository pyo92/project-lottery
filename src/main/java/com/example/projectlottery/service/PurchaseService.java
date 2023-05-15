package com.example.projectlottery.service;

import com.example.projectlottery.domain.Purchase;
import com.example.projectlottery.dto.request.LottoGameRequest;
import com.example.projectlottery.dto.request.PurchaseRequest;
import com.example.projectlottery.dto.response.LottoGameResponse;
import com.example.projectlottery.repository.PurchaseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;

    /**
     * 번호 + 자동 선택을 바탕으로 하나의 게임을 만든다.
     */
    public LottoGameResponse makeLottoGameSet(LottoGameRequest request) {
        Set<Integer> gameSet = new HashSet<>();
        //입력받은 번호를 다 넣는다.
        gameSet.add(request.number1());
        gameSet.add(request.number2());
        gameSet.add(request.number3());
        gameSet.add(request.number4());
        gameSet.add(request.number5());
        gameSet.add(request.number6());

        //NULL 값을 날려준다.
        gameSet.remove(null);

        Random rnd = new Random();
        while (gameSet.size() < 6) {
            gameSet.add(rnd.nextInt(45) + 1); //set 이므로 중복값이 아닐때만 들어간다.
        }

        return LottoGameResponse.of(gameSet);
    }

    /**
     * 회차별 구매 이력을 기록한다.
     */
    public void savePurchaseHistory(PurchaseRequest request) {
        purchaseRepository.save(request.toEntity());
    }

    /**
     * 회차별 구매 이력을 가져온다.
     */
    public void getPurchaseHistory(Long drawNo) {
        List<Purchase> byDrawNo = purchaseRepository.findByDrawNo(drawNo);
    }
}
