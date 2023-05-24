package com.husc.services;

import org.springframework.data.domain.Page;

import com.husc.models.Food;

public interface FoodService {
	Page<Food> findPaginated(int pageNo, int pageSize, String sortField, String sortDirection);
	Page<Food> searchFoods(String keyword, int pageNo, int pageSize, String sortField, String sortDirection);
	Page<Food> findPaginatedHome(int pageNo, int pageSize);
	Page<Food> searchHomeFoods(String keyword, int pageNo, int pageSize);
}
