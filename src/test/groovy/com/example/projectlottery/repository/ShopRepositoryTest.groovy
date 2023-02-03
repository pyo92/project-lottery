package com.example.projectlottery.repository

import com.example.projectlottery.IntegrationContainerBaseTest
import com.example.projectlottery.domain.Shop
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

@Transactional
class ShopRepositoryTest extends IntegrationContainerBaseTest {

    @Autowired
    private ShopRepository shopRepository

    def "JPA Repository 테스트 - save"() {
        given:
        String sido = "경기도"
        String sigungu = "용인시 수지구"
        String roadAddress = "경기 용인시 수지구 광교중앙로 297"
        String address = "경기 용인시 수지구 상현동 1117-8"
        String name = "와이케이복권방"
        Double x = 127.068591
        Double y = 37.2966947

        def shop = Shop.builder()
                .sido(sido)
                .sigungu(sigungu)
                .roadAddress(roadAddress)
                .address(address)
                .name(name)
                .longitude(x)
                .latitude(y)
                .build()

        when:
        def result = shopRepository.save(shop)

        then:
        result == shop
        result.getId() == shop.getId()
        result.getSido() == shop.getSido()
        result.getSigungu() == shop.getSigungu()
        result.getRoadAddress() == shop.getRoadAddress()
        result.getAddress() == shop.getAddress()
        result.getName() == shop.getName()
        result.getLongitude() == shop.getLongitude()
        result.getLatitude() == shop.getLatitude()
    }

    def "JPA Repository 테스트 - findAll"() {
        given:
        String sido = "경기도"
        String sigungu = "용인시 수지구"
        String roadAddress = "경기 용인시 수지구 광교중앙로 297"
        String address = "경기 용인시 수지구 상현동 1117-8"
        String name = "와이케이복권방"
        Double x = 127.068591
        Double y = 37.2966947

        def shop = Shop.builder()
                .sido(sido)
                .sigungu(sigungu)
                .roadAddress(roadAddress)
                .address(address)
                .name(name)
                .longitude(x)
                .latitude(y)
                .build()

        when:
        shopRepository.save(shop)

        then:
        shopRepository.findAll().size() == 1;
    }

    def "JPA Repository 테스트 - update"() {
        given:
        String sido = "경기도"
        String sigungu = "용인시 수지구"
        String roadAddress = "경기 용인시 수지구 광교중앙로 297"
        String address = "경기 용인시 수지구 상현동 1117-8"
        String name = "와이케이복권방"
        Double x = 127.068591
        Double y = 37.2966947

        def shop = Shop.builder()
                .sido(sido)
                .sigungu(sigungu)
                .roadAddress(roadAddress)
                .address(address)
                .name(name)
                .longitude(x)
                .latitude(y)
                .build()

        shopRepository.save(shop)

        when:
        shop.name += " new"
        shopRepository.flush()

        then:
        def result = shopRepository.findById(shop.getId()).orElseThrow()
        result.name == "와이케이복권방 new"
    }
}
