package com.example.projectpoi.repository

import com.example.projectpoi.IntegrationContainerBaseTest
import com.example.projectpoi.domain.Poi
import com.example.projectpoi.domain.PoiType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

@Transactional
class PoiRepositoryTest extends IntegrationContainerBaseTest {

    @Autowired
    private PoiRepository poiRepository

    def "JPA Repository 테스트 - save"() {
        given:
        PoiType poiType = PoiType.LOTTERY
        String address = "대구 달서구 대명천로 220"
        String name = "일등복권편의점"
        Double x = 128.536200
        Double y = 35.8422196

        def poi = Poi.builder()
                .poiType(poiType)
                .address(address)
                .name(name)
                .longitude(x)
                .latitude(y)
                .build()

        when:
        def save = poiRepository.save(poi)


        println poi.getId()

        then:
        save == poi
        save.getId() == poi.getId()
        save.getPoiType() == poi.getPoiType()
        save.getAddress() == poi.getAddress()
        save.getName() == poi.getName()
        save.getLongitude() == poi.getLongitude()
        save.getLatitude() == poi.getLatitude()
    }

    def "JPA Repository 테스트 - findAll"() {
        given:
        PoiType poiType = PoiType.LOTTERY
        String address = "대구 달서구 대명천로 220"
        String name = "일등복권편의점"
        Double x = 128.536200
        Double y = 35.8422196

        def poi = Poi.builder()
                .poiType(poiType)
                .address(address)
                .name(name)
                .longitude(x)
                .latitude(y)
                .build()

        when:
        poiRepository.save(poi)

        println poi.getId()

        then:
        poiRepository.findAll().size() == 1;
    }

    def "JPA Repository 테스트 - update"() {
        given:
        PoiType poiType = PoiType.LOTTERY
        String address = "대구 달서구 대명천로 220"
        String name = "일등복권편의점"
        Double x = 128.536200
        Double y = 35.8422196

        def poi = Poi.builder()
                .poiType(poiType)
                .address(address)
                .name(name)
                .longitude(x)
                .latitude(y)
                .build()

        poiRepository.save(poi)


        println poi.getId()
        when:
        poi.name += " new"
        poiRepository.flush()

        then:
        def find = poiRepository.findById(poi.id).orElseThrow()
        find.name == "일등복권편의점 new"
    }
}
