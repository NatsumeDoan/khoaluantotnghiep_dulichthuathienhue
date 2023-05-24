package com.husc.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.husc.models.Booking;
import com.husc.repository.BookingRepository;

@Service
public class BookingServiceImpl implements BookingService {
	
	@Autowired
	BookingRepository bookingRepository;

	@Override
	public Page<Booking> findPaginated(int pageNo, int pageSize) {
		Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
		return this.bookingRepository.findAll(pageable);
	}

	@Override
	public Page<Booking> searchBookings(String keyword, int pageNo, int pageSize) {
		Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
		 if(keyword.isEmpty()) {
	    	return this.bookingRepository.findAll(pageable);
	    }
	    else  return this.bookingRepository.searchProducts(keyword, pageable);
	}

}
