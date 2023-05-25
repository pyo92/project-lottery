package com.example.projectlottery.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.IntStream;

@Service
public class PaginationService {

    private static final int MAX_PRINT_LENGTH = 5; //최대 표시 페이지 수

    /**
     * pagination 목록 계산 (for 로또판매점 검색 view)
     * @param curPage 현재 페이지 번호
     * @param totalPages 전체 페이지 번호
     * @return pagination
     */
    public List<Integer> getPagination(int curPage, int totalPages) {
        int start = Math.max(curPage - MAX_PRINT_LENGTH / 2, 0);
        int end = Math.min(start + MAX_PRINT_LENGTH, totalPages);

        start = start > end - MAX_PRINT_LENGTH ? Math.max(end - MAX_PRINT_LENGTH, 0) : start;

        return IntStream.range(start, end).boxed().toList();
    }
}
