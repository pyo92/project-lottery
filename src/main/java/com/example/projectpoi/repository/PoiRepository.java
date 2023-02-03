package com.example.projectpoi.repository;

import com.example.projectpoi.domain.Poi;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PoiRepository extends JpaRepository<Poi, Long> {
}
