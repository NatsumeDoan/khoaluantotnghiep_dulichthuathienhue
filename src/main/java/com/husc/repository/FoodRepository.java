package com.husc.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.husc.models.Food;

public interface FoodRepository extends JpaRepository<Food, Long>{
	@Query("select f from Food f where f.title like %?1%")
    Page<Food> searchProducts(String keyword, Pageable pageable);
}
