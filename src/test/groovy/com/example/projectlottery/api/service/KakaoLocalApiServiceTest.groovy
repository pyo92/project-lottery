package com.example.projectlottery.api.service

import com.example.projectlottery.IntegrationContainerBaseTest
import org.springframework.beans.factory.annotation.Autowired

class KakaoLocalApiServiceTest extends IntegrationContainerBaseTest {

    @Autowired
    private KakaoLocalApiService kakaoLocalApiService

    def "address 값이 null 이면, requestSearchAddress() 메서드는 null 값을 반환한다."() {
        given:
        String address = null

        when:
        def result = kakaoLocalApiService.requestSearchAddress(address)

        then:
        result == null
    }

    def "address 값이 정상이면, requestSearchAddress() 메서드는 meta 와 documents 데이터를 반환한다."() {
        given:
        String address = "경기 용인시 수지구 상현로 6"

        when:
        def result = kakaoLocalApiService.requestSearchAddress(address)

        then:
        result.getMeta().getTotalCount() > 0
        result.getDocs().size() > 0
    }

    def "정상적인 주소를 입력했을 경우, 정상적으로 위도, 경도로 변환된다."() {
        given:
        boolean actualResult = false

        when:
        def searchResult = kakaoLocalApiService.requestSearchAddress(address)

        then:
        if(searchResult == null) actualResult = false
        else actualResult = searchResult.getDocs().size() > 0

        actualResult == expectedResult

        where:
        address | expectedResult
        "서울 영등포구 신길동 323" | true
        "서울 강동구 명일동 334-2" | true
        "서울 중구 퇴계로6길 5" | true
        "서울 성동구 왕십리로 362" | true
        "경남 밀양시 중앙로 52" | true
        "제주 제주시 일도이동 48-2" | true
        "광진구 구의동 251-455555" | false
        "" | false
    }
}
