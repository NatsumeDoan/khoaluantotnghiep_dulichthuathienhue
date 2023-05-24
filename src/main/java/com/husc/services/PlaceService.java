package com.husc.services;

import org.springframework.data.domain.Page;

import com.husc.models.Place;

public interface PlaceService {
	Page<Place> findPaginated(int pageNo, int pageSize, String sortField, String sortDirection);
	Page<Place> searchPlaces(String keyword, int pageNo, int pageSize, String sortField, String sortDirection);
	Page<Place> findPaginatedHome(int pageNo, int pageSize);
	Page<Place> searchHomePlaces(String keyword, int pageNo, int pageSize);
}
