package com.husc.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.husc.models.Place;
import com.husc.repository.PlaceRepository;

@Service
public class PlaceServiceImpl implements PlaceService{
	@Autowired
	PlaceRepository placeRepository;
	
	@Override
	public Page<Place> findPaginated(int pageNo, int pageSize, String sortField, String sortDirection) {
		Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
			Sort.by(sortField).descending();
		
		Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
		return this.placeRepository.findAll(pageable);
	}

	@Override
	public Page<Place> searchPlaces(String keyword, int pageNo, int pageSize, String sortField, String sortDirection) {
		Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
			Sort.by(sortField).descending();
		Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
		 if(keyword.isEmpty()) {
	    	return this.placeRepository.findAll(pageable);
	    }
	    else  return this.placeRepository.searchProducts(keyword, pageable);
	}

	@Override
	public Page<Place> findPaginatedHome(int pageNo, int pageSize) {
		Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
		return this.placeRepository.findAll(pageable);
	}

	@Override
	public Page<Place> searchHomePlaces(String keyword, int pageNo, int pageSize) {
		Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
		 if(keyword.isEmpty()) {
	    	return this.placeRepository.findAll(pageable);
	    }
	    else  return this.placeRepository.searchProducts(keyword, pageable);
	}

}
