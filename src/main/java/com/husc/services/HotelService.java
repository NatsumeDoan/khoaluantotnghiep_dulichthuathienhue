package com.husc.services;

import java.util.List;

import org.springframework.data.domain.Page;

import com.husc.models.Hotel;

public interface HotelService {
	List<Hotel> findByCategories(List<Long> categories);
	Page<Hotel> findPaginatedHome(int pageNo, int pageSize);
	Page<Hotel> searchHomeHotels(String keyword, int pageNo, int pageSize);
}
