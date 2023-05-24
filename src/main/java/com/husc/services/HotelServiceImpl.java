package com.husc.services;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.husc.models.Category;
import com.husc.models.Hotel;
import com.husc.repository.CategoryRepository;
import com.husc.repository.HotelRepository;

@Service
public class HotelServiceImpl implements HotelService {
	@Autowired
	HotelRepository hotelRepository;
	
	@Autowired
	CategoryRepository categoryRepository;
	
	@Override
	public List<Hotel> findByCategories(List<Long> categories) {
		 List<Hotel> hotels = hotelRepository.findAll();
		 Set<Category> category = new HashSet<>();
			for (Long c_id : categories) {
				category.add(categoryRepository.findById(c_id).get());
			}
		 
		 return hotels.stream()
			        .filter(hotel -> hotel.getCategories().containsAll(category))
			        .collect(Collectors.toList());
	}

	@Override
	public Page<Hotel> findPaginatedHome(int pageNo, int pageSize) {
		Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
		return this.hotelRepository.findAll(pageable);
	}

	@Override
	public Page<Hotel> searchHomeHotels(String keyword, int pageNo, int pageSize) {
		Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
		 if(keyword.isEmpty()) {
	    	return this.hotelRepository.findAll(pageable);
	    }
	    else  return this.hotelRepository.searchhotels(keyword, pageable);
	}

}
