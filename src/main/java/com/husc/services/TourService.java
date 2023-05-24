package com.husc.services;

import org.springframework.data.domain.Page;

import com.husc.models.Tour;

public interface TourService{
	Page<Tour> findPaginated(int pageNo, int pageSize, String sortField, String sortDirection);
	Page<Tour> searchTours(String keyword, int pageNo, int pageSize, String sortField, String sortDirection);

	Page<Tour> findPaginatedHome(int pageNo, int pageSize);
	Page<Tour> searchHomeTours(String keyword, int pageNo, int pageSize);
}