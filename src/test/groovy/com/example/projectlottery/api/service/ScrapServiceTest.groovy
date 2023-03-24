package com.example.projectlottery.api.service

import com.example.projectlottery.IntegrationContainerBaseTest
import com.example.projectlottery.repository.LottoPrizeRepository
import com.example.projectlottery.repository.LottoRepository
import com.example.projectlottery.repository.LottoWinShopRepository
import com.example.projectlottery.repository.ShopRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

import java.time.LocalDate

@Transactional
class ScrapServiceTest extends IntegrationContainerBaseTest {

    @Autowired
    private ScrapLotteryShopService scrapLotteryShopService
    @Autowired
    private ScrapLotteryWinService scrapLotteryWinService
    @Autowired
    private ScrapLotteryWinShopService scrapLotteryWinShopService

    //스크랩핑이 정상 동작하는지 테스트하는 것이 중점이므로 repository 사용
    @Autowired
    private ShopRepository shopRepository
    @Autowired
    private LottoRepository lottoRepository
    @Autowired
    private LottoPrizeRepository lottoPrizeRepository
    @Autowired
    private LottoWinShopRepository lottoWinShopRepository

    def "동행복권 스크랩핑 테스트 - 복권 판매점"() {
        given:
        String state = "SEJONG"

        when:
        def oldShops = shopRepository.findAll()

        scrapLotteryShopService.getShopL645(state)

        def shops = shopRepository.findAll()

        then:
        oldShops.size() == 0
        oldShops.size() < shops.size()
    }

    def "동행복권 스크랩핑 테스트 - 복권 당첨번호"() {
        given:
        Long drawNo = 1059L

        when:
        scrapLotteryWinService.getResultsL645(drawNo, drawNo)

        def lotto = lottoRepository.findById(drawNo).orElse(null)
        def lottoPrizes = lottoPrizeRepository.findAll()

        then:
        lotto.getDrawNo() == drawNo
        lotto.getDrawDt() == LocalDate.of(2023, 3, 18)
        lotto.getLottoWinNumber().getNumber1() == 7
        lotto.getLottoWinNumber().getNumber2() == 10
        lotto.getLottoWinNumber().getNumber3() == 22
        lotto.getLottoWinNumber().getNumber4() == 25
        lotto.getLottoWinNumber().getNumber5() == 34
        lotto.getLottoWinNumber().getNumber6() == 40
        lotto.getLottoWinNumber().getNumberB() == 27

        lottoPrizes.size() == 5

        lottoPrizes.get(0).getRank() == 1
        lottoPrizes.get(0).getWinAmount() == 26431190253
        lottoPrizes.get(0).getWinGameCount() == 13
        lottoPrizes.get(0).getWinAmountPerGame() == 2033168481

        lottoPrizes.get(1).getRank() == 2
        lottoPrizes.get(1).getWinAmount() == 4405198440
        lottoPrizes.get(1).getWinGameCount() == 83
        lottoPrizes.get(1).getWinAmountPerGame() == 53074680

        lottoPrizes.get(2).getRank() == 3
        lottoPrizes.get(2).getWinAmount() == 4405198774
        lottoPrizes.get(2).getWinGameCount() == 3127
        lottoPrizes.get(2).getWinAmountPerGame() == 1408762

        lottoPrizes.get(3).getRank() == 4
        lottoPrizes.get(3).getWinAmount() == 7597050000
        lottoPrizes.get(3).getWinGameCount() == 151941
        lottoPrizes.get(3).getWinAmountPerGame() == 50000

        lottoPrizes.get(4).getRank() == 5
        lottoPrizes.get(4).getWinAmount() == 12584775000
        lottoPrizes.get(4).getWinGameCount() == 2516955
        lottoPrizes.get(4).getWinAmountPerGame() == 5000
    }

    def "동행복권 스크랩핑 테스트 - 당첨 판매점"() {
        given:
        Long drawNo = 1059L

        // 추첨 정보를 먼저 스크랩핑 한 상태여야 한다.
        scrapLotteryWinService.getResultsL645(drawNo, drawNo)

        when:
        scrapLotteryWinShopService.getWinShopL645(drawNo, drawNo)

        def lottoWinShops = lottoWinShopRepository.findAll()

        def lotto1stWinShops = lottoWinShops.stream()
                .filter(s -> s.getRank() == 1)
                .sorted((s1, s2) -> { return s1.getNo() - s2.getNo() })
                .toList()

        def lotto2ndWinShops = lottoWinShops.stream()
                .filter(s -> s.getRank() == 2)
                .sorted((s1, s2) -> { return s1.getNo() - s2.getNo() })
                .toList()

        then:
        //1059 회차 당첨 판매점은 96(13 + 83)곳이다.
        lottoWinShops.size() == 96

        lotto1stWinShops.size() == 13
        lotto1stWinShops.get(0).getRank() == 1
        lotto1stWinShops.get(0).getNo() == 1
        lotto1stWinShops.get(0).getShop().getName() == "대박집"
        lotto1stWinShops.get(0).getLottoPurchaseType().getDescription() == "자동"
        lotto1stWinShops.get(0).getDisplayAddress() == "서울 광진구 능동로53길 41 1층"

        lotto2ndWinShops.size() == 83
        lotto2ndWinShops.get(0).getRank() == 2
        lotto2ndWinShops.get(0).getNo() == 1
        lotto2ndWinShops.get(0).getShop().getName() == "차오르다"
        lotto2ndWinShops.get(0).getDisplayAddress() == "서울 강서구 강서로54길 11 가동1층"
    }
}
