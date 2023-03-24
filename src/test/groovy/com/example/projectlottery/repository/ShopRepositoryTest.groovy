package com.example.projectlottery.repository

import com.example.projectlottery.IntegrationContainerBaseTest
import com.example.projectlottery.domain.Shop
import com.example.projectlottery.dto.ShopDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

import java.time.LocalDate
import java.time.LocalDateTime

@Transactional
class ShopRepositoryTest extends IntegrationContainerBaseTest {

    @Autowired
    private ShopRepository shopRepository

    private Shop shop

    def init() {
        Long id = 1L
        String address = "address"
        String name = "name"
        String tel = "tel"
        double longitude = 0
        double latitude = 0
        boolean l645YN = true
        boolean l720YN = false
        boolean spYN = false
        String state1 = "state1"
        String state2 = "state2"
        String state3 = "state3"
        LocalDate scrapedDt = LocalDate.now()

        shop = Shop.of(id, address, name, tel, longitude, latitude, l645YN, l720YN, spYN, state1, state2, state3, scrapedDt)
    }

    def "JPA Repository 테스트 - save"() {
        given:
        init()

        when:
        def result = shopRepository.save(shop)

        then:
        result.getId() == shop.getId()
        result.getAddress() == shop.getAddress()
        result.getName() == shop.getName()
        result.getTel() == shop.getTel()
        result.getLongitude() == shop.getLongitude()
        result.getLatitude() == shop.getLatitude()
        result.isL645YN() == shop.isL645YN()
        result.isL720YN() == shop.isL720YN()
        result.isSpYN() == shop.isSpYN()
        result.getState1() == shop.getState1()
        result.getState2() == shop.getState2()
        result.getState3() == shop.getState3()
        result.getScrapedDt() == shop.getScrapedDt()
    }

    def "JPA Repository 테스트 - findById"() {
        given:
        init()
        shopRepository.save(shop)

        when:
        def result = shopRepository.findById(shop.getId()).orElse(null)

        then:
        result.getId() == shop.getId()
        result.getAddress() == shop.getAddress()
        result.getName() == shop.getName()
        result.getTel() == shop.getTel()
        result.getLongitude() == shop.getLongitude()
        result.getLatitude() == shop.getLatitude()
        result.isL645YN() == shop.isL645YN()
        result.isL720YN() == shop.isL720YN()
        result.isSpYN() == shop.isSpYN()
        result.getState1() == shop.getState1()
        result.getState2() == shop.getState2()
        result.getState3() == shop.getState3()
        result.getScrapedDt() == shop.getScrapedDt()
    }

    def "JPA Repository 테스트 - findAll"() {
        given:
        init()

        when:
        shopRepository.save(shop)

        then:
        shopRepository.findAll().size() == 1;
    }

    def "JPA Repository 테스트 - delete"() {
        given:
        init()

        when:
        def result = shopRepository.save(shop)
        shopRepository.deleteAll()

        then:
        shopRepository.findAll().size() == 0;
    }

    def "JPA Auditing Fields 테스트"() {
        given:
        init()

        LocalDateTime now = LocalDateTime.now()

        when:
        shopRepository.save(shop)
        def result = shopRepository.findAll()

        then:
        result.get(0).getCreatedAt().isAfter(now)
        result.get(0).getModifiedAt().isAfter(now)
    }
}
