package com.example.projectlottery.service

import com.example.projectlottery.IntegrationContainerBaseTest
import com.example.projectlottery.domain.Region
import com.example.projectlottery.repository.RegionRepository
import org.springframework.beans.factory.annotation.Autowired

class RegionServiceTest extends IntegrationContainerBaseTest {

    @Autowired
    private RegionService regionService

    @Autowired
    private RegionRepository regionRepository //테스트 데이터 세팅용 repository

    def setup() {
        Region parent1 = Region.of("11000", "서울특별시", null, null)
        Region parent1_child1 = Region.of("11110", "서울특별시", "종로구", "11000")
        Region parent1_child2 = Region.of("11140", "서울특별시", "중구", "11000")
        Region parent1_child3 = Region.of("11170", "서울특별시", "용산구", "11000")
        Region parent1_child4 = Region.of("11200", "서울특별시", "성동구", "11000")

        Region parent2 = Region.of("26000", "부산광역시", null, null)

        regionRepository.save(parent1);
        regionRepository.save(parent1_child1);
        regionRepository.save(parent1_child2);
        regionRepository.save(parent1_child3);
        regionRepository.save(parent1_child4);

        regionRepository.save(parent2);
    }

    def "getAllState1()"() {
        when:
        def states = regionService.getAllState1()

        then:
        states.size() == 2
        states.contains("서울특별시")
        states.contains("부산광역시")
    }

    def "getAllState2() - child 존재 case"() {
        given:
        String state1 = "서울특별시"

        when:
        def states = regionService.getAllState2(state1)

        then:
        states.size() == 4
        states.contains("종로구")
        states.contains("중구")
        states.contains("용산구")
        states.contains("성동구")
    }

    def "getAllState2() - child 미존재 case"() {
        given:
        String state1 = "부산광역시"

        when:
        def states = regionService.getAllState2(state1)

        then:
        states.size() == 0
    }
}
