package com.example.projectpoi.api.service

import com.example.projectpoi.IntegrationContainerBaseTest
import org.springframework.beans.factory.annotation.Autowired

class KakaoSearchAddressServiceTest extends IntegrationContainerBaseTest {

    @Autowired
    private KakaoSearchAddressService kakaoSearchAddressService

    def "address 값이 null 이면, requestSearchAddress() 메서드는 null 값을 반환한다."() {
        given:
        String address = null

        when:
        def result = kakaoSearchAddressService.requestSearchAddress(address)

        then:
        result == null
    }

    def "address 값이 정상이면, requestSearchAddress() 메서드는 meta 와 documents 데이터를 반환한다."() {
        given:
        String address = "경기 용인시 수지구 상현로 6"

        when:
        def result = kakaoSearchAddressService.requestSearchAddress(address)

        then:
        result.getMetaDto().getTotalCount() > 0
        result.getDocumentList().size() > 0
    }
}
