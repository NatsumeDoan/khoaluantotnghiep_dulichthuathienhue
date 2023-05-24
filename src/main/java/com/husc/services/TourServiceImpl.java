package com.husc.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.husc.models.Tour;
import com.husc.repository.TourRepository;

@Service
public class TourServiceImpl implements TourService{

	@Autowired
	TourRepository tourRepository;
	
	@Override
	public Page<Tour> findPaginated(int pageNo, int pageSize, String sortField, String sortDirection) {
		Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
			Sort.by(sortField).descending();
		
		Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
		return this.tourRepository.findAll(pageable);
	}

	@Override
	public Page<Tour> searchTours(String keyword, int pageNo, int pageSize, String sortField, String sortDirection) {
		Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
			Sort.by(sortField).descending();
		Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
		 if(keyword.isEmpty()) {
	    	return this.tourRepository.findAll(pageable);
	    }
	    else  return this.tourRepository.searchProducts(keyword, pageable);
	}

	@Override
	public Page<Tour> findPaginatedHome(int pageNo, int pageSize) {
		Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
		return this.tourRepository.findAll(pageable);
	}

	@Override
	public Page<Tour> searchHomeTours(String keyword, int pageNo, int pageSize) {
		Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
		 if(keyword.isEmpty()) {
	    	return this.tourRepository.findAll(pageable);
	    }
	    else  return this.tourRepository.searchProducts(keyword, pageable);
	}
}
