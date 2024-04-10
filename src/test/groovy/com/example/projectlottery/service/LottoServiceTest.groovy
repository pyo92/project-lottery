package com.example.projectlottery.service

import com.example.projectlottery.IntegrationContainerBaseTest
import com.example.projectlottery.api.service.ScrapLotteryWinService
import com.example.projectlottery.api.service.ScrapLotteryWinShopService
import com.example.projectlottery.repository.LottoPrizeRepository
import com.example.projectlottery.repository.LottoRepository
import com.example.projectlottery.repository.LottoWinShopRepository
import com.example.projectlottery.repository.ShopRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.beans.factory.annotation.Autowired

import java.time.LocalDate

class LottoServiceTest extends IntegrationContainerBaseTest {

    @Autowired
    private ScrapLotteryWinService scrapLotteryWinService
    @Autowired
    private ScrapLotteryWinShopService scrapLotteryWinShopService

    @Autowired
    private LottoService lottoService

    @Autowired
    private LottoRepository lottoRepository
    @Autowired
    private LottoPrizeRepository lottoPrizeRepository
    @Autowired
    private LottoWinShopRepository lottoWinShopRepository
    @Autowired
    private ShopRepository shopRepository

    @Autowired
    private RedisTemplateService redisTemplateService

    private static boolean isInitialized = false
    private static int testedCount = 0

    def setup() {
        if (!isInitialized) {
            //test container 는 빈 상태이므로 초기 데이터 세팅
            scrapLotteryWinService.getResultsL645(1L, 1L)
            scrapLotteryWinShopService.scrapWinShopL645(1L, 1L)
        }

        testedCount++
    }

    def cleanup() {
        //getLatestDrawNo() 테스트를 위해 redis cache 초기화
        redisTemplateService.deleteLatestDrawNo()

        //@Transactional 어노테이션을 사용해 rollback 할 수도 있지만,
        //테스트 케이스마다 스크랩핑을 통해 기초 데이터를 세팅하고 롤백하는 게 부하가 커 클래스 단위로 처리하도록 강제
        if (testedCount == 6) {
            lottoWinShopRepository.deleteAll()
            lottoPrizeRepository.deleteAll()
            lottoRepository.deleteAll()

            shopRepository.deleteAll()
        }
    }

    def "getLotto() - 정상 case"() {
        given:
        Long drawNo = 1L

        when:
        def lotto = lottoService.getLotto(drawNo)

        then:
        lotto.drawNo() == drawNo
        lotto.drawDt() == LocalDate.of(2002, 12, 7)
        lotto.number1() == 10
        lotto.number2() == 23
        lotto.number3() == 29
        lotto.number4() == 33
        lotto.number5() == 37
        lotto.number6() == 40
        lotto.numberB() == 16

    }

    def "getLotto() - 실패 case"() {
        given:
        Long drawNo = 2L

        when:
        lottoService.getLotto(drawNo)

        then:
        def e = thrown EntityNotFoundException
        e.message == "해당 회차가 없습니다. (drawNo: " + drawNo + ")"
    }

    def "getLatestDrawNo() - 정상 case"() {
        given:
        Long expectedResult = 1L

        when:
        def latestDrawNo = lottoService.getLatestDrawNo()

        then:
        latestDrawNo == expectedResult
    }

    def "getLatestDrawNo() - 추가 회차 스크랩핑 case"() {
        given:
        Long expectedResult = 1059L

        //매주 추가 회차 스크랩핑 후 잘 가져오는지 테스트하기 위해 새로운 회차 스크랩핑
        scrapLotteryWinService.getResultsL645(expectedResult, expectedResult)

        when:
        def latestDrawNo = lottoService.getLatestDrawNo()

        then:
        latestDrawNo == expectedResult
    }

    def "getLottoResponse() - 정상 case"() {
        given:
        Long drawNo = 1L

        when:
        def lotto = lottoService.getLottoResponse(drawNo)
        def lottoPrizes = lotto.lottoPrizes()

        then:
        lotto.drawNo() == drawNo
        lotto.drawDt() == LocalDate.of(2002, 12, 7).toString()
        lotto.number1() == 10
        lotto.number2() == 23
        lotto.number3() == 29
        lotto.number4() == 33
        lotto.number5() == 37
        lotto.number6() == 40
        lotto.bonus() == 16

        lottoPrizes.size() == 5
        //1등 체크
        lottoPrizes.get(0).rank() == 1
        lottoPrizes.get(0).winAmount() == 0
        lottoPrizes.get(0).winGameCount() == 0
        lottoPrizes.get(0).winAmountPerGame() == 0
        //2등 체크
        lottoPrizes.get(1).rank() == 2
        lottoPrizes.get(1).winAmount() == 143934100
        lottoPrizes.get(1).winGameCount() == 1
        lottoPrizes.get(1).winAmountPerGame() == 143934100
        //3등 체크
        lottoPrizes.get(2).rank() == 3
        lottoPrizes.get(2).winAmount() == 143934000
        lottoPrizes.get(2).winGameCount() == 28
        lottoPrizes.get(2).winAmountPerGame() == 5140500
        //4등 체크
        lottoPrizes.get(3).rank() == 4
        lottoPrizes.get(3).winAmount() == 287695800
        lottoPrizes.get(3).winGameCount() == 2537
        lottoPrizes.get(3).winAmountPerGame() == 113400
        //5등 체크
        lottoPrizes.get(4).rank() == 5
        lottoPrizes.get(4).winAmount() == 401550000
        lottoPrizes.get(4).winGameCount() == 40155
        lottoPrizes.get(4).winAmountPerGame() == 10000
    }

    def "getLottoResponse() - 당첨 판매점 목록 비교(1회 vs 1059회)"() {
        given:
        //262회 이후 당첨 판매점이 정상적으로 제공되는지 확인하기 위해 새로운 회차 스크랩핑
        scrapLotteryWinService.getResultsL645(1059L, 1059L)
        scrapLotteryWinShopService.scrapWinShopL645(1059L, 1059L)

        when:
        def lotto1 = lottoService.getLottoResponse(1L)
        def lotto1059 = lottoService.getLottoResponse(1059L)

        then:
        lotto1.drawNo() == 1L
        lotto1.lotto1stWinShops().size() == 0 //262회차 이전에는 제공되지 않는 데이터
        lotto1.lotto2ndWinShops().size() == 0 //262회차 이전에는 제공되지 않는 데이터

        lotto1059.drawNo() == 1059L
        lotto1059.lotto1stWinShops().size() > 0
        lotto1059.lotto2ndWinShops().size() > 0

    }
}
