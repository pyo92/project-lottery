package com.example.projectlottery.service

import com.example.projectlottery.IntegrationContainerBaseTest
import com.example.projectlottery.dto.response.LottoResponse
import com.example.projectlottery.dto.response.ShopResponse
import com.example.projectlottery.dto.response.querydsl.QShopSummary
import org.springframework.beans.factory.annotation.Autowired

class RedisTemplateServiceTest extends IntegrationContainerBaseTest {

    @Autowired
    private RedisTemplateService redisTemplateService

    def cleanup() {
        //redis cache -> 테스트 케이스마다 정리
        redisTemplateService.deleteLatestDrawNo()
        redisTemplateService.deleteAllShopDetail()
        redisTemplateService.deleteAllShopRanking()
    }

    def "saveLatestDrawNo() - success"() {
        when:
        redisTemplateService.saveLatestDrawNo(drawNo)
        def actual = redisTemplateService.getLatestDrawNo()

        then:
        actual == expected

        where:
        drawNo | expected
        1L     | 1L
        2L     | 2L
        3L     | 3L
        4L     | 4L
        5L     | 5L
        1055L  | 1055L
        1056L  | 1056L
        1057L  | 1057L
        1058L  | 1058L
        1059L  | 1059L
    }

    def "saveLatestDrawNo() - fail"() {
        given:
        Long expected = null

        when:
        redisTemplateService.saveLatestDrawNo(expected)
        def actual = redisTemplateService.getLatestDrawNo()

        then:
        actual == null
    }

    def "deleteLatestDrawNo()"() {
        given:
        Long expected = 1059L
        redisTemplateService.saveLatestDrawNo(expected)

        when:
        def beforeDelete = redisTemplateService.getLatestDrawNo()
        redisTemplateService.deleteLatestDrawNo()
        def afterDelete = redisTemplateService.deleteLatestDrawNo()

        then:
        beforeDelete != afterDelete
        beforeDelete == expected
        afterDelete == null
    }

    def "saveWinDetail() - success"() {
        given:
        LottoResponse expected = LottoResponse.of(drawNo, drawDt, number1, number2, number3, number4, number5, number6, bonus, List.of(), List.of(), List.of())

        when:
        redisTemplateService.saveWinDetail(expected)
        def actual = redisTemplateService.getWinDetail(drawNo)

        then:
        expected == actual
        actual.drawNo() == drawNo
        actual.drawDt() == drawDt
        actual.number1() == number1
        actual.number2() == number2
        actual.number3() == number3
        actual.number4() == number4
        actual.number5() == number5
        actual.number6() == number6
        actual.bonus() == bonus

        where:
        drawNo | drawDt       | number1 | number2 | number3 | number4 | number5 | number6 | bonus
        1L     | "2002-12-07" | 10      | 23      | 29      | 33      | 37      | 40      | 16
        2L     | "2002-12-14" | 9       | 13      | 21      | 25      | 32      | 42      | 2
        3L     | "2002-12-21" | 11      | 16      | 19      | 21      | 27      | 31      | 30
        4L     | "2002-12-28" | 14      | 27      | 30      | 31      | 40      | 42      | 2
        5L     | "2003-01-04" | 16      | 24      | 29      | 40      | 41      | 42      | 3
        1055L  | "2023-02-18" | 4       | 7       | 12      | 14      | 22      | 33      | 31
        1056L  | "2023-02-25" | 13      | 20      | 24      | 32      | 36      | 45      | 29
        1057L  | "2023-03-04" | 8       | 13      | 19      | 27      | 40      | 45      | 12
        1058L  | "2023-03-11" | 11      | 23      | 25      | 30      | 32      | 40      | 42
        1059L  | "2023-03-18" | 7       | 10      | 22      | 25      | 34      | 40      | 27

    }

    def "saveWinDetail() - fail"() {
        given:
        Long drawNo = null
        LottoResponse expected = LottoResponse.of(drawNo, "", 0, 0, 0, 0, 0, 0, 0, List.of(), List.of(), List.of())

        when:
        redisTemplateService.saveWinDetail(expected)
        def actual = redisTemplateService.getWinDetail(drawNo)

        then:
        actual == null
    }

    def "saveShopDetail() - success"() {
        given:
        ShopResponse expected = ShopResponse.of(id, name, address, tel, longitude, latitude, l645YN, l720YN, spYN, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, List.of(), List.of())

        when:
        redisTemplateService.saveShopDetail(expected)
        def actual = redisTemplateService.getShopDetail(id)

        then:
        expected == actual
        actual.id() == id
        actual.name() == name
        actual.address() == address
        actual.tel() == tel
        actual.longitude() == longitude
        actual.latitude() == latitude
        actual.l645YN() == l645YN
        actual.l720YN() == l720YN
        actual.spYN() == spYN

        where:
        id        | name          | address                              | tel           | longitude         | latitude          | l645YN | l720YN | spYN
        11100004L | "한보당"         | "서울 영등포구 신길동 323"                    | ""            | 126.906824144642D | 37.5060732105061D | false  | false  | false
        11100016L | "아이센스복권방"     | "서울 중랑구 중랑역로 17,(중화동)"               | "02-436-6473" | 127.076041492899D | 37.5957274092098D | true   | false  | false
        11100018L | "공공일안경원"      | "서울 강동구 명일동 334-2"                   | ""            | 127.143961445024D | 37.549383275279D  | false  | false  | false
        11100021L | "꽃길복권방"       | "서울 강북구 덕릉로 140"                     | ""            | 127.029438273509D | 37.6349642960559D | true   | false  | false
        11100022L | "다모아"         | "서울 구로구 공원로6나길 39-1"                 | "02-830-3330" | 126.89179540724D  | 37.5044721086261D | true   | false  | false
        24900014L | "GS25(제주일도)"  | "제주 제주시 일도이동 48-2"                   | ""            | 126.542478861954D | 33.5034282061783D | false  | false  | false
        24900031L | "GS25(제주탑동)"  | "제주 제주시 건입동 1417"                    | ""            | 126.525007819863D | 33.5163717825227D | false  | false  | false
        24900032L | "GS25(서사라)"   | "제주 제주시 삼도1동 531-1"                  | ""            | 126.519667287923D | 33.5034942383555D | false  | false  | false
        24920001L | "GS25(노형대림점)" | "제주 제주시 노형동 2511-05 성림미래안아파트 1층 102" | ""            | 126.470197420663D | 33.4840446170211D | false  | false  | false
        51100000L | "인터넷판매(동행복권)" | "동행복권(dhlottery.co.kr)"              | ""            | 127.015785D       | 37.482063D        | true   | false  | false
    }

