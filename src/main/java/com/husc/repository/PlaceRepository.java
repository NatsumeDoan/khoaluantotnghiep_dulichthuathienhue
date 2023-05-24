package com.husc.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.husc.models.Place;

public interface PlaceRepository extends JpaRepository<Place, Long> {
	@Query("select p from Place p where p.title like %?1%")
    Page<Place> searchProducts(String keyword, Pageable pageable);
}