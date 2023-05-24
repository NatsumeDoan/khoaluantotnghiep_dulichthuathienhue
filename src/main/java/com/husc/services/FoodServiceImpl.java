package com.husc.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.husc.models.Food;
import com.husc.repository.FoodRepository;

@Service
public class FoodServiceImpl implements FoodService{
	@Autowired
	FoodRepository foodRepository;
	
	@Override
	public Page<Food> findPaginated(int pageNo, int pageSize, String sortField, String sortDirection) {
		Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
			Sort.by(sortField).descending();
		
		Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
		return this.foodRepository.findAll(pageable);
	}

	@Override
	public Page<Food> searchFoods(String keyword, int pageNo, int pageSize, String sortField, String sortDirection) {
		Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
			Sort.by(sortField).descending();
		Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
		 if(keyword.isEmpty()) {
	    	return this.foodRepository.findAll(pageable);
	    }
	    else  return this.foodRepository.searchProducts(keyword, pageable);
	}

	@Override
	public Page<Food> findPaginatedHome(int pageNo, int pageSize) {
		Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
		return this.foodRepository.findAll(pageable);
	}

	@Override
	public Page<Food> searchHomeFoods(String keyword, int pageNo, int pageSize) {
		Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
		 if(keyword.isEmpty()) {
	    	return this.foodRepository.findAll(pageable);
	    }
	    else  return this.foodRepository.searchProducts(keyword, pageable);
	}

}
