package com.example.projectlottery.repository;

import com.example.projectlottery.domain.Region;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RegionRepository extends JpaRepository<Region, String> {

    List<Region> findAllByParentRegIsNull();

    List<Region> findAllByParentReg(String parentReg);
}
