package com.example.projectlottery.service;

import com.example.projectlottery.domain.Region;
import com.example.projectlottery.repository.RegionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class RegionService {

    private final RegionRepository regionRepository;

    public List<String> getAllState1() {
        return regionRepository.findAllByParentRegIsNull().stream()
                .map(Region::getState1)
                .collect(Collectors.toList());
    }

    public List<String> getAllState2(String state1) {
        if (!StringUtils.hasText(state1))
            return List.of();

        String parentReg = regionRepository.findAllByParentRegIsNull().stream()
                .filter(region -> region.getState1().equals(state1))
                .findFirst()
                .get().getReg();

        return regionRepository.findAllByParentReg(parentReg).stream()
                .map(Region::getState2)
                .collect(Collectors.toList());
    }
}