    def "saveShopDetail() - fail"() {
        given:
        Long id = null
        ShopResponse expected = ShopResponse.of(id, "", "", "", 0D, 0D, false, false, false, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, List.of(), List.of())

        when:
        redisTemplateService.saveShopDetail(expected)
        def actual = redisTemplateService.getShopDetail(id)

        then:
        actual == null
    }

    def "deleteAllShopDetail()"() {
        given:
        Long id = 51100000L
        String name = "인터넷판매(동행복권)"
        String address = "동행복권(dhlottery.co.kr)"
        String tel = ""
        double longitude = 127.015785D
        double latitude = 37.482063D
        boolean l645YN = true
        boolean l720YN = false
        boolean spYN = false
        ShopResponse expected = ShopResponse.of(id, name, address, tel, longitude, latitude, l645YN, l720YN, spYN, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, List.of(), List.of())
        redisTemplateService.saveShopDetail(expected)

        when:
        def beforeDelete = redisTemplateService.getShopDetail(id)
        redisTemplateService.deleteAllShopDetail()
        def afterDelete = redisTemplateService.getShopDetail(id)

        then:
        beforeDelete != afterDelete
        beforeDelete == expected
        afterDelete == null
    }

    def "saveShopRanking() - success"() {
        given:
        List<QShopSummary> expected = new ArrayList<>()
        String name = ""
        String address = ""
        String lottoPurchaseType = ""
        expected.add(new QShopSummary(11100001L, name, address, lottoPurchaseType, 0, 0))
        expected.add(new QShopSummary(11100002L, name, address, lottoPurchaseType, 1, 1))
        expected.add(new QShopSummary(11100003L, name, address, lottoPurchaseType, 52, 0))
        expected.add(new QShopSummary(11100004L, name, address, lottoPurchaseType, 52, 305))

        QShopSummary target = new QShopSummary(11100005L, name, address, lottoPurchaseType, count1stWin, count2ndWin)
        expected.add(target)

        //기존 redis 로직에서 정렬 조건처럼 가중치를 먹여서 정렬
        expected = expected.sort((s1, s2) -> {
            double score1 = s1.firstPrizeWinCount() * 100000000D + s1.secondPrizeWinCount() * 10000D - s1.id() / 100000000D;
            double score2 = s2.firstPrizeWinCount() * 100000000D + s2.secondPrizeWinCount() * 10000D - s2.id() / 100000000D;

            if (score2 > score1) return 1
            else if (score2 < score1) return -1
            else return 0
        })

        when:
        redisTemplateService.saveShopRanking(expected)
        def actual = redisTemplateService.getAllShopRanking()

        then:
        expected == actual
        actual.get(expectedRank - 1) == target

        where:
        id | count1stWin | count2ndWin | expectedRank
        1L | 0           | 0           | 5
        1L | 0           | 1           | 4
        1L | 1           | 0           | 4
        1L | 1           | 999         | 3
        1L | 52          | 304         | 2
        1L | 52          | 307         | 1
    }

    def "saveShopRanking() - fail"() {
        given:
        Long id = null
        QShopSummary target = new QShopSummary(id, "", "", "", 0L, 0L)

        when:
        redisTemplateService.saveShopRanking(List.of(target))
        def actual = redisTemplateService.getAllShopRanking()

        then:
        actual.size() == 0
    }

    def "deleteAllShopRanking()"() {
        given:
        List<QShopSummary> expected = new ArrayList<>()
        String name = ""
        String address = ""
        String lottoPurchaseType = ""
        expected.add(new QShopSummary(11100001L, name, address, lottoPurchaseType, 0, 0))
        expected.add(new QShopSummary(11100002L, name, address, lottoPurchaseType, 1, 1))
        expected.add(new QShopSummary(11100003L, name, address, lottoPurchaseType, 52, 0))
        expected.add(new QShopSummary(11100003L, name, address, lottoPurchaseType, 52, 305))

        redisTemplateService.saveShopRanking(expected)

        when:
        def beforeDelete = redisTemplateService.getAllShopRanking()
        redisTemplateService.deleteAllShopRanking()
        def afterDelete = redisTemplateService.getAllShopRanking()

        then:
        beforeDelete != afterDelete
        beforeDelete.size() == 4
        afterDelete.size() == 0
    }
}
