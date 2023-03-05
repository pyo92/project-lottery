package com.example.projectlottery.service;

import com.example.projectlottery.dto.ShopDto;
import com.example.projectlottery.dto.response.shop.ShopResponse;
import com.example.projectlottery.repository.ShopRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class ShopService {

    private final ShopRepository shopRepository;

    @Transactional(readOnly = true)
    public Optional<ShopDto> getShopById(Long id) {
        return shopRepository.findById(id).map(ShopDto::from);
    }

    @Transactional(readOnly = true)
    public Set<ShopDto> getShopByL645YNAndScrapedDt(boolean l645YN, LocalDate scrapedDt) {
        return shopRepository.findByL645YNAndScrapedDtBefore(l645YN, scrapedDt).stream()
                .map(ShopDto::from)
                .collect(Collectors.toUnmodifiableSet()); //순서가 중요하지 않으므로 set 반환
    }

    @Transactional(readOnly = true)
    public ShopResponse getShopResponse(Long id) {
        return shopRepository.findById(id)
                .map(ShopResponse::from)
                .orElseThrow(() -> new EntityNotFoundException("해당 회차가 없습니다. (id: " + id + ")"));
    }


    public void save(ShopDto dto) {
        shopRepository.save(dto.toEntity());
    }
}
