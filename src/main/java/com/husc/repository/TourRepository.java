package com.husc.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.husc.models.Tour;

public interface TourRepository extends JpaRepository<Tour, Long>{
	@Query("select f from Tour f where f.description like %?1% or f.title like %?1%")
    Page<Tour> searchProducts(String keyword, Pageable pageable);
	Tour findByTourCode(String tourCode);
}
