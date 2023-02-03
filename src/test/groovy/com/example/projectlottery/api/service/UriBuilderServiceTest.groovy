package com.example.projectlottery.api.service


import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

import java.nio.charset.StandardCharsets

@SpringBootTest
class UriBuilderServiceTest extends Specification {

    private UriBuilderService uriBuilderService

    def setup() {
        uriBuilderService = new UriBuilderService()
    }

    def "URL 생성 테스트 - 한글 파라미터 인코딩 정상 case"() {
        given:
        String address = "경기 용인시 수지구"

        when:
        def uri = uriBuilderService.buildUriByAddress(address)
        def decoded = URLDecoder.decode(uri.toString(), StandardCharsets.UTF_8)

        then:
        decoded == "https://dapi.kakao.com/v2/local/search/address.json?query=" + address
    }
}
