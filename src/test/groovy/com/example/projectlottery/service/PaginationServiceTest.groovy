package com.example.projectlottery.service


import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@SpringBootTest
class PaginationServiceTest extends Specification {

    private PaginationService paginationService

    def setup() {
        paginationService = new PaginationService()
    }

    def "Paging 테스트"() {
        when:
        def actualResult = paginationService.getPagination(curPage, totalPages)

        then:
        exepectedResult == actualResult

        where: //getPagination() result 는 index 값으로 실제 페이지 번호 -1 임에 유의
        curPage | totalPages | exepectedResult
        1       | 1          | [0]
        1       | 2          | [0, 1]
        1       | 3          | [0, 1, 2]
        1       | 4          | [0, 1, 2, 3]
        1       | 5          | [0, 1, 2, 3, 4]
        1       | 5          | [0, 1, 2, 3, 4]
        3       | 5          | [0, 1, 2, 3, 4]
        4       | 5          | [0, 1, 2, 3, 4]
        5       | 7          | [2, 3, 4, 5, 6]
        8       | 9          | [4, 5, 6, 7, 8]
        9       | 9          | [4, 5, 6, 7, 8]
        10      | 20         | [8, 9, 10, 11, 12]
        1       | 100        | [0, 1, 2, 3, 4]
    }
}
