package com.example.projectlottery.service

import com.example.projectlottery.IntegrationContainerBaseTest
import com.example.projectlottery.api.service.ScrapLotteryShopService
import com.example.projectlottery.repository.ShopRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.beans.factory.annotation.Autowired

import java.time.LocalDate

class ShopServiceTest extends IntegrationContainerBaseTest {

    @Autowired
    private ScrapLotteryShopService scrapLotteryShopService

    @Autowired
    private ShopService shopService

    @Autowired
    private ShopRepository shopRepository

    @Autowired
    private RedisTemplateService redisTemplateService

    private static boolean isInitialized = false
    private static int testedCount = 0

    def setup() {
        if (!isInitialized) { //매번 스크랩핑하면 느리기 때문에 조치
            isInitialized = true

            //test container 는 빈 상태이므로 초기 데이터 세팅
            scrapLotteryShopService.scrapShopL645("SEJONG")
        }

        testedCount++
    }

    def cleanup() {
        //@Transactional 어노테이션을 사용해 rollback 할 수도 있지만,
        //테스트 케이스마다 스크랩핑을 통해 기초 데이터를 세팅하고 롤백하는 게 부하가 커 클래스 단위로 처리하도록 강제
        if (testedCount == 4) {  //매번 스크랩핑 데이터를 삭제하면 느리기 때문에 조치
            shopRepository.deleteAll()
        }
    }

    def "getShopById() - 정상 case"() {
        given:
        Long shopId = 13220001L

        when:
        def shop = shopService.getShopById(shopId).orElse(null)

        then:
        shop != null
        shop.id() == shopId
        shop.name() == "세종행복컨설팅"
        shop.address() == "세종 도움3로 105-8 신목빌딩 103호"
        shop.tel() == ""
        shop.longitude() == 127.249223136707D
        shop.latitude() == 36.5043390758359D
        shop.l645YN()
        !shop.l720YN()
        !shop.spYN()
        shop.state1() == "세종특별자치시"
        shop.state2() == ""
        shop.state3() == "종촌동"
        shop.scrapedDt() == LocalDate.now()
    }

    def "getShopById() - 실패 case"() {
        given:
        Long shopId = 0L

        when:
        def shop = shopService.getShopById(shopId).orElse(null)

        then:
        shop == null
    }

    def "getShopResponse() - 정상 case"() {
        given:
        Long shopId = 14410113L

        when:
        def shop = shopService.getShopResponse(shopId)

        then:
        shop != null
        shop.id() == shopId
        shop.name() == "대박슈퍼"
        shop.address() == "세종 행복10길 3 1층 101호"
        shop.tel() == ""
        shop.longitude() == 127.291208003234D
        shop.latitude() == 36.6035098877612D
        shop.l645YN()
        !shop.l720YN()
        !shop.spYN()
    }

    def "getShopResponse() - 실패 case"() {
        given:
        Long shopId = 0L

        when:
        shopService.getShopResponse(shopId)

        then:
        def e = thrown EntityNotFoundException
        e.message == "해당 판매점 없습니다. (id: " + shopId + ")"
    }
}
